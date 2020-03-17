package com.ibm.ws.jakarta.transformer.action.impl;

import com.ibm.ws.jakarta.transformer.action.ActionType;
import com.ibm.ws.jakarta.transformer.action.RarAction;

public class RarActionImpl extends ContainerActionImpl implements RarAction {
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

	//

	@Override
	protected RarChangesImpl newChanges() {
		return new RarChangesImpl();
	}

	@Override
	public RarChangesImpl getChanges() {
		return (RarChangesImpl) super.getChanges();
	}

	//

	@Override
	public String getAcceptExtension() {
		return ".war";
	}
}
