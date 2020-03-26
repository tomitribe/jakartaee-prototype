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

package org.eclipse.transformer.action.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.transformer.TransformException;
import org.eclipse.transformer.action.ActionType;
import org.eclipse.transformer.util.ByteData;
import org.slf4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class XMLActionImpl extends ActionImpl {

	public XMLActionImpl(
		Logger logger,
		InputBufferImpl buffer,
		SelectionRuleImpl selectionRule,
		SignatureRuleImpl signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	public String getName() {
		return "XML Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.XML;
	}

	@Override
	public String getAcceptExtension() {
		return ".xml";
	}

	//

	@Override
	public ByteData apply(String inputName, byte[] inputBytes, int inputCount) throws TransformException {
		clearChanges();

		setResourceNames(inputName, inputName);

		InputStream inputStream = new ByteArrayInputStream(inputBytes, 0, inputCount);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputCount);

		transform(inputName, inputStream, outputStream);

		if ( !hasNonResourceNameChanges() ) {
			return null;

		} else {
			byte[] outputBytes = outputStream.toByteArray();
			return new ByteData(inputName, outputBytes, 0, outputBytes.length);
		}
	}

	//

	private static final SAXParserFactory parserFactory;

	static {
		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
	}

	public static SAXParserFactory getParserFactory() {
		return parserFactory;
	}

	//

	private static Charset utf8;

	static {
		utf8 = Charset.forName("UTF-8");
	}

	public static Charset getUTF8() {
		return utf8;
	}

	//

	public void transform(String inputName, InputStream input, OutputStream output) throws TransformException {
		InputSource inputSource = new InputSource(input);

		XMLContentHandler handler = new XMLContentHandler(inputName, inputSource, output);

		SAXParser parser;
		try {
			parser = getParserFactory().newSAXParser();
			// 'newSAXParser' throws ParserConfigurationException, SAXException
		} catch ( Exception e ) {
			e.printStackTrace(System.out);
			throw new TransformException("Failed to obtain parser for [ " + inputName + " ]", e);
		}

		try {
			parser.parse(input, handler); // throws SAXException, IOException
		} catch ( Exception e ) {
			e.printStackTrace(System.out);
			throw new TransformException("Failed to parse [ " + inputName + " ]", e);
		}
	}

	//

	

	public class XMLContentHandler extends DefaultHandler2 {
		@Override // DefaultHandler2
		public void attributeDecl(String arg0, String arg1, String arg2, String arg3, String arg4) throws SAXException {
			super.attributeDecl(arg0, arg1, arg2, arg3, arg4);
		}

		@Override // DefaultHandler2
		public void elementDecl(String arg0, String arg1) throws SAXException {
			super.elementDecl(arg0, arg1);
		}

		//

		@Override // DefaultHandler2
		public void internalEntityDecl(String arg0, String arg1) throws SAXException {
			super.internalEntityDecl(arg0, arg1);
		}

		@Override // DefaultHandler2
		public void externalEntityDecl(String arg0, String arg1, String arg2) throws SAXException {
			super.externalEntityDecl(arg0, arg1, arg2);
		}

		@Override // DefaultHandler2
		public InputSource resolveEntity(String arg0, String arg1, String arg2, String arg3)
			throws SAXException, IOException {
			return super.resolveEntity(arg0, arg1, arg2, arg3);
		}

		@Override // DefaultHandler2
		public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
			return super.resolveEntity(arg0, arg1);
		}

		//

		@Override // DefaultHandler2
		public InputSource getExternalSubset(String arg0, String arg1) throws SAXException, IOException {
			return super.getExternalSubset(arg0, arg1);
		}

		//

		@Override // DefaultHandler2
		public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
			super.comment(arg0, arg1, arg2);
		}

		@Override // DefaultHandler2
		public void startCDATA() throws SAXException {
			super.startCDATA();
		}

		@Override // DefaultHandler2
		public void endCDATA() throws SAXException {
			super.endCDATA();
		}

		@Override // DefaultHandler2
		public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
			super.startDTD(arg0, arg1, arg2);
		}

		@Override // DefaultHandler2
		public void endDTD() throws SAXException {
			// 			super.endDTD();
		}

		@Override // DefaultHandler2
		public void startEntity(String arg0) throws SAXException {
			super.startEntity(arg0);
		}

		@Override // DefaultHandler2
		public void endEntity(String arg0) throws SAXException {
			super.endEntity(arg0);
		}

		//

		public XMLContentHandler(String inputName, InputSource inputSource, OutputStream outputStream) {
			this.inputName = inputName;

			String encoding = inputSource.getEncoding();
			this.charset = ( (encoding == null) ? null : Charset.forName(encoding) );

			this.publicId = inputSource.getPublicId();
			this.systemId = inputSource.getSystemId();

			this.xmlTables = getSignatureRule().selectXMLTables(inputName);

			this.outputStream = outputStream;

			this.lineBuilder = new StringBuilder();
		}

		//

		private final String inputName;

		private final String publicId;
		private final String systemId;
		private Charset charset;

		//

		public String getInputName() {
			return inputName;
		}

		public Charset getCharset() {
			return charset;
		}

		public String getPublicId() {
			return publicId;
		}

		public String getSystemId() {
			return systemId;
		}

		//

		private final List<Map<String, String>> xmlTables;

		public List<Map<String, String>> getXMLTables() {
			return xmlTables;
		}

		public String xmlSubstitute(String initialValue) {
			boolean didChange;

			String finalValue = replaceEmbeddedPackages(initialValue);

			if ( finalValue != null ) {
				didChange = true;
				debug( "XML action on [ {} ] replaces [ {} ] with [ {} ] using package renames",
				       getInputName(), initialValue, finalValue );
			} else {
				didChange = false;
				finalValue = initialValue;
			}

			if ( xmlTables != null ) {
				finalValue = getSignatureRule().xmlSubstitute(finalValue, xmlTables);

				if ( !initialValue.equals(finalValue) ) {
					didChange = true;
					debug( "XML action on [ {} ] replaces [ {} ] with [ {} ] using direct updates",
					       getInputName(), initialValue, finalValue );
				}
			}

			if ( didChange ) {
				addReplacement();
			}

			return finalValue;
		}

		//

		private final OutputStream outputStream;

		public OutputStream getOutputStream() {
			return outputStream;
		}

		public void write(String text) throws SAXException {
			write( text, getCharset() );
		}
		
		public void writeUTF8(String text) throws SAXException {
			write( text, getUTF8() );
		}

		public void write(String text, Charset useCharset) throws SAXException {
			try {
				outputStream.write( text.getBytes(useCharset) );
			} catch ( IOException e ) {
				throw new SAXException("Failed to write [ " + text + " ]", e);
			}
		}

		//

		private final StringBuilder lineBuilder;

		protected void appendLine() {
			lineBuilder.append('\n');
		}

		protected void append(char c) {
			lineBuilder.append(c);
		}

		protected void append(char[] buffer, int start, int length) {
			for ( int trav = start; trav < start + length; trav++ ) {
				lineBuilder.append( buffer[trav] );
			}
		}

		protected void appendLine(char c) {
			lineBuilder.append(c);
			lineBuilder.append('\n');
		}

		protected void append(String text) {
			lineBuilder.append(text);
		}
		
		protected void appendLine(String text) {
			lineBuilder.append(text);
			lineBuilder.append('\n');
		}

		protected void emit() throws SAXException {
			String nextLine = lineBuilder.toString();
			lineBuilder.setLength(0);

			write(nextLine); // throws SAXException
		}

		protected void emitLineUTF8(String text) throws SAXException {
			String nextLine = lineBuilder.toString();
			lineBuilder.setLength(0);

			writeUTF8(nextLine); // throws SAXException
		}

		//

		@Override // DefaultHandler
		public void startDocument() throws SAXException {
			Charset useCharset = getCharset();
			if ( useCharset == null ) {
				useCharset = getUTF8();
			}
			String charsetName = useCharset.name();
			emitLineUTF8("<?xml version = \"1.0\" encoding = \""+ charsetName + "\"?>\n");
		}

//		@Override // DefaultHandler
//		public void endDocument() throws SAXException {
//			super.endDocument();
//		}
//
//		@Override // DefaultHandler
//		public void setDocumentLocator(Locator locator) {
//			super.setDocumentLocator(locator);
//		}

		@Override // DefaultHandler
		public void processingInstruction(String target, String data) throws SAXException {
			append("<?");
			append(target);
			if ( (data != null) && data.length() > 0) {
				append(' ');
				append(data);
			}
			append("?>");
		}

		//

//		@Override // DefaultHandler
//		public void startPrefixMapping(String prefix, String uri) throws SAXException {
//			super.startPrefixMapping(prefix, uri);
//		}
//
//		@Override // DefaultHandler
//		public void endPrefixMapping(String prefix) throws SAXException {
//			super.endPrefixMapping(prefix);
//		}

		//

		// TODO: Should element qualified names be updated?

		@Override // DefaultHandler
		public void startElement(String qualifiedName, String arg1, String arg2, Attributes attributes) throws SAXException {
		      append('<');
		      append(qualifiedName);

		      if ( attributes != null ) {
		         int numberAttributes = attributes.getLength();
		         for (int loopIndex = 0; loopIndex < numberAttributes; loopIndex++) {
		            append(' ');
		            append( attributes.getQName(loopIndex) );
		            append("=\"");
		            append( xmlSubstitute( attributes.getValue(loopIndex) ) );
		            append('"');
		         }
		      }

		      appendLine('>');

		      emit();
		}

		// TODO: Should element qualified names be updated?

		@Override // DefaultHandler
		public void endElement(String qualifiedName, String arg1, String arg2) throws SAXException {
		      append("</");
		      append(qualifiedName);
		      append('>');
		}

		@Override // DefaultHandler
		public void characters(char[] chars, int start, int length) throws SAXException {
		      String initialText = new String(chars, start, length);
			      String finalText = xmlSubstitute(initialText);
		      append(finalText);
		}

		@Override // DefaultHandler
		public void ignorableWhitespace(char[] whitespace, int start, int length) throws SAXException {
			append(whitespace, start, length);
		}

		@Override // DefaultHandler
		public void skippedEntity(String name) throws SAXException {
			super.skippedEntity(name);
		}
	}
}
