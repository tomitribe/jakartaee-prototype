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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.transformer.TransformProperties;
import org.eclipse.transformer.action.SelectionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectionRuleImpl implements SelectionRule {
    
    static Logger logger = LoggerFactory.getLogger(SelectionRuleImpl.class);

	public SelectionRuleImpl(
		Set<String> includes, Set<String> excludes) {

		this.included = new HashSet<String>(includes);
		this.includedExact = new HashSet<String>();
		this.includedHead = new HashSet<String>();
		this.includedTail = new HashSet<String>();
		this.includedAny = new HashSet<String>();

		TransformProperties.processSelections(
			this.included,
			this.includedExact, this.includedHead, this.includedTail, this.includedAny );

		this.excluded = new HashSet<String>(excludes);
		this.excludedExact = new HashSet<String>();
		this.excludedHead = new HashSet<String>();
		this.excludedTail = new HashSet<String>();
		this.excludedAny = new HashSet<String>();

		TransformProperties.processSelections(
			this.excluded,
			this.excludedExact, this.excludedHead, this.excludedTail, this.excludedAny );
	}

	//

	private final Set<String> included;
	private final Set<String> includedExact;
	private final Set<String> includedHead;
	private final Set<String> includedTail;
	private final Set<String> includedAny;
	
	private final Set<String> excluded;
	private final Set<String> excludedExact;
	private final Set<String> excludedHead;
	private final Set<String> excludedTail;	
	private final Set<String> excludedAny;	

	@Override
	public boolean select(String resourceName) {
		boolean isIncluded = selectIncluded(resourceName);
		boolean isExcluded = rejectExcluded(resourceName);

		return ( isIncluded && !isExcluded );
	}

	@Override
	public boolean selectIncluded(String resourceName) {
		if ( included.isEmpty() ) {
			logger.debug("Include [ %s ]: %s\n", resourceName, "No includes");
			return true;

		} else if ( includedExact.contains(resourceName) ) {
			logger.debug("Include [ %s ]: %s\n", resourceName, "Exact include");
			return true;

		} else {
			for ( String tail : includedHead ) {
				if ( resourceName.endsWith(tail) ) {
					logger.debug("Include [ %s ]: %s (%s)\n", resourceName, "Match tail", tail);
					return true;
				}
			}
			for ( String head : includedTail ) {
				if ( resourceName.startsWith(head) ) {
					logger.debug("Include [ %s ]: %s (%s)\n", resourceName, "Match head", head);
					return true;
				}
			}
			for ( String middle : includedAny ) {
				if ( resourceName.contains(middle) ) {
					logger.debug("Include [ %s ]: %s (%s)\n", resourceName, "Match middle", middle);
					return true;
				}
			}

			logger.debug("Do not include [ %s ]\n", resourceName);
			return false;
		}
	}

	@Override
	public boolean rejectExcluded(String resourceName ) {
		if ( excluded.isEmpty() ) {
			logger.debug("Do not exclude[ %s ]: %s\n", resourceName, "No excludes");
			return false;

		} else if ( excludedExact.contains(resourceName) ) {
			logger.debug("Exclude [ %s ]: %s\n", resourceName, "Exact exclude");
			return true;

		} else {
			for ( String tail : excludedHead ) {
				if ( resourceName.endsWith(tail) ) {
					logger.debug("Exclude[ %s ]: %s (%s)\n", resourceName, "Match tail", tail);
					return true;
				}
			}
			for ( String head : excludedTail ) {
				if ( resourceName.startsWith(head) ) {
					logger.debug("Exclude[ %s ]: %s (%s)\n", resourceName, "Match head", head);
					return true;
				}
			}
			for ( String middle : excludedAny ) {
				if ( resourceName.contains(middle) ) {
					logger.debug("Exclude[ %s ]: %s (%s)\n", resourceName, "Match middle", middle);
					return true;
				}
			}

			logger.debug("Do not exclude[ %s ]\n", resourceName);
			return false;
		}
	}
}
