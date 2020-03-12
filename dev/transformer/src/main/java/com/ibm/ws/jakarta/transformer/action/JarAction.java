package com.ibm.ws.jakarta.transformer.action;

public interface JarAction extends ZipAction {
	@Override
	JarChanges getChanges();
}
