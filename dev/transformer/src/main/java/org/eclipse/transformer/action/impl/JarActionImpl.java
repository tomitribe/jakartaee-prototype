package org.eclipse.transformer.action.impl;

import org.eclipse.transformer.action.ActionType;

public class JarActionImpl extends ZipActionImpl {

	public JarActionImpl(
		LoggerImpl logger,
		InputBufferImpl buffer,
		SelectionRuleImpl selectionRule,
		SignatureRuleImpl signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	public String getName() {
		return "Jar Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.JAR;
	}

	@Override
	public String getAcceptExtension() {
		return ".jar";
	}
}