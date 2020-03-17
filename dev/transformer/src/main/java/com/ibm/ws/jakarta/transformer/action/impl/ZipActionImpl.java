package com.ibm.ws.jakarta.transformer.action.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ibm.ws.jakarta.transformer.JakartaTransformException;
import com.ibm.ws.jakarta.transformer.action.Action;
import com.ibm.ws.jakarta.transformer.action.ActionType;
import com.ibm.ws.jakarta.transformer.action.ContainerChanges;
import com.ibm.ws.jakarta.transformer.action.ZipAction;
import com.ibm.ws.jakarta.transformer.util.FileUtils;
import com.ibm.ws.jakarta.transformer.util.InputStreamData;

public class ZipActionImpl extends ContainerActionImpl implements ZipAction {

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

	//

	@Override
	protected ZipChangesImpl newChanges() {
		return new ZipChangesImpl();
	}

	@Override
	public ZipChangesImpl getChanges() {
		return (ZipChangesImpl) super.getChanges();
	}

	//

	@Override
	public String getAcceptExtension() {
		return ".zip";
	}
}