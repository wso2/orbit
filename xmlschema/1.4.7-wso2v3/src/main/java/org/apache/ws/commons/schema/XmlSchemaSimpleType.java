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
 * Class defines a simple type that determines the information and
 * constraints for the values of attributes or elements with text-only
 * content. Represents the World Wide Web Consortium (W3C) simpleType element.
 */

public class XmlSchemaSimpleType extends XmlSchemaType {

    XmlSchemaSimpleTypeContent content;

    /**
     * Creates new XmlSchemaSimpleType
     */
    public XmlSchemaSimpleType(XmlSchema schema) {
        super(schema);
    }

    public XmlSchemaSimpleTypeContent getContent() {
        return content;
    }

    public void setContent(XmlSchemaSimpleTypeContent content) {
        this.content = content;
    }

    public String toString(String prefix, int tab) {
        String xml = new String();

        for (int i = 0; i < tab; i++)
            xml += "\t";

        if (!prefix.equals("") && prefix.indexOf(":") == -1)
            prefix += ":";


        xml += "<" + prefix + "simpleType>\n";

        if (content != null)
            xml += content.toString(prefix, (tab + 1));

        for (int i = 0; i < tab; i++)
            xml += "\t";

        xml += "</" + prefix + "simpleType>\n";
        return xml;
    }

}

