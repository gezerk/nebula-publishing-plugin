/*
 * Copyright 2015 Netflix, Inc.
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

import nebula.plugin.publishing.PublishingBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.ivy.IvyPublication
import org.gradle.api.publish.ivy.plugins.IvyPublishPlugin

class IvyBasePublishingPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply PublishingBasePlugin
        project.plugins.apply IvyPublishPlugin

        project.publishing {
            publications {
                nebulaIvy(IvyPublication) {
                    descriptor.withXml { XmlProvider xml ->
                        def root = xml.asNode()
                        def infoNode = root?.info
                        if (!infoNode) {
                            infoNode = root.appendNode('info')
                        } else {
                            infoNode = infoNode[0]
                        }
                        infoNode.appendNode('description', project.description)
                    }
                }
            }
        }
    }
}
