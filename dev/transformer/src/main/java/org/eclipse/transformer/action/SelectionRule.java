package org.eclipse.transformer.action;

public interface SelectionRule {
	boolean select(String resourceName);
	boolean selectIncluded(String resourceName);
	boolean rejectExcluded(String resourceName);

}