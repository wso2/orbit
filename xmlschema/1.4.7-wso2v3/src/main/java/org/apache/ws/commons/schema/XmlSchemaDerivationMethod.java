/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ws.commons.schema;

import org.apache.ws.commons.schema.constants.Constants;

/**
 * Provides different methods for preventing derivation.
 */
public class XmlSchemaDerivationMethod extends org.apache.ws.commons.schema.constants.Enum {
    static String[] members = new String[]{
            Constants.BlockConstants.ALL,
            Constants.BlockConstants.EMPTY,
            Constants.BlockConstants.EXTENSION,
            Constants.BlockConstants.LIST,
            Constants.BlockConstants.NONE,
            Constants.BlockConstants.RESTRICTION,
            Constants.BlockConstants.SUBSITUTION,
            Constants.BlockConstants.UNION};

    /**
     * Creates new XmlSeverityType
     */
    public XmlSchemaDerivationMethod() {
        super();
    }

    public XmlSchemaDerivationMethod(String value) {
        super(value);
    }

    public String[] getValues() {
        return members;
    }


}
