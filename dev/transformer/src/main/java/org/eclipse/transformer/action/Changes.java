package org.eclipse.transformer.action;

import java.io.PrintStream;

public interface Changes {
	String getInputResourceName();
	void setInputResourceName(String inputResourceName);

	String getOutputResourceName();
	void setOutputResourceName(String outputResourceName);

	int getReplacements();
	void addReplacement();
	void addReplacements(int additions);

	void addNestedInto(ContainerChanges containerChanges);

	boolean hasChanges();
	boolean hasNonResourceNameChanges();
	boolean hasResourceNameChange();

	void clearChanges();

	void displayChanges(PrintStream printStream, String inputPath, String outputPath);
}
