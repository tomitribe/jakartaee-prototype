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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.transformer.TransformException;
import org.eclipse.transformer.action.impl.XMLActionImpl;
import org.eclipse.transformer.util.InputStreamData;
import org.junit.jupiter.api.Test;

import transformer.test.util.CaptureLoggerImpl;

public class TestTransformXML extends CaptureTest {

//	<featureManager>
//	  <feature>servlet-4.0</feature>
//	  <feature>jsp-2.3</feature>
//	</featureManager>	
//
//	<featureManager>
//	  <feature>servlet-5.0</feature>
//	  <feature>jsp-3.0</feature>
//	</featureManager>	

	public static final String INPUT_PATH ="transformer/test/data/input";
	public static final String OUTPUT_PATH ="transformer/test/data/output";

	public static final String UNCHANGED_HEADER_SERVER_XML = "unchanged.header.server.xml"; 
	public static final String UNCHANGED_SERVER_XML = "unchanged.server.xml"; 
	public static final String PARTIAL_SERVER_XML = "partial.server.xml"; 
	public static final String FULL_SERVER_XML = "full.server.xml"; 

	public static final String SERVLET_40 = "servlet-4.0";
	public static final String SERVLET_50 = "servlet-5.0";

	public static final String JSP_23 = "jsp-2.3";
	public static final String JSP_30 = "jsp-3.0";

	public static final Map<String, String> universalXMLTable;
	
	static {
		universalXMLTable = new HashMap<String, String>();
		universalXMLTable.put(SERVLET_40, SERVLET_50);
	}

	public static Map<String, String> getUniversalXMLTable() {
		return universalXMLTable;
	}

	public static final Map<String, Map<String, String>> xmlTables;
	
	static {
		xmlTables = new HashMap<String, Map<String, String>>();

		Map<String, String> jspXMLTable = new HashMap<String, String>();
		jspXMLTable.put(JSP_23, JSP_30);

		xmlTables.put(FULL_SERVER_XML, jspXMLTable);
	}

	public static Map<String, Map<String, String>> getXMLTables() {
		return xmlTables;
	}

	//

	public XMLActionImpl xmlAction;

	public XMLActionImpl getXMLAction() {
		if ( xmlAction == null ) {
			CaptureLoggerImpl useLogger = getCaptureLogger();

			xmlAction = new XMLActionImpl(
				useLogger,
				createBuffer(),
				createSelectionRule( useLogger, null, null ),
				createSignatureRule(
					useLogger,
					null, null,
					null,
					null,
					getUniversalXMLTable(),
					getXMLTables() ) ); 
		}
		return xmlAction;
	}

	@Test
	public void testXMLTransform_null_header() throws IOException, TransformException {
		verifyTransform(
			getXMLAction(),
			"Null case",
			INPUT_PATH + '/' + UNCHANGED_HEADER_SERVER_XML,
			OUTPUT_PATH + '/' + UNCHANGED_HEADER_SERVER_XML);
	}

	@Test
	public void testXMLTransform_null() throws IOException, TransformException {
		verifyTransform(
			getXMLAction(),
			"Null case",
			INPUT_PATH + '/' + UNCHANGED_SERVER_XML,
			OUTPUT_PATH + '/' + UNCHANGED_SERVER_XML);
	}

	@Test
	public void testXMLTransform_partial() throws IOException, TransformException {
		verifyTransform(
			getXMLAction(),
			"Partial case",
			INPUT_PATH + '/' + PARTIAL_SERVER_XML,
			OUTPUT_PATH + '/' + PARTIAL_SERVER_XML);
	}

	@Test
	public void testXMLTransform_full() throws IOException, TransformException {
		verifyTransform(
			getXMLAction(),
			"Full case",
			INPUT_PATH + '/' + FULL_SERVER_XML,
			OUTPUT_PATH + '/' + FULL_SERVER_XML);
	}

	protected void verifyTransform(XMLActionImpl action, String testTag, String inputPath, String outputPath)
		throws IOException, TransformException {

		System.out.println("Case   [ " + testTag + " ] START");

		System.out.println("Action [ " + action.getName() + " ]");
		System.out.println("Input  [ " + inputPath + " ]");
		System.out.println("Output [ " + outputPath + " ]");

		InputStream inputStream = TestUtils.getResourceStream(inputPath);
		InputStreamData transformedData;
		try {
			transformedData = action.apply(inputPath, inputStream);
		} finally {
			inputStream.close();
		}
		List<String> transformedLines = TestUtils.loadLines(transformedData.stream);

		InputStream expectedStream = TestUtils.getResourceStream(outputPath);
		List<String> expectedLines;
		try {
			expectedLines = TestUtils.loadLines(expectedStream);
		} finally {
			expectedStream.close();
		}

		TestUtils.verify(testTag, expectedLines, transformedLines);

		System.out.println("Case   [ " + testTag + " ] VERIFIED");
	}
}
