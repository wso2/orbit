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
import org.apache.ws.commons.schema.*;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Iterator;

public class TestUnqualifiedSchema extends TestCase {


    public void testUnqualifiedSchemas() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document doc = documentBuilderFactory.newDocumentBuilder().
                parse(Resources.asURI("unqualifiedTypes.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema s = schemaCol.read(doc.getDocumentElement());

        assertNotNull(s);

        XmlSchemaElement e=s.getElementByName(new QName("http://soapinterop.org/xsd","complexElt") );
        XmlSchemaComplexType t  =(XmlSchemaComplexType)e.getSchemaType();
        assertNotNull(t);

        XmlSchemaSequence seq = (XmlSchemaSequence)t.getParticle();
        XmlSchemaObjectCollection items = seq.getItems();
        Iterator iterator = items.getIterator();
        while (iterator.hasNext()) {
            XmlSchemaElement elt2 = (XmlSchemaElement) iterator.next();
            System.out.println(elt2.getQName());
            XmlSchemaType schemaType2 = elt2.getSchemaType();

            assertNotNull(schemaType2);
        }



    }


}
