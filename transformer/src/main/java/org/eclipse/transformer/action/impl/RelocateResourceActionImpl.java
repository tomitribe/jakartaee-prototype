/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.transformer.action.impl;

import org.eclipse.transformer.TransformException;
import org.eclipse.transformer.action.ActionType;
import org.eclipse.transformer.util.ByteData;
import org.slf4j.Logger;

import java.io.File;

/**
 * Move files that aren't class or java files, but are in affected packages to the correct location.
 */
public class RelocateResourceActionImpl extends ActionImpl {

    public RelocateResourceActionImpl(
        Logger logger, boolean isTerse, boolean isVerbose,
        InputBufferImpl buffer,
        SelectionRuleImpl selectionRule,
        SignatureRuleImpl signatureRule) {

        super(logger, isTerse, isVerbose, buffer, selectionRule, signatureRule);
    }

    @Override
    public String getAcceptExtension() {
        return "";
    }

    public String getName() {
        return "Relocate Resource Action";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.RESOURCE;
    }

    @Override
    protected ChangesImpl newChanges() {
        return new ChangesImpl();
    }

    @Override
    public ChangesImpl getLastActiveChanges() {
        return (ChangesImpl) super.getLastActiveChanges();
    }

    @Override
    public ChangesImpl getActiveChanges() {
        return (ChangesImpl) super.getActiveChanges();
    }

    @Override
    public boolean accept(String resourceName, File resourceFile) {
        return !resourceName.endsWith("/");
    }

    @Override
    public ByteData apply(String inputName, byte[] inputBytes, int inputLength) throws TransformException {

        String inputResourceName = this.getResourceName(inputName);
        String outputResourceName = transformBinaryType(inputResourceName);

        String outputName;
        if (outputResourceName != null) {
            outputName = inputName.substring(0, inputName.length() - inputResourceName.length()) + outputResourceName;
            verbose("Resource name [ {} ] -> [ {} ]", inputName, outputName);
        } else {
            outputName = inputName;
        }

        setResourceNames(inputName, outputName);
        return new ByteData(outputName, inputBytes, 0, inputLength);
    }

    private String getResourceName(final String inputPath) {
        if (inputPath == null || inputPath.trim().length() == 0) {
            return inputPath;
        }

        if (inputPath.startsWith("WEB-INF/classes/")) {
            return inputPath.substring("WEB-INF/classes/".length());
        } else if (inputPath.startsWith("META-INF/versions/")) {
            int nextSlash = inputPath.indexOf('/', "META-INF/versions/".length());
            if (nextSlash == -1) {
                return inputPath.substring("META-INF/versions/".length());
            } else {
                return inputPath.substring(nextSlash + 1);
            }
        } else if (inputPath.startsWith("META-INF/")) {
            return inputPath.substring("META-INF/".length());
        }

        return inputPath;
    }
}
