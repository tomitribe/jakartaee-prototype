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

package transformer.test;

import org.eclipse.transformer.TransformException;
import org.eclipse.transformer.action.impl.InputBufferImpl;
import org.eclipse.transformer.action.impl.RelocateResourceActionImpl;
import org.eclipse.transformer.action.impl.SelectionRuleImpl;
import org.eclipse.transformer.action.impl.SignatureRuleImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import transformer.test.util.CaptureLoggerImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestTransformResources extends CaptureTest {

	public static final String JAVAX_SERVLET = "javax.servlet";
	public static final String JAVAX_SERVLET_ANNOTATION = "javax.servlet.annotation";
	public static final String JAVAX_SERVLET_DESCRIPTOR = "javax.servlet.descriptor";
	public static final String JAVAX_SERVLET_HTTP = "javax.servlet.http";
	public static final String JAVAX_SERVLET_RESOURCES = "javax.servlet.resources";

	public static final String JAKARTA_SERVLET = "jakarta.servlet";
	public static final String JAKARTA_SERVLET_ANNOTATION = "jakarta.servlet.annotation";
	public static final String JAKARTA_SERVLET_DESCRIPTOR = "jakarta.servlet.descriptor";
	public static final String JAKARTA_SERVLET_HTTP = "jakarta.servlet.http";
	public static final String JAKARTA_SERVLET_RESOURCES = "jakarta.servlet.resources";

	public static final String JAKARTA_SERVLET_VERSION = "[2.6, 6.0)";
	public static final String JAKARTA_SERVLET_ANNOTATION_VERSION  = "[2.6, 6.0)";
	public static final String JAKARTA_SERVLET_DESCRIPTOR_VERSION  = "[2.6, 6.0)";
	public static final String JAKARTA_SERVLET_HTTP_VERSION  = "[2.6, 6.0)";
	public static final String JAKARTA_SERVLET_RESOURCES_VERSION  = "[2.6, 6.0)";

	protected Set<String> includes;

	public Set<String> getIncludes() {
		if ( includes == null ) {
			includes = new HashSet<String>();
		}

		return includes;
	}

	public Set<String> getExcludes() {
		return Collections.emptySet();
	}

	protected Map<String, String> packageRenames;

	public Map<String, String> getPackageRenames() {
		if ( packageRenames == null ) {
			packageRenames = new HashMap<String, String>();
			packageRenames.put(JAVAX_SERVLET, JAKARTA_SERVLET);
			packageRenames.put(JAVAX_SERVLET_ANNOTATION, JAKARTA_SERVLET_ANNOTATION);
			packageRenames.put(JAVAX_SERVLET_DESCRIPTOR, JAKARTA_SERVLET_DESCRIPTOR);
			packageRenames.put(JAVAX_SERVLET_HTTP, JAKARTA_SERVLET_HTTP);
			packageRenames.put(JAVAX_SERVLET_RESOURCES,JAKARTA_SERVLET_RESOURCES);
		}
		return packageRenames;
	}

	protected Map<String, String> packageVersions;

	public Map<String, String> getPackageVersions() {
		if ( packageVersions == null ) {
			packageVersions = new HashMap<String, String>();
			packageVersions.put(JAVAX_SERVLET,            JAKARTA_SERVLET_VERSION );
			packageVersions.put(JAVAX_SERVLET_ANNOTATION, JAKARTA_SERVLET_ANNOTATION_VERSION );
			packageVersions.put(JAVAX_SERVLET_DESCRIPTOR, JAKARTA_SERVLET_DESCRIPTOR_VERSION );
			packageVersions.put(JAVAX_SERVLET_HTTP,       JAKARTA_SERVLET_HTTP_VERSION );
			packageVersions.put(JAVAX_SERVLET_RESOURCES,  JAKARTA_SERVLET_RESOURCES_VERSION );
		}
		return packageVersions;
	}

	@Test
	public void testTransformResources() throws TransformException, IOException {

		CaptureLoggerImpl useLogger = getCaptureLogger();

		final RelocateResourceActionImpl action = new RelocateResourceActionImpl(
			useLogger, true, true, new InputBufferImpl(),
			new SelectionRuleImpl(useLogger, getIncludes(), getExcludes()),
			new SignatureRuleImpl(useLogger, getPackageRenames(), getPackageVersions(), null, null, null)
		);

		action.apply("javax/servlet/http/LocalStrings.properties", getClass().getResourceAsStream("/transformer/test/data/simple.resource"));

		Assertions.assertEquals("javax/servlet/http/LocalStrings.properties", action.getLastActiveChanges().getInputResourceName());
		Assertions.assertEquals("jakarta/servlet/http/LocalStrings.properties", action.getLastActiveChanges().getOutputResourceName());
		Assertions.assertEquals(0, action.getLastActiveChanges().getReplacements());
	}

	@Test
	public void testTransformResourcesWithoutPackageMatch() throws TransformException, IOException {

		CaptureLoggerImpl useLogger = getCaptureLogger();

		final RelocateResourceActionImpl action = new RelocateResourceActionImpl(
			useLogger, true, true, new InputBufferImpl(),
			new SelectionRuleImpl(useLogger, getIncludes(), getExcludes()),
			new SignatureRuleImpl(useLogger, getPackageRenames(), getPackageVersions(), null, null, null)
		);

		action.apply("com/test/resource/LocalStrings.properties", getClass().getResourceAsStream("/transformer/test/data/simple.resource"));

		Assertions.assertEquals("com/test/resource/LocalStrings.properties", action.getLastActiveChanges().getInputResourceName());
		Assertions.assertEquals("com/test/resource/LocalStrings.properties", action.getLastActiveChanges().getOutputResourceName());
		Assertions.assertEquals(0, action.getLastActiveChanges().getReplacements());
	}
}
