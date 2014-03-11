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
 */
public class SimpleContentRestrictionTest extends TestCase {

    /**
     * This method will test the simple content restriction.
     *
     * @throws Exception Any exception encountered
     */
    public void testSimpleContentRestriction() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types"
                 attributeFormDefault="qualified">
  
           <simpleType name="drinksize">
             <restriction base="string">
               <enumeration value="small"/>
               <enumeration value="medium"/>
               <enumeration value="large"/>
             </restriction>
           </simpleType>

           <complexType name="dietdrinksize">
             <simpleContent>
               <restriction base="tns:drinksize">
                 <enumeration value="small"/>
                 <enumeration value="medium"/>
                 <attribute name="units" type="string" use="required"/>
                 <attribute name="id" type="integer" use="required" default="001"/>
               </restriction>
             </simpleContent>
           </complexType>
           
         </schema>
        */                                                                      

        QName TYPE_QNAME = new QName("http://soapinterop.org/types",
                                     "dietdrinksize");
        InputStream is = new FileInputStream(Resources.asURI("screstriction.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaComplexType xsct =
            (XmlSchemaComplexType)schema.getTypeByName(TYPE_QNAME);
        assertNotNull(xsct);

        XmlSchemaSimpleContent xssc = (XmlSchemaSimpleContent)xsct.getContentModel();
        assertNotNull(xssc);
        
        XmlSchemaSimpleContentRestriction xsscr 
            = (XmlSchemaSimpleContentRestriction)xssc.getContent();
        assertNotNull(xsscr);
        assertEquals(new QName("http://soapinterop.org/types", "drinksize"),
                     xsscr.getBaseTypeName());
        XmlSchemaObjectCollection xsoc = xsscr.getAttributes();
        assertNotNull(xsoc);
        assertEquals(2, xsoc.getCount());

        Set s = new HashSet();
        s.add("units");
        s.add("id");
        for (int i = 0; i < xsoc.getCount(); i++) {
            XmlSchemaAttribute xsa = (XmlSchemaAttribute)xsoc.getItem(i);
            String name = xsa.getName();
            if (name.equals("units")) {
                assertEquals(new QName("http://soapinterop.org/types", "units"),
                             xsa.getQName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "string"),
                             xsa.getSchemaTypeName());
                assertNull(xsa.getDefaultValue());
                assertEquals("required", xsa.getUse().getValue());
                assertNull(xsa.getFixedValue());
            } else if (name.equals("id")) {
                assertEquals(new QName("http://soapinterop.org/types", "id"),
                             xsa.getQName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "integer"),
                             xsa.getSchemaTypeName());
                assertEquals("001", xsa.getDefaultValue());
                assertEquals("required", xsa.getUse().getValue());
                assertNull(xsa.getFixedValue());
            } else {
                fail("The name \"" + name + "\" was not expected.");
            }
            assertTrue(s.remove(name));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());
        
        XmlSchemaObjectCollection xsoc2 = xsscr.getFacets();
        assertNotNull(xsoc2);
        assertEquals(2, xsoc2.getCount());

        s.clear();
        s.add("small");
        s.add("medium");
        for (int i = 0; i < xsoc2.getCount(); i++) {
            XmlSchemaEnumerationFacet xsef =
                (XmlSchemaEnumerationFacet)xsoc2.getItem(i);
            String value = (String)xsef.getValue();
            if (!(value.equals("small") || value.equals("medium"))) {
                fail("Unexpected enumeration value of \"" + value
                     + "\" found.");
            }
            assertTrue(s.remove(value));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

}