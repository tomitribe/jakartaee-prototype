package org.eclipse.transformer.action.impl;

import org.eclipse.transformer.action.InputBuffer;

public class InputBufferImpl implements InputBuffer {
	public InputBufferImpl() {
		this.inputBuffer = null;
	}

	private byte[] inputBuffer;

	@Override
	public byte[] getInputBuffer() {
		return inputBuffer;
	}

	@Override
	public void setInputBuffer(byte[] inputBuffer) {
		this.inputBuffer = inputBuffer;
	}
}
