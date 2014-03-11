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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to return a node list without thread-safety issue.
 *
 */
class DocumentFragmentNodeList implements NodeList {
	private List nodes;
	private DocumentFragment fragment;
	
	/**
	 * Construct a list of the children of a given node.
	 * @param parentNode node from which to copy children.
	 */
	DocumentFragmentNodeList(Node parentNode) {
		fragment = parentNode.getOwnerDocument().createDocumentFragment();
		nodes = new ArrayList();
		for(Node child = parentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			nodes.add(fragment.appendChild(child.cloneNode(true)));
		}
	}

	/**
	 * Create a list of the children of a given node that are elements with a specified qualified name.
	 * @param parentNode node from which to copy children.
	 * @param filterUri Namespace URI of children to copy.
	 * @param filterLocal Local name of children to copy.
	 */
	DocumentFragmentNodeList(Node parentNode, String filterUri, String filterLocal) {
		fragment = parentNode.getOwnerDocument().createDocumentFragment();
		nodes = new ArrayList();
		for(Node child = parentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if(child.getNodeType() == Node.ELEMENT_NODE 
					&& child.getNamespaceURI().equals(filterUri)
					&& child.getLocalName().equals(filterLocal)) {
				nodes.add(fragment.appendChild(child.cloneNode(true)));
			}
		}
	}

	public int getLength() {
		return nodes.size(); 
	}

	public Node item(int index) {
		if(nodes == null) {
			return null;
		} else {
			return (Node) nodes.get(index);
		}
	}

}
