package com.ibm.ws.jakarta.transformer.action.impl;

import java.io.File;

import com.ibm.ws.jakarta.transformer.JakartaTransformException;
import com.ibm.ws.jakarta.transformer.action.Action;
import com.ibm.ws.jakarta.transformer.action.ActionType;
import com.ibm.ws.jakarta.transformer.action.ContainerChanges;
import com.ibm.ws.jakarta.transformer.action.DirectoryAction;

public class DirectoryActionImpl extends ContainerActionImpl implements DirectoryAction {

    public DirectoryActionImpl(LoggerImpl logger,
                               InputBufferImpl buffer,
                               SelectionRuleImpl selectionRule,
                               SignatureRuleImpl signatureRule) {

            super(logger, buffer, selectionRule, signatureRule);
    }

	//

	@Override
	public ActionType getActionType() {
		return ActionType.DIRECTORY;
	}

	@Override
	public String getName() {
		return "Directory Action";
	}

	//

	@Override
	protected DirectoryChangesImpl newChanges() {
		return new DirectoryChangesImpl();
	}

	@Override
	public DirectoryChangesImpl getChanges() {
		return (DirectoryChangesImpl) super.getChanges();
	}

	//

	/**
	 * The choice of using a stream or using an input stream should never occur
	 * on a directory action.
	 */
	public boolean useStreams() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean accept(String resourceName, File resourceFile) {
		return ( (resourceFile != null) && resourceFile.isDirectory() );
	}

    @Override
	public void apply(String inputPath, File inputFile, File outputFile)
		throws JakartaTransformException {

	    setResourceNames(inputPath, inputPath);
        transform(".", inputFile, outputFile);
	}

	protected void transform(
		String inputPath, File inputFile,
		File outputFile)  throws JakartaTransformException {

	    inputPath = inputPath + '/' + inputFile.getName();

	    // Note the asymmetry between the handling of the root directory, 
	    // which is selected by a composite action, and the handling of sub-directories,
	    // which are handled automatically by the directory action.
	    //
	    // This means that the directory action processes the entire tree
	    // of child directories.
	    //
	    // The alternative would be to put the directory action as a child of itself,
	    // and have sub-directories be accepted using composite action selection.

	    if ( inputFile.isDirectory() ) {
	    	if ( !outputFile.exists() ) {
	    		outputFile.mkdir();
	    	}

	    	for ( File childInputFile : inputFile.listFiles() ) {
	    		File childOutputFile = new File( outputFile, childInputFile.getName() );
	    		transform(inputPath, childInputFile, childOutputFile);
	    	}

	    } else {
	    	Action selectedAction = acceptAction(inputPath, inputFile);
	    	if ( selectedAction == null ) {
	    		recordUnaccepted(inputPath);
	    	} else if ( !select(inputPath) ) {
	    		recordUnselected(selectedAction, !ContainerChanges.HAS_CHANGES, inputPath);
	    	} else {
	    		selectedAction.apply(inputPath, inputFile, outputFile);
	    		recordTransform(selectedAction, inputPath);
	    	}
	    }
	}
}
