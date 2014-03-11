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

import javax.xml.namespace.QName;


/**
 * Class for the attribute group reference. Represents the World Wide
 * Web Consortium (W3C) attributeGroup element with the ref attribute.
 */
public class XmlSchemaAttributeGroupRef extends XmlSchemaAnnotated {

    /**
     * Creates new XmlSchemaAttributeGroupRef
     */
    public XmlSchemaAttributeGroupRef() {
    }

    QName refName;

    public QName getRefName() {
        return this.refName;
    }

    public void setRefName(QName refName) {
        this.refName = refName;
    }

}
