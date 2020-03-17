package com.ibm.ws.jakarta.transformer.action;

import java.util.Map;
import java.util.Set;

public interface ContainerChanges extends Changes {
	int getAllResources();

	int getAllUnselected();
	int getAllSelected();

	int getAllUnchanged();
	int getAllChanged();

	Map<String, int[]> getChangedByAction();
	Map<String, int[]> getUnchangedByAction();

	Set<String> getActionNames();

	int getChanged(Action action);
	int getChanged(String name);

	int getUnchanged(Action action);
	int getUnchanged(String name);

	//

	void add(ContainerChanges otherChanges);

	boolean hasNestedChanges();
	ContainerChanges getNestedChanges();
	void addNested(ContainerChanges otherChanges);

	//

	void record();

	boolean HAS_CHANGES = true;

	void record(Action action);
	void record(Action action, boolean hasChanges);
	void record(String name, boolean hasChanges);
}
