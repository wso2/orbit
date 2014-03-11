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

import org.w3c.dom.NodeList;

/**
 * Class that specifies information to be read or used by humans
 * within an annotation. Represents the World Wide Web Consortium
 * (W3C) documentation element.
 */

public class XmlSchemaDocumentation extends XmlSchemaObject {

    /**
     * Creates new XmlSchemaDocumentation
     */
    public XmlSchemaDocumentation() {
    }

    /**
     * Provides the source of the application information.
     */
    String source;
    String language;

    /**
     * Returns an array of XmlNode that represents the document text markup.
     */
    NodeList markup;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public NodeList getMarkup() {
        return markup;
    }

    public void setMarkup(NodeList markup) {
        this.markup = markup;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
