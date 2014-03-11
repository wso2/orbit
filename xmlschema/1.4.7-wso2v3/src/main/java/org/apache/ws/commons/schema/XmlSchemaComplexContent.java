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

/**
 * Class that represents the complex content model for complex types.
 * Contains extensions or restrictions on a complex type that has mixed
 * content or elements only. Represents the World Wide Web Consortium (W3C)
 * complexContent element.
 */

// Vidyanand - 16th Oct - initial implementation

public class XmlSchemaComplexContent extends XmlSchemaContentModel {

    /**
     * Creates new XmlSchemaComplexContent
     */
    public XmlSchemaComplexContent() {
    }

    /* One of either the XmlSchemaComplexContentRestriction or 
	 * XmlSchemaComplexContentExtension classes.
	 */
    XmlSchemaContent content;

    public XmlSchemaContent getContent() {
        return this.content;
    }

    public void setContent(XmlSchemaContent content) {
        this.content = content;
    }

    /* Indicates that this type has a mixed content model. Character data
	 * is allowed to appear between the child elements of the complex type. 
	 */
    public boolean mixed;

    public boolean isMixed() {
        return this.mixed;
    }

    public void setMixed(boolean mixed) {
        this.mixed = mixed;
    }

    public String toString(String prefix, int tab) {
        String xml = new String();
        for (int i = 0; i < tab; i++)
            xml += "\t";

        if (!prefix.equals("") && prefix.indexOf(":") == -1)
            prefix += ":";

        xml += "<" + prefix + "complexContent>\n";

        xml += content.toString(prefix, (tab + 1));

        for (int i = 0; i < tab; i++)
            xml += "\t";
        xml += "<" + prefix + "complexContent>\n";
        return xml;
    }
}
