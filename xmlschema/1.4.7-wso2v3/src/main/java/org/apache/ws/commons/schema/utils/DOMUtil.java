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

import org.w3c.dom.*;

import java.lang.reflect.Method;

/**
 * Some useful utility methods.
 * This class was modified in Xerces2 with a view to abstracting as
 * much as possible away from the representation of the underlying
 * parsed structure (i.e., the DOM).  This was done so that, if Xerces
 * ever adopts an in-memory representation more efficient than the DOM
 * (such as a DTM), we should easily be able to convert our schema
 * parsing to utilize it.
 *
 * @version $ID DOMUtil
 */
public class DOMUtil {

    private static final String DEFAULT_ENCODING = "UTF-8";

	//
    // Constructors
    //

    /**
     * This class cannot be instantiated.
     */
    protected DOMUtil() {
    }

    //
    // Public static methods
    //

    /**
     * Finds and returns the first child element node.
     */
    public static Element getFirstChildElement(Node parent) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) child;
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node):Element

    /**
     * Finds and returns the last child element node.
     */
    public static Element getLastChildElement(Node parent) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) child;
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node):Element


    /**
     * Finds and returns the next sibling element node.
     */
    public static Element getNextSiblingElement(Node node) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) sibling;
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingElement(Node):Element

    /**
     * Finds and returns the first child node with the given name.
     */
    public static Element getFirstChildElement(Node parent, String elemName) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(elemName)) {
                    return (Element) child;
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node,String):Element

    /**
     * Finds and returns the last child node with the given name.
     */
    public static Element getLastChildElement(Node parent, String elemName) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(elemName)) {
                    return (Element) child;
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node,String):Element

    /**
     * Finds and returns the next sibling node with the given name.
     */
    public static Element getNextSiblingElement(Node node, String elemName) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                if (sibling.getNodeName().equals(elemName)) {
                    return (Element) sibling;
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElement(Node,String):Element

    /**
     * Finds and returns the first child node with the given qualified name.
     */
    public static Element getFirstChildElementNS(Node parent,
                                                 String uri, String localpart) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) &&
                        child.getLocalName().equals(localpart)) {
                    return (Element) child;
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElementNS(Node,String,String):Element

    /**
     * Finds and returns the last child node with the given qualified name.
     */
    public static Element getLastChildElementNS(Node parent,
                                                String uri, String localpart) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) &&
                        child.getLocalName().equals(localpart)) {
                    return (Element) child;
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElementNS(Node,String,String):Element

    /**
     * Finds and returns the next sibling node with the given qualified name.
     */
    public static Element getNextSiblingElementNS(Node node,
                                                  String uri, String localpart) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                String siblingURI = sibling.getNamespaceURI();
                if (siblingURI != null && siblingURI.equals(uri) &&
                        sibling.getLocalName().equals(localpart)) {
                    return (Element) sibling;
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElementNS(Node,String,String):Element

    /**
     * Finds and returns the first child node with the given name.
     */
    public static Element getFirstChildElement(Node parent, String elemNames[]) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element) child;
                    }
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node,String[]):Element

    /**
     * Finds and returns the last child node with the given name.
     */
    public static Element getLastChildElement(Node parent, String elemNames[]) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element) child;
                    }
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node,String[]):Element

    /**
     * Finds and returns the next sibling node with the given name.
     */
    public static Element getNextSiblingElement(Node node, String elemNames[]) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (sibling.getNodeName().equals(elemNames[i])) {
                        return (Element) sibling;
                    }
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElement(Node,String[]):Element

    /**
     * Finds and returns the first child node with the given qualified name.
     */
    public static Element getFirstChildElementNS(Node parent,
                                                 String[][] elemNames) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                            child.getLocalName().equals(elemNames[i][1])) {
                        return (Element) child;
                    }
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElementNS(Node,String[][]):Element

    /**
     * Finds and returns the last child node with the given qualified name.
     */
    public static Element getLastChildElementNS(Node parent,
                                                String[][] elemNames) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                            child.getLocalName().equals(elemNames[i][1])) {
                        return (Element) child;
                    }
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElementNS(Node,String[][]):Element

    /**
     * Finds and returns the next sibling node with the given qualified name.
     */
    public static Element getNextSiblingElementNS(Node node,
                                                  String[][] elemNames) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = sibling.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                            sibling.getLocalName().equals(elemNames[i][1])) {
                        return (Element) sibling;
                    }
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElementNS(Node,String[][]):Element

    /**
     * Finds and returns the first child node with the given name and
     * attribute name, value pair.
     */
    public static Element getFirstChildElement(Node parent,
                                               String elemName,
                                               String attrName,
                                               String attrValue) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                if (element.getNodeName().equals(elemName) &&
                        element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node,String,String,String):Element

    /**
     * Finds and returns the last child node with the given name and
     * attribute name, value pair.
     */
    public static Element getLastChildElement(Node parent,
                                              String elemName,
                                              String attrName,
                                              String attrValue) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                if (element.getNodeName().equals(elemName) &&
                        element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node,String,String,String):Element

    /**
     * Finds and returns the next sibling node with the given name and
     * attribute name, value pair. Since only elements have attributes,
     * the node returned will be of type Node.ELEMENT_NODE.
     */
    public static Element getNextSiblingElement(Node node,
                                                String elemName,
                                                String attrName,
                                                String attrValue) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) sibling;
                if (element.getNodeName().equals(elemName) &&
                        element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingElement(Node,String,String,String):Element

    /**
     * Returns the concatenated child text of the specified node.
     * This method only looks at the immediate children of type
     * <code>Node.TEXT_NODE</code> or the children of any child
     * node that is of type <code>Node.CDATA_SECTION_NODE</code>
     * for the concatenation.
     *
     * @param node The node to look at.
     */
    public static String getChildText(Node node) {

        // is there anything to do?
        if (node == null) {
            return null;
        }

        // concatenate children text
        StringBuffer str = new StringBuffer();
        Node child = node.getFirstChild();
        while (child != null) {
            short type = child.getNodeType();
            if (type == Node.TEXT_NODE) {
                str.append(child.getNodeValue());
            } else if (type == Node.CDATA_SECTION_NODE) {
                str.append(getChildText(child));
            }
            child = child.getNextSibling();
        }

        // return text value
        return str.toString();

    } // getChildText(Node):String

    // return the name of this element
    public static String getName(Node node) {
        return node.getNodeName();
    } // getLocalName(Element):  String

    /**
     * returns local name of this element if not null, otherwise
     * returns the name of the node
     */
    public static String getLocalName(Node node) {
        String name = node.getLocalName();
        return (name != null) ? name : node.getNodeName();
    } // getLocalName(Element):  String

    public static Element getParent(Element elem) {
        Node parent = elem.getParentNode();
        if (parent instanceof Element)
            return (Element) parent;
        return null;
    } // getParent(Element):Element

    // get the Document of which this Node is a part
    public static Document getDocument(Node node) {
        return node.getOwnerDocument();
    } // getDocument(Node):Document

    // return this Document's root node
    public static Element getRoot(Document doc) {
        return doc.getDocumentElement();
    } // getRoot(Document(:  Element

    // some methods for handling attributes:

    // return the right attribute node
    public static Attr getAttr(Element elem, String name) {
        return elem.getAttributeNode(name);
    } // getAttr(Element, String):Attr

    // return the right attribute node
    public static Attr getAttrNS(Element elem, String nsUri,
                                 String localName) {
        return elem.getAttributeNodeNS(nsUri, localName);
    } // getAttrNS(Element, String):Attr

    // get all the attributes for an Element
    public static Attr[] getAttrs(Element elem) {
        NamedNodeMap attrMap = elem.getAttributes();
        Attr[] attrArray = new Attr[attrMap.getLength()];
        for (int i = 0; i < attrMap.getLength(); i++)
            attrArray[i] = (Attr) attrMap.item(i);
        return attrArray;
    } // getAttrs(Element):  Attr[]

    // get attribute's value
    public static String getValue(Attr attribute) {
        return attribute.getValue();
    } // getValue(Attr):String

    // It is noteworthy that, because of the way the DOM specs
    // work, the next two methods return the empty string (not
    // null!) when the attribute with the specified name does not
    // exist on an element.  Beware!

    // return the value of the attribute of the given element
    // with the given name
    public static String getAttrValue(Element elem, String name) {
        return elem.getAttribute(name);
    } // getAttr(Element, String):Attr

    // return the value of the attribute of the given element
    // with the given name
    public static String getAttrValueNS(Element elem, String nsUri,
                                        String localName) {
        return elem.getAttributeNS(nsUri, localName);
    } // getAttrValueNS(Element, String):Attr

    // return the namespace URI
    public static String getNamespaceURI(Node node) {
        return node.getNamespaceURI();
    }

    /**
     * Get the input encoding of the document. This uses a DOM 3 API
     * call getInputEncoding hence it returns the correct value
     * only if a DOM3 API is used. Otherwise it returns the default encoding
     * @param doc
     * @return the encoding (e.g. UTF-8)
     */
    public static String getInputEncoding(Document doc) {
        try {
            Method m = doc.getClass().getMethod("getInputEncoding", new Class[]{});
            return (String) m.invoke(doc, new Object[]{});
        } catch (Throwable e) {
            return DEFAULT_ENCODING;
        }
    }
    
    /**
     * Get the xml encoding of the document. This uses a DOM 3 API
     * call getXmlEncoding hence it returns the correct value
     * only if a DOM3 API is used. Otherwise it returns the default encoding
     * @see #getInputEncoding(Document)
     * @param doc
     * @return the encoding (e.g. utf-8).
     */
    public static String getXmlEncoding(Document doc) {
        try {
        	 Method m = doc.getClass().getMethod("getXmlEncoding", new Class[]{});
             return (String) m.invoke(doc, new Object[]{});
        } catch (Throwable e) {
            return DEFAULT_ENCODING;
        }
    }
} 
