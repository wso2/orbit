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

package org.apache.ws.commons.schema.utils;

import org.apache.ws.commons.schema.constants.Constants;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Searches for namespace prefix declarations.
 */
public abstract class PrefixCollector {
    /**
     * Records a single namespace prefix declaration.
     */
    protected abstract void declare(String pPrefix, String pNamespaceURI);

    /**
     * Searches for namespace prefix declarations in the given node.
     * For any prefix declaration, it invokes {@link #declare(String, String)}.
     * This method doesn't work recursively: The parent nodes prefix
     * declarations are ignored.
     */
    public void searchLocalPrefixDeclarations(Node pNode) {
        short type = pNode.getNodeType();
        if (type == Node.ELEMENT_NODE || type == Node.DOCUMENT_NODE) {
            NamedNodeMap map = pNode.getAttributes();
            for (int i = 0; map != null && i < map.getLength(); i++) {
                Node attr = map.item(i);
                final String uri = attr.getNamespaceURI();
                if (Constants.XMLNS_ATTRIBUTE_NS_URI.equals(uri)) {
                    String localName = attr.getLocalName();
                    String prefix = Constants.XMLNS_ATTRIBUTE.equals(localName) ? Constants.DEFAULT_NS_PREFIX : localName;
                    declare(prefix, attr.getNodeValue());
                }
            }
        }
    }

    /**
     * Searches for namespace prefix declarations in the given node.
     * For any prefix declaration, it invokes {@link #declare(String, String)}.
     * This method works recursively: The parent nodes prefix
     * declarations are collected before the current nodes.
     */
    public void searchAllPrefixDeclarations(Node pNode) {
        Node parent = pNode.getParentNode();
        if (parent != null) {
            searchAllPrefixDeclarations(parent);
        }
        searchLocalPrefixDeclarations(pNode);
    }
}
