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

package tests;

import junit.framework.TestCase;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

public class TwoSchemasRefTest extends TestCase {

    public void testTwoSchemas() throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document doc = documentBuilderFactory.newDocumentBuilder().
                parse(Resources.asURI("twoSchemas-ref.wsdl"));

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		NodeList schemaNodes = doc.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema","schema");
        for (int j = 0; j < schemaNodes.getLength(); j++) {
        	Node schemaNode = schemaNodes.item(j);
        	if("schema".equals(schemaNode.getLocalName())){
        		schemaCol.read((Element)schemaNode);
        	}
        }

        XmlSchemaElement elementByQName = schemaCol.getElementByQName(new QName("http://tns.demo.org", "elem1"));
        assertNotNull(elementByQName);
        
    }
}
