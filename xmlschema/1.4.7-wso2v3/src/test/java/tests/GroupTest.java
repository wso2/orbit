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
public class GroupTest extends TestCase {

    /**
     * This method will test the group.
     *
     * @throws Exception Any exception encountered
     */
    public void testGroup() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types">

           <group name="priceGroup">
             <annotation>
               <documentation xml:lang="en">
                  A price is any one of the following:
                      * Full Price (with amount)
                      * Sale Price (with amount and authorization)
                      * Clearance Price (with amount and authorization)
                      * Free (with authorization)
               </documentation>
             </annotation>
             <choice id="pg.choice">
               <element name="fullPrice" type="decimal"/>
               <element name="salePrice" type="decimal"/>
               <element name="clearancePrice" type="decimal"/>
               <element name="freePrice" type="decimal"/>
             </choice>
           </group>
  
           <element name="price">
             <complexType>
               <group ref="tns:priceGroup" />
             </complexType>
           </element>

         </schema>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "price");
        InputStream is = new FileInputStream(Resources.asURI("group.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("price", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "price"),
                     elem.getQName());

        XmlSchemaComplexType cType = (XmlSchemaComplexType)elem.getSchemaType();
        assertNotNull(cType);

        XmlSchemaGroupRef ref = (XmlSchemaGroupRef)cType.getParticle();
        assertEquals(new QName("http://soapinterop.org/types", "priceGroup"),
                     ref.getRefName());

        XmlSchemaObjectTable t = schema.getGroups();
        assertEquals(1, t.getCount());

        Set s = new HashSet();
        s.add("priceGroup");
        for (Iterator i = t.getNames(); i.hasNext(); ) {
            String name = ((QName)i.next()).getLocalPart();
            assertEquals("priceGroup", name);
            s.remove(name);
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

        s.clear();
        s.add("org.apache.ws.commons.schema.XmlSchemaGroup");
        XmlSchemaGroup xsg = null;
        for (Iterator i = t.getValues(); i.hasNext(); ) {
            xsg = (XmlSchemaGroup)i.next();
            s.remove(xsg.getClass().getName());
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());
        
        assertNotNull(xsg);
        assertEquals("priceGroup", xsg.getName().getLocalPart());

        XmlSchemaChoice xsc = (XmlSchemaChoice)xsg.getParticle();
        assertNotNull(xsc);

        s.clear();
        s.add("fullPrice");
        s.add("salePrice");
        s.add("clearancePrice");
        s.add("freePrice");
        XmlSchemaObjectCollection items = xsc.getItems();
        Iterator iterator = items.getIterator();
        while (iterator.hasNext()) {
            XmlSchemaElement e = (XmlSchemaElement)iterator.next();
            String eName = e.getName();
            if (eName.equals("fullPrice")) {
                assertEquals(new QName("", "fullPrice"), e.getQName());
            } else if (eName.equals("salePrice")) {
                assertEquals(new QName("", "salePrice"), e.getQName());
            } else if (eName.equals("clearancePrice")) {
                assertEquals(new QName("", "clearancePrice"), e.getQName());
            } else if (eName.equals("freePrice")) {
                assertEquals(new QName("", "freePrice"), e.getQName());
            } else {
                fail("The name \"" + eName + "\" was found but shouldn't "
                     + "have been found.");
            }
            assertEquals(new QName("http://www.w3.org/2001/XMLSchema",
                                   "decimal"), e.getSchemaTypeName());
            assertTrue(s.remove(e.getName()));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

}
