package org.eclipse.transformer.action.impl;

import org.eclipse.transformer.action.ActionType;

public class EarActionImpl extends ContainerActionImpl {
	public EarActionImpl(
		LoggerImpl logger,
		InputBufferImpl buffer,
		SelectionRuleImpl selectionRule,
		SignatureRuleImpl signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	public String getName() {
		return "EAR Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.EAR;
	}

	//

	@Override
	public String getAcceptExtension() {
		return ".ear";
	}
}
