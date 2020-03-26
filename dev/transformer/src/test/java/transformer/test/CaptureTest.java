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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.transformer.action.BundleData;
import org.eclipse.transformer.action.impl.InputBufferImpl;
import org.eclipse.transformer.action.impl.SelectionRuleImpl;
import org.eclipse.transformer.action.impl.SignatureRuleImpl;
import org.slf4j.Logger;

import transformer.test.util.CaptureLoggerImpl;

public class CaptureTest {
	public CaptureLoggerImpl captureLogger;

	public CaptureLoggerImpl createLogger() {
		return new CaptureLoggerImpl("Test");
	}

	public CaptureLoggerImpl getCaptureLogger() {
		if ( captureLogger == null ) {
			captureLogger = createLogger(); 
		}
		return captureLogger;
	}

	public List<? extends CaptureLoggerImpl.LogEvent> consumeCapturedEvents() {
		if ( captureLogger != null ) {
			List<? extends CaptureLoggerImpl.LogEvent> capturedEvents =
				captureLogger.consumeCapturedEvents();
			System.out.println("Cleared [ " + capturedEvents.size() + " ] events");
			return capturedEvents;

		} else {
			return Collections.emptyList();
		}
	}

	public InputBufferImpl createBuffer() {
		return new InputBufferImpl();
	}

	//

	public SelectionRuleImpl createSelectionRule(
		Logger useLogger,
		Set<String> useIncludes,
		Set<String> useExcludes) {

		return new SelectionRuleImpl(useLogger, useIncludes, useExcludes);
	}

	public SignatureRuleImpl createSignatureRule(
			Logger useLogger,
			Map<String, String> usePackageRenames) {

		return new SignatureRuleImpl(
			useLogger,
			usePackageRenames, null,
			null,
			null,
			null, null);
	}

	public SignatureRuleImpl createSignatureRule(
		Logger useLogger,
		Map<String, String> usePackageRenames,
		Map<String, String> usePackageVersions,
		Map<String, BundleData> bundleData,
		Map<String, String> directStrings,
		Map<String, String> universalXMLTable,
		Map<String, Map<String, String>> xmlTables) {

		return new SignatureRuleImpl(
			useLogger,
			usePackageRenames, usePackageVersions,
			bundleData,
			directStrings,
			universalXMLTable, xmlTables);
	}
}
