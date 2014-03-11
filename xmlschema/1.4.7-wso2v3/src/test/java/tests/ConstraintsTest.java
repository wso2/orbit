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
 * @author Brent Ulbricht 
 */
public class ConstraintsTest extends TestCase {

    /**
     * This method will test the unique, key, and
     * keyref constaints.
     *
     * @throws Exception Any exception encountered
     */
    public void testConstraints() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types"
                 elementFormDefault="qualified">

           <element name="constraintTest">
             <complexType>
               <sequence>
                 <element name="manufacturers" type="tns:ManufacturerType"/>
                 <element name="products" type="tns:ProductType"/>
               </sequence>
             </complexType>

             <unique name="uniqueTest">
               <selector xpath="tns:manufacturers/tns:location"/>
               <field xpath="@district"/>
             </unique>

             <key name="keyTest">
               <selector xpath="tns:products/tns:productName"/>
               <field xpath="@productId"/>
             </key>

             <keyref name="keyRefTest" refer="tns:keyTest">
               <selector xpath="tns:manufacturers/tns:location/tns:productName"/>
               <field xpath="@productId"/>
             </keyref>

           </element>

           <complexType name="ManufacturerType">
             <sequence>
               <element name="location" maxOccurs="unbounded">
                 <complexType>
                   <sequence>
                     <element name="productName" maxOccurs="unbounded"/>
                       <complexType>
                         <complexContent>
                           <extension base="string">
                             <attribute name="productId" type="integer"/>
                             <attribute name="units" type="integer"/>
                           </extension>
                         </complexContent>
                       </complexType>
                     </element>
                   </sequence>
                   <attribute name="district" type="integer"/>
                 </complexType>
               </element>
             </sequence>
           </complexType>

           <complexType name="ProductType">
             <sequence>
               <element name="productName" maxOccurs="unbounded">
                 <complexType>
                   <simpleContent>
                     <extension base="string">
                       <attribute name="productId" type="integer"/>
                     </extension>
                   </simpleContent>
                 </complexType>
               </element>
             </sequence>
           </complexType>

         </schema>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "constraintTest");
        InputStream is = new FileInputStream(Resources.asURI("constraints.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("constraintTest", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "constraintTest"),
                     elem.getQName());

        XmlSchemaObjectCollection c = elem.getConstraints();
        assertEquals(3, c.getCount());
                             
        Set s = new HashSet();
        s.add(XmlSchemaKey.class.getName());
        s.add(XmlSchemaKeyref.class.getName());
        s.add(XmlSchemaUnique.class.getName());
        for (int i = 0; i < c.getCount(); i++) {
            Object o = c.getItem(i);
            if (o instanceof XmlSchemaKey) {
                XmlSchemaKey key = (XmlSchemaKey)o;
                assertEquals("keyTest", key.getName());
                
                XmlSchemaXPath selectorXpath = key.getSelector();
                assertEquals("tns:products/tns:productName",
                             selectorXpath.getXPath());
                
                XmlSchemaObjectCollection fields = key.getFields();
                assertEquals(1, fields.getCount());
                XmlSchemaXPath fieldXpath = null;
                for (int j = 0; j < fields.getCount(); j++) {
                    fieldXpath = (XmlSchemaXPath)fields.getItem(j);
                }
                assertNotNull(fieldXpath);
                assertEquals("@productId", fieldXpath.getXPath());
            } else if (o instanceof XmlSchemaKeyref) {
                XmlSchemaKeyref keyref = (XmlSchemaKeyref)o;
                assertNotNull(keyref);
                assertEquals("keyRefTest", keyref.getName());
                assertEquals(new QName("http://soapinterop.org/types",
                                       "keyTest"),
                             keyref.getRefer());
                
                XmlSchemaXPath selectorXpath = keyref.getSelector();
                assertEquals("tns:manufacturers/tns:location/tns:productName",
                             selectorXpath.getXPath());

                XmlSchemaObjectCollection fields = keyref.getFields();
                assertEquals(1, fields.getCount());
                XmlSchemaXPath fieldXpath = null;
                for (int j = 0; j < fields.getCount(); j++) {
                    fieldXpath = (XmlSchemaXPath)fields.getItem(j);
                }
                assertNotNull(fieldXpath);
                assertEquals("@productId", fieldXpath.getXPath());
            } else if (o instanceof XmlSchemaUnique) {
                XmlSchemaUnique unique = (XmlSchemaUnique)o;
                assertNotNull(unique);
                assertEquals("uniqueTest", unique.getName());
                XmlSchemaXPath selectorXpath = unique.getSelector();
                assertEquals("tns:manufacturers/tns:location",
                             selectorXpath.getXPath());

                XmlSchemaObjectCollection fields = unique.getFields();
                assertEquals(1, fields.getCount());
                XmlSchemaXPath fieldXpath = null;
                for (int j = 0; j < fields.getCount(); j++) {
                    fieldXpath = (XmlSchemaXPath)fields.getItem(j);
                }
                assertNotNull(fieldXpath);
                assertEquals("@district", fieldXpath.getXPath());
            } else {
                fail("An unexpected constraint of \""
                     + o.getClass().getName() + "\" was found.");
            }
            s.remove(o.getClass().getName());
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());
        
    }

}
