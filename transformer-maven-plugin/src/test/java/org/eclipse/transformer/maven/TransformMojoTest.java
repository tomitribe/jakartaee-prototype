/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package org.eclipse.transformer.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.DefaultArtifactHandlerStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifact;
import org.eclipse.transformer.Transformer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class TransformMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();

    @Rule
    public TestResources resources = new TestResources();

    @Test
    public void testProjectArtifactTransformerPlugin() throws Exception {
        final TransformMojo mojo = findMojo("transform-build-artifact", "run");
        Assert.assertNotNull(mojo);

        final File targetDirectory = this.resources.getBasedir("transform-build-artifact");
        final File modelDirectory = new File(targetDirectory,"target/model" );
        final File pom = new File(targetDirectory, "pom.xml");

        final MavenProject mavenProject = createMavenProject(modelDirectory, pom, "war", "rest-sample");
        mavenProject.getArtifact().setFile(createService());

        mojo.setProject(mavenProject);
        mojo.setClassifier("transformed");

        final Artifact[] sourceArtifacts = mojo.getSourceArtifacts();
        Assert.assertEquals(1, sourceArtifacts.length);
        Assert.assertEquals("org.superbiz.rest", sourceArtifacts[0].getGroupId());
        Assert.assertEquals("rest-sample", sourceArtifacts[0].getArtifactId());
        Assert.assertEquals("1.0-SNAPSHOT", sourceArtifacts[0].getVersion());
        Assert.assertEquals("war", sourceArtifacts[0].getType());
        Assert.assertNull(sourceArtifacts[0].getClassifier());

        final Transformer transformer = mojo.getTransformer();
        Assert.assertNotNull(transformer);

        mojo.transform(transformer, sourceArtifacts[0]);

        Assert.assertEquals(1, mavenProject.getAttachedArtifacts().size());
        final Artifact transformedArtifact = mavenProject.getAttachedArtifacts().get(0);

        Assert.assertEquals("org.superbiz.rest", transformedArtifact.getGroupId());
        Assert.assertEquals("rest-sample", transformedArtifact.getArtifactId());
        Assert.assertEquals("1.0-SNAPSHOT", transformedArtifact.getVersion());
        Assert.assertEquals("war", transformedArtifact.getType());
        Assert.assertEquals("transformed", transformedArtifact.getClassifier());
    }

    @Test
    public void testMultipleArtifactTransformerPlugin() throws Exception {
        final TransformMojo mojo = findMojo("transform-build-artifact", "run");
        Assert.assertNotNull(mojo);

        final File targetDirectory = this.resources.getBasedir("transform-build-artifact");
        final File modelDirectory = new File(targetDirectory,"target/model" );
        final File pom = new File(targetDirectory, "pom.xml");

        final MavenProject mavenProject = createMavenProject(modelDirectory, pom, "pom", "simple-service");

        mojo.setProject(mavenProject);
        mojo.setClassifier("transformed");

        mojo.getProjectHelper().attachArtifact(mavenProject, "zip", "test1", createService());
        mojo.getProjectHelper().attachArtifact(mavenProject, "zip", "test2", createService());
        mojo.getProjectHelper().attachArtifact(mavenProject, "zip", "test3", createService());

        final Artifact[] sourceArtifacts = mojo.getSourceArtifacts();
        Assert.assertEquals(3, sourceArtifacts.length);

        for (int i = 0; i < 3; i++) {
            Assert.assertEquals("org.superbiz.rest", sourceArtifacts[i].getGroupId());
            Assert.assertEquals("simple-service", sourceArtifacts[i].getArtifactId());
            Assert.assertEquals("1.0-SNAPSHOT", sourceArtifacts[i].getVersion());
            Assert.assertEquals("zip", sourceArtifacts[i].getType());
            Assert.assertEquals("test" + (i + 1), sourceArtifacts[i].getClassifier());
        }

        final Transformer transformer = mojo.getTransformer();
        Assert.assertNotNull(transformer);

        for (int i = 0; i < 3; i++) {
            mojo.transform(transformer, sourceArtifacts[i]);
        }

        Assert.assertEquals(6, mavenProject.getAttachedArtifacts().size());
        Set<String> classifiers = mavenProject.getAttachedArtifacts().stream()
            .filter(a -> (a.getType().equals("zip") && a.getArtifactId().equals("simple-service")))
            .map(a -> a.getClassifier())
            .collect(Collectors.toSet());

        Assert.assertEquals(6, mavenProject.getAttachedArtifacts().size());
        Assert.assertTrue(classifiers.contains("test1"));
        Assert.assertTrue(classifiers.contains("test2"));
        Assert.assertTrue(classifiers.contains("test3"));
        Assert.assertTrue(classifiers.contains("test1-transformed"));
        Assert.assertTrue(classifiers.contains("test2-transformed"));
        Assert.assertTrue(classifiers.contains("test3-transformed"));
    }

    public MavenProject createMavenProject(final File modelDirectory, final File pom, final String packaging, final String artfifactId) {
        final MavenProject mavenProject = new MavenProject();
        mavenProject.setFile(pom);
        mavenProject.setGroupId("org.superbiz.rest");
        mavenProject.setArtifactId(artfifactId);
        mavenProject.setVersion("1.0-SNAPSHOT");
        mavenProject.setPackaging(packaging);
        mavenProject.getBuild().setDirectory( modelDirectory.getParentFile().getAbsolutePath());
        mavenProject.getBuild().setOutputDirectory( modelDirectory.getAbsolutePath());
        mavenProject.setArtifact(new DefaultArtifact(
            mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion(), (String)null, "war", (String)null,
            new DefaultArtifactHandlerStub(packaging, null)
        ));
        return mavenProject;
    }

    public File createService() throws IOException {
        final File tempFile = File.createTempFile("service", ".war");
        tempFile.delete();

        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "service.war")
            .addClass(EchoService.class);

        webArchive.as(ZipExporter.class).exportTo(tempFile, true);
        return tempFile;
    }

    protected TransformMojo findMojo(final String projectName, final String goalName) throws Exception {
        File baseDir = this.resources.getBasedir(projectName);
        Assert.assertNotNull(baseDir);
        Assert.assertTrue(baseDir.exists());
        Assert.assertTrue(baseDir.isDirectory());

        File pom = new File(baseDir, "pom.xml");
        return (TransformMojo) this.rule.lookupMojo(goalName, pom);
    }

}
