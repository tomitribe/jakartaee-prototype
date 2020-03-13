package org.eclipse.transformer.action.impl;

import org.eclipse.transformer.action.ActionType;

public class WarActionImpl extends ContainerActionImpl {
	public WarActionImpl(
		LoggerImpl logger,
		InputBufferImpl buffer,
		SelectionRuleImpl selectionRule,
		SignatureRuleImpl signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	public String getName() {
		return "WAR Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.WAR;
	}

	//

	@Override
	public String getAcceptExtension() {
		return ".war";
	}
}
