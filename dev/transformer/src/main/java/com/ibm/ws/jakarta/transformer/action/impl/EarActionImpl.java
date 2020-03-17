package com.ibm.ws.jakarta.transformer.action.impl;

import com.ibm.ws.jakarta.transformer.action.ActionType;
import com.ibm.ws.jakarta.transformer.action.EarAction;

public class EarActionImpl extends ContainerActionImpl implements EarAction {
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
	protected EarChangesImpl newChanges() {
		return new EarChangesImpl();
	}

	@Override
	public EarChangesImpl getChanges() {
		return (EarChangesImpl) super.getChanges();
	}

	//

	@Override
	public String getAcceptExtension() {
		return ".ear";
	}
}
