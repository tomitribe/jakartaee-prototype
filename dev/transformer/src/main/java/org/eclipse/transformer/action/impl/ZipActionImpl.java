package org.eclipse.transformer.action.impl;

import org.eclipse.transformer.action.ActionType;

public class ZipActionImpl extends ContainerActionImpl {

	public ZipActionImpl(
		LoggerImpl logger,
		InputBufferImpl buffer,
		SelectionRuleImpl selectionRule,
		SignatureRuleImpl signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	public String getName() {
		return "Zip Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ZIP;
	}

	@Override
	public String getAcceptExtension() {
		return ".zip";
	}
}