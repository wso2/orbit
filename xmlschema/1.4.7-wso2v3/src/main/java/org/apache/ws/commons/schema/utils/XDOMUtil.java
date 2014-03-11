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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XDOMUtil extends DOMUtil {

    /**
     * Creates a new instance of XDOMUtil
     */
    private XDOMUtil() {
    }

    public static Element getFirstChildElementNS(Node parent, String uri) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri)) {
                    return (Element) child;
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    }

    public static Element getNextSiblingElementNS(Node node, String uri) {
        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                String siblingURI = sibling.getNamespaceURI();
                if (siblingURI != null && siblingURI.equals(uri)) {
                    return (Element) sibling;
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    }
    
    public static boolean anyElementsWithNameNS(Element element, String uri, String name) {
    	for (Element el = getFirstChildElementNS(element, uri); el != null; el = XDOMUtil.getNextSiblingElementNS(el, uri)) {
    		if(el.getLocalName().equals(name) && el.getNamespaceURI().equals(uri)) {
    			return true;
    		}
    	}
    	return false;
    }
    

}
