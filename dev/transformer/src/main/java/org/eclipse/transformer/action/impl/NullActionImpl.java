package org.eclipse.transformer.action.impl;

import java.io.File;

import org.eclipse.transformer.TransformException;
import org.eclipse.transformer.action.ActionType;
import org.eclipse.transformer.util.ByteData;

public class NullActionImpl extends ActionImpl {

	public NullActionImpl(
			LoggerImpl logger,
			InputBufferImpl buffer,
			SelectionRuleImpl selectionRule,
			SignatureRuleImpl signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	public String getName() {
		return "Null Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.NULL;
	}

	//

	@Override
	public String getAcceptExtension() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean accept(String resourcePath, File resourceFile) {
		return true;
	}

	@Override
	public ByteData apply(String inputName, byte[] inputBytes, int inputLength)
		throws TransformException {

		clearChanges();
		setResourceNames(inputName, inputName);
		return new ByteData(inputName, inputBytes, 0, inputLength);
	}
}
