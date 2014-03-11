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
 * Class for simple types and complex types with a simple content model.
 * Represents the World Wide Web Consortium (W3C) simpleContent element.
 */

public class XmlSchemaSimpleContent extends XmlSchemaContentModel {

    /**
     * Creates new XmlSchemaSimpleContent
     */
    public XmlSchemaSimpleContent() {
    }

    /*  One of XmlSchemaSimpleContentRestriction or XmlSchemaSimpleContentExtension. */
    XmlSchemaContent content;

    public XmlSchemaContent getContent() {
        return this.content;
    }

    public void setContent(XmlSchemaContent content) {
        this.content = content;
    }

    public String toString(String prefix, int tab) {
        String xml = new String();

        if (!prefix.equals("") && prefix.indexOf(":") == -1) {
            prefix += ":";
        }
        return xml;
    }
}
