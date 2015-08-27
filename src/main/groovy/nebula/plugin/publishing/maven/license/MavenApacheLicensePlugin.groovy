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
package nebula.plugin.publishing.maven.license

import nebula.plugin.publishing.maven.MavenBasePublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin


class MavenApacheLicensePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply MavenBasePublishPlugin

        project.publishing {
            publications {
                nebula(MavenPublication) {
                    pom.withXml { XmlProvider xml ->
                        def licensesNode = xml.asNode()?.licenses
                        if (!licensesNode) {
                            licensesNode = xml.asNode().appendNode('licenses')
                        }
                        def apache = licensesNode.appendNode('license')
                        apache.appendNode('name', 'The Apache Software License, Version 2.0')
                        apache.appendNode('url', 'http://www.apache.org/licenses/LICENSE-2.0.txt')
                        apache.appendNode('distribution', 'repo'
                        )
                    }
                }
            }
        }
    }
}
