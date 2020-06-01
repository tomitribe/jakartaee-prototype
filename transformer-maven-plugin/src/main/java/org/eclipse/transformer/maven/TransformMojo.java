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
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.eclipse.transformer.Transformer;
import org.eclipse.transformer.jakarta.JakartaTransformer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a Maven plugin which runs the Eclipse Transformer on build artifacts as part of the build.
 */
@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM, defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class TransformMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "false", readonly = true, required = true)
    private Boolean invert;

    @Parameter(property = "transformer-plugin.renames", defaultValue = "")
    private String rulesRenamesUri;

    @Parameter(property = "transformer-plugin.versions", defaultValue = "")
    private String rulesVersionUri;

    @Parameter(property = "transformer-plugin.bundles", defaultValue = "")
    private String rulesBundlesUri;

    @Parameter(property = "transformer-plugin.direct", defaultValue = "")
    private String rulesDirectUri;

    @Parameter(property = "transformer-plugin.xml", defaultValue = "")
    private String rulesXmlsUri;

    @Parameter(defaultValue = "transformed")
    private String classifier;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
    private String finalName;

    @Component
    private MavenProjectHelper projectHelper;

    @Component
    protected ArtifactFactory factory;

    @Component
    protected ArtifactResolver resolver;

    @Parameter
    private Artifact artifact;

    /**
     * Main execution point of the plugin. This looks at the attached artifacts, and runs the transformer on them.
     * @throws MojoExecutionException Thrown if there is an error during plugin execution
     */
    public void execute() throws MojoExecutionException {
        try {
            final Transformer transformer = getTransformer();

            final Artifact[] sourceArtifacts = getSourceArtifacts();
            for (final Artifact sourceArtifact : sourceArtifacts) {
                transform(transformer, sourceArtifact);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error occurred during execution", e);
        }
    }

    /**
     * This runs the transformation process on the source artifact with the transformer provided.
     * The transformed artifact is attached to the project.
     * @param transformer The Transformer to use for the transformation
     * @param sourceArtifact The Artifact to transform
     * @throws IOException
     */
    public void transform(final Transformer transformer, final Artifact sourceArtifact) throws IOException {

        final String sourceClassifier = sourceArtifact.getClassifier();
        final String targetClassifier = (sourceClassifier == null || sourceClassifier.length() == 0) ?
            this.classifier : sourceClassifier + "-" + this.classifier;

        final File targetFile = new File(outputDirectory,
            sourceArtifact.getArtifactId() + "-" +
                targetClassifier + "-" + sourceArtifact.getVersion() + "."
                + sourceArtifact.getType());

        transformer.setArgs(new String[] { sourceArtifact.getFile().getAbsolutePath(), targetFile.getAbsolutePath() });
        int rc = transformer.run();

        if (targetFile.exists()) {
            targetFile.deleteOnExit();
        }


        projectHelper.attachArtifact(
            project,
            sourceArtifact.getType(),
            targetClassifier,
            targetFile
        );
    }

    /**
     * Builds a configured transformer for the specified source and target artifacts
     * @return A configured transformer
     */
    public Transformer getTransformer() {
        final Transformer transformer = new Transformer(System.out, System.err);
        transformer.setOptionDefaults(JakartaTransformer.class, getOptionDefaults());
        return transformer;
    }

    /**
     * Gets the source artifacts that should be transformed
     * @return an array to artifacts to be transformed
     */
    public Artifact[] getSourceArtifacts() {
        List<Artifact> artifactList = new ArrayList<Artifact>();
        if (project.getArtifact() != null && project.getArtifact().getFile() != null) {
            artifactList.add(project.getArtifact());
        }

        for (final Artifact attachedArtifact : project.getAttachedArtifacts()) {
            if (attachedArtifact.getFile() != null) {
                artifactList.add(attachedArtifact);
            }
        }

        return artifactList.toArray(new Artifact[0]);
    }

    private Map<Transformer.AppOption, String> getOptionDefaults() {
        HashMap<Transformer.AppOption, String> optionDefaults = new HashMap();
        optionDefaults.put(Transformer.AppOption.RULES_RENAMES, isEmpty(rulesRenamesUri) ? "jakarta-renames.properties" : rulesRenamesUri);
        optionDefaults.put(Transformer.AppOption.RULES_VERSIONS, isEmpty(rulesVersionUri) ? "jakarta-versions.properties" : rulesVersionUri);
        optionDefaults.put(Transformer.AppOption.RULES_BUNDLES, isEmpty(rulesBundlesUri) ? "jakarta-bundles.properties" : rulesBundlesUri);
        optionDefaults.put(Transformer.AppOption.RULES_DIRECT, isEmpty(rulesDirectUri) ? "jakarta-direct.properties" : rulesDirectUri);
        optionDefaults.put(Transformer.AppOption.RULES_MASTER_XML, isEmpty(rulesXmlsUri) ? "jakarta-xml-master.properties" : rulesXmlsUri);
        return optionDefaults;
    }

    private boolean isEmpty(final String input) {
        return input == null || input.trim().length() == 0;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public MavenSession getSession() {
        return session;
    }

    public void setSession(MavenSession session) {
        this.session = session;
    }

    public Boolean getInvert() {
        return invert;
    }

    public void setInvert(Boolean invert) {
        this.invert = invert;
    }

    public String getRulesRenamesUri() {
        return rulesRenamesUri;
    }

    public void setRulesRenamesUri(String rulesRenamesUri) {
        this.rulesRenamesUri = rulesRenamesUri;
    }

    public String getRulesVersionUri() {
        return rulesVersionUri;
    }

    public void setRulesVersionUri(String rulesVersionUri) {
        this.rulesVersionUri = rulesVersionUri;
    }

    public String getRulesBundlesUri() {
        return rulesBundlesUri;
    }

    public void setRulesBundlesUri(String rulesBundlesUri) {
        this.rulesBundlesUri = rulesBundlesUri;
    }

    public String getRulesDirectUri() {
        return rulesDirectUri;
    }

    public void setRulesDirectUri(String rulesDirectUri) {
        this.rulesDirectUri = rulesDirectUri;
    }

    public String getRulesXmlsUri() {
        return rulesXmlsUri;
    }

    public void setRulesXmlsUri(String rulesXmlsUri) {
        this.rulesXmlsUri = rulesXmlsUri;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getFinalName() {
        return finalName;
    }

    public void setFinalName(String finalName) {
        this.finalName = finalName;
    }

    public MavenProjectHelper getProjectHelper() {
        return projectHelper;
    }

    public void setProjectHelper(MavenProjectHelper projectHelper) {
        this.projectHelper = projectHelper;
    }

    public ArtifactFactory getFactory() {
        return factory;
    }

    public void setFactory(ArtifactFactory factory) {
        this.factory = factory;
    }

    public ArtifactResolver getResolver() {
        return resolver;
    }

    public void setResolver(ArtifactResolver resolver) {
        this.resolver = resolver;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }
}
