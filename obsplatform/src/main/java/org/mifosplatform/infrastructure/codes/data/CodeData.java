/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.codes.data;

/**
 * Immutable data object representing a code.
 */
public class CodeData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final boolean systemDefined;
    @SuppressWarnings("unused")
	private final String description;
    private final String module;
    
    public static CodeData instance(final Long id, final String name, final String description,final boolean systemDefined, final String module) {
        return new CodeData(id, name, description,systemDefined,module);
    }

    private CodeData(final Long id, final String name, final String description,final boolean systemDefined, final String module) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.systemDefined = systemDefined;
        this.module = module;
    }

	public Long getCodeId() {
		 return this.id;       
   }
}