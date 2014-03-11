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
 * Class for simple types that are derived by restriction. Restricts the
 * range of values for the element to a subset of the inherited simple types.
 * Represents the World Wide Web Consortium (W3C) restriction element for simple content.
 */

public class XmlSchemaSimpleContentRestriction extends XmlSchemaContent {

    /**
     * Creates new XmlSchemaSimpleContentRestriction
     */
    public XmlSchemaSimpleContentRestriction() {
        facets = new XmlSchemaObjectCollection();
        attributes = new XmlSchemaObjectCollection();
    }

    /* Allows an XmlSchemaAnyAttribute to be used for the attribute value.*/
    XmlSchemaAnyAttribute anyAttribute;

    public void setAnyAttribute(XmlSchemaAnyAttribute anyAttribute) {
        this.anyAttribute = anyAttribute;
    }

    public XmlSchemaAnyAttribute getAnyAttribute() {
        return this.anyAttribute;
    }

    /* Contains XmlSchemaAttribute and XmlSchemaAttributeGroupRef. Collection of attributes for the simple type.*/
    XmlSchemaObjectCollection attributes;

    public XmlSchemaObjectCollection getAttributes() {
        return this.attributes;
    }

    /*Derived from the type specified by the base value.*/
    XmlSchemaSimpleType baseType;

    public void setBaseType(XmlSchemaSimpleType baseType) {
        this.baseType = baseType;
    }

    public XmlSchemaSimpleType getBaseType() {
        return this.baseType;
    }

    /* Name of the built-in data type, simple type, or complex type.*/
    QName baseTypeName;

    public void setBaseTypeName(QName baseTypeName) {
        this.baseTypeName = baseTypeName;
    }

    public QName getBaseTypeName() {
        return this.baseTypeName;
    }

    /* One or more of the facet classes: */
    XmlSchemaObjectCollection facets;

    public XmlSchemaObjectCollection getFacets() {
        return this.facets;
    }

}
			
