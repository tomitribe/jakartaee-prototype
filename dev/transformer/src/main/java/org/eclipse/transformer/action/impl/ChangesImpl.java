package org.eclipse.transformer.action.impl;

import java.io.PrintStream;

import org.eclipse.transformer.action.Changes;
import org.eclipse.transformer.action.ContainerChanges;

public class ChangesImpl implements Changes {
	public ChangesImpl() {
		// Empty
	}

	@Override
	public boolean hasChanges() {
		return hasResourceNameChange() || hasNonResourceNameChanges();
	}

	@Override
	public void clearChanges() {
		inputResourceName = null;
		outputResourceName = null;

		replacements = 0;
	}

	//

	private String inputResourceName;
	private String outputResourceName;

	@Override
	public String getInputResourceName() {
		return inputResourceName;
	}

	@Override
	public void setInputResourceName(String inputResourceName) {
		this.inputResourceName = inputResourceName;
	}

	@Override
	public String getOutputResourceName() {
		return outputResourceName;
	}

	@Override
	public void setOutputResourceName(String outputResourceName) {
		this.outputResourceName = outputResourceName;
	}

	@Override
	public boolean hasResourceNameChange() {
		// The input name will be null if the transform fails very early.
		return ( (inputResourceName != null) &&
				 !inputResourceName.equals(outputResourceName) );
	}

	//

	private int replacements;

	@Override
	public int getReplacements() {
		return replacements;
	}

	@Override
	public void addReplacement() {
		replacements++;
	}

	@Override
	public void addReplacements(int additions) {
		replacements += additions;
	}

	@Override
	public boolean hasNonResourceNameChanges() {
		return ( replacements > 0 );
	}

	//

	@Override
	public void addNestedInto(ContainerChanges containerChanges) {
		// By default do nothing. 
	}

	//

	@Override
	public void displayChanges(PrintStream printStream, String inputPath, String outputPath) {
		printStream.printf(
			"Input  [ %s ] as [ %s ]\n", getInputResourceName(), inputPath );
		printStream.printf(
			"Output [ %s ] as [ %s ]\n", getOutputResourceName(), outputPath );
		printStream.printf(
			"Replacements  [ %s ]\n", getReplacements() );
	}
}
