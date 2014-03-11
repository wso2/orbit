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

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 * Copyright 2004,2007 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
public class ChoiceTest extends TestCase {

    /**
     * This method will test the choice.
     *
     * @throws Exception Any exception encountered
     */
    public void testChoice() throws Exception {

        /*
        <schema xmlns="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:tns="http://soapinterop.org/types"
                targetNamespace="http://soapinterop.org/types">

          <element name="computer">
            <complexType>
              <choice>
                <element name="desktop" type="string"/>
                <element name="laptop" type="string"/>
              </choice>
            </complexType>
          </element>

        </schema>
        */

        QName computerElementQname = new QName("http://soapinterop.org/types",
                                        "computer");      
        QName mbElementQname = new QName("http://soapinterop.org/types",
                                        "motherboard");
        
        InputStream is = new FileInputStream(Resources.asURI("choice.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        QName WRONG_QNAME = new QName("http://soapinterop.org/types",
                                      "machine");
        XmlSchemaElement elem = schemaCol.getElementByQName(WRONG_QNAME);
        assertNull(elem);
        elem = schemaCol.getElementByQName(computerElementQname);
        assertEquals("computer", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "computer"),
                     elem.getQName());

        XmlSchemaComplexType cType = (XmlSchemaComplexType)elem.getSchemaType();
        assertNotNull(cType);

        XmlSchemaChoice choice = (XmlSchemaChoice)cType.getParticle();
        assertNotNull(choice);

        Set s = new HashSet();
        s.add("desktop");
        s.add("laptop");
        XmlSchemaObjectCollection items = choice.getItems();
        Iterator iterator = items.getIterator();
        while (iterator.hasNext()) {
            XmlSchemaElement e = (XmlSchemaElement)iterator.next();
            String eName = e.getName();
            if (eName.equals("desktop")) {
                assertEquals(new QName("", "desktop"), e.getQName());
                assertEquals(e.getName(), "desktop");
            } else if (eName.equals("laptop")) {
                assertEquals(new QName("", "laptop"), e.getQName());
                assertEquals(e.getName(), "laptop");
            } else {
                fail("Should have had a name of desktop or laptop, but"
                     + " instead had " + eName);
            }
            assertEquals(new QName("http://www.w3.org/2001/XMLSchema",
                                   "string"), e.getSchemaTypeName());
            assertTrue(s.remove(e.getName()));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

        //test min and max occurs
        elem = schemaCol.getElementByQName(mbElementQname);
        assertEquals("motherboard", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "motherboard"),
                     elem.getQName());

        cType = (XmlSchemaComplexType)elem.getSchemaType();
        assertNotNull(cType);

        choice = (XmlSchemaChoice)cType.getParticle();
        assertNotNull(choice);

        //values from the XML file
        assertEquals(choice.getMinOccurs(),1);
        assertEquals(choice.getMaxOccurs(),6);

    }

}
