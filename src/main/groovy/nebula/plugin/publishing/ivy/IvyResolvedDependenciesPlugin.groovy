/*
 * Copyright 2015-2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nebula.plugin.publishing.ivy

import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionComparator
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.DefaultVersionSelectorScheme
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ExactVersionSelector
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.publish.ivy.IvyPublication

/**
 * Replaces first order dependencies with the selected versions when publishing.
 */
class IvyResolvedDependenciesPlugin extends AbstractResolvedDependenciesPlugin {
    @Override
    void apply(Project project) {
        project.plugins.apply IvyBasePublishPlugin

        project.publishing {
            publications {
                withType(IvyPublication) {
                    descriptor.withXml { XmlProvider xml ->
                        project.plugins.withType(JavaBasePlugin) {
                            def dependencies = xml.asNode()?.dependencies?.dependency
                            dependencies?.each { Node dep ->
                                String scope = dep.@conf
                                String group = dep.@org
                                String name = dep.@name

                                if (scope == 'compile->default') {
                                    scope = 'compile'
                                }

                                if (scope == 'provided->default' || scope == 'runtime->default') {
                                    scope = 'runtime'
                                }

                                if (scope == 'test->default') {
                                    scope = 'test'
                                }

                                if (scope.contains('->')) {
                                    scope = scope.split('->')[0]
                                }

                                def mvid = selectedModuleVersion(project, scope, group, name)
                                if (!mvid) {
                                    return  // continue loop if a dependency is not found in dependencyMap
                                }

                                if (dep.@rev) {
                                    def version = dep.@rev as String
                                    def scheme = new DefaultVersionSelectorScheme(new DefaultVersionComparator())
                                    def selector = scheme.parseSelector(version)
                                    if (!(selector instanceof ExactVersionSelector)) {
                                        dep.@revConstraint = version
                                    }
                                }

                                dep.@org = mvid.group
                                dep.@name = mvid.name
                                dep.@rev = mvid.version
                            }
                        }
                    }
                }
            }
        }
    }
}
