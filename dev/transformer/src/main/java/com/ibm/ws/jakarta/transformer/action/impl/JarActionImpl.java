package com.ibm.ws.jakarta.transformer.action.impl;

import com.ibm.ws.jakarta.transformer.action.ActionType;
import com.ibm.ws.jakarta.transformer.action.JarAction;

public class JarActionImpl extends ZipActionImpl implements JarAction {

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

	//

	@Override
	protected JarChangesImpl newChanges() {
		return new JarChangesImpl();
	}

	@Override
	public JarChangesImpl getChanges() {
		return (JarChangesImpl) super.getChanges();
	}

	//

	@Override
	public String getAcceptExtension() {
		return ".jar";
	}
}