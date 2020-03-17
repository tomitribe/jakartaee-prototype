package org.eclipse.transformer.action.impl;

import org.eclipse.transformer.action.ActionType;

public class RarActionImpl extends ContainerActionImpl {
	public RarActionImpl(
		LoggerImpl logger,
		InputBufferImpl buffer,
		SelectionRuleImpl selectionRule,
		SignatureRuleImpl signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	public String getName() {
		return "RAR Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.RAR;
	}

	@Override
	public String getAcceptExtension() {
		return ".rar";
	}
}
