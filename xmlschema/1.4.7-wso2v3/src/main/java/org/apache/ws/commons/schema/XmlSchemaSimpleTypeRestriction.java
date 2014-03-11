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
 * Class for the restriction of simpleType elements. Represents the World
 * Wide Web Consortium (W3C) restriction element for simple types.
 */

public class XmlSchemaSimpleTypeRestriction extends XmlSchemaSimpleTypeContent {

    /**
     * Creates new XmlSchemaSimpleTypeRestriction
     */
    public XmlSchemaSimpleTypeRestriction() {
        facets = new XmlSchemaObjectCollection();
    }

    XmlSchemaSimpleType baseType;

    public XmlSchemaSimpleType getBaseType() {
        return this.baseType;
    }

    public void setBaseType(XmlSchemaSimpleType baseType) {
        this.baseType = baseType;
    }

    QName baseTypeName;

    public QName getBaseTypeName() {
        return this.baseTypeName;
    }

    public void setBaseTypeName(QName baseTypeName) {
        this.baseTypeName = baseTypeName;
    }

    XmlSchemaObjectCollection facets;// = new XmlSchemaObjectCollection();

    public XmlSchemaObjectCollection getFacets() {
        return this.facets;
    }

    public String toString(String prefix, int tab) {
        String xml = new String();

        if (!prefix.equals("") && prefix.indexOf(":") == -1)
            prefix += ":";

        for (int i = 0; i < tab; i++)
            xml += "\t";

        xml += "<" + prefix + "restriction ";

        if (baseTypeName != null) {
            xml += "base =\"" + baseTypeName + "\">\n";
        } else {
            xml += ">\n";
            // inline def
            xml += baseType.toString(prefix, (tab + 1));
        }

        xml += facets.toString(prefix, (tab + 1));
        for (int i = 0; i < tab; i++)
            xml += "\t";
        xml += "</" + prefix + "restriction>\n";


        return xml;

    }

}
