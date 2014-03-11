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
package tests.w3c;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class representing a single schema test as described in a .testSet file.
 *
 */
public class SchemaTest {

    private final static String SCHEMA_DOCUMENT = "schemaDocument";

    private final static String EXPECTED = "expected";

    private final static String CURRENT = "current";

    String schemaDocumentLink = null;

    private String expectedValidity = null;

    String currentStatus = null;

    String currentDate = null;

    public SchemaTest(Element n) throws Exception {
        NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node c = nl.item(i);
            if (!(c instanceof Element))
                continue;
            Element elem = (Element) c;
            String elemName = elem.getNodeName();
            if (elemName.equals(SCHEMA_DOCUMENT)) {
                 schemaDocumentLink = elem.getAttributeNS(
                        "http://www.w3.org/1999/xlink", "href");
                 
                 // Workaround for mistake in the NISTXMLSchema1-0-20020116.testSet file
                 // See http://lists.w3.org/Archives/Public/www-xml-schema-comments/2006JulSep/0000.html
                 if (schemaDocumentLink.equals("./NISTTestsAll/NISTSchema-anyURI-maxLength-1.xsd")) {
                     schemaDocumentLink = "./nisttest/NISTTestsAll/NISTSchema-anyURI-maxLength-1.xsd";
                 }
            }

            if (elemName.equals(EXPECTED)) {
                expectedValidity = elem.getAttribute("validity");
            }

            if (elemName.equals(CURRENT)) {
                currentStatus = elem.getAttribute("status");
                currentDate = elem.getAttribute("date");
            }
        }
    }

    public boolean isValid() {
        return expectedValidity.equals("valid");
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer("href=");
        sb.append(schemaDocumentLink);
        sb.append(" expectedValidity=");
        sb.append(expectedValidity);
        sb.append(" currentStatus=");
        sb.append(currentStatus);
        sb.append(" currentDate=");
        sb.append(currentDate);
        
        return sb.toString();
    }
}