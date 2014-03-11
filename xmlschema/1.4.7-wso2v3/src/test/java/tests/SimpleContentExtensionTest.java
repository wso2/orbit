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
public class SimpleContentExtensionTest extends TestCase {

    /**
     * This method will test the simple content extension.
     *
     * @throws Exception Any exception encountered
     */
    public void testSimpleContentExtension() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types"
                 attributeFormDefault="qualified">
  
           <element name="height">
             <complexType>
               <simpleContent>
                 <extension base="integer">
                   <attribute name="units" type="string" use="required"/>
                   <attribute name="id" type="integer" use="required" default="001"/>
                   <attribute name="desc" type="decimal" fixed="1.1"/>
                 </extension>
               </simpleContent>
             </complexType>
           </element>                                                           

         </schema>
        */                                                                      

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "height");
        InputStream is = new FileInputStream(Resources.asURI("simplecontentextension.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schema.getElementByName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("height", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "height"),
                     elem.getQName());

        XmlSchemaComplexType xsct = (XmlSchemaComplexType)elem.getSchemaType();
        assertNotNull(xsct);
        XmlSchemaSimpleContent xssc = (XmlSchemaSimpleContent)xsct.getContentModel();
        assertNotNull(xssc);
        
        XmlSchemaSimpleContentExtension xssce 
            = (XmlSchemaSimpleContentExtension)xssc.getContent();
        assertNotNull(xssce);
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "integer"),
                     xssce.getBaseTypeName());

        XmlSchemaObjectCollection xsoc = xssce.getAttributes();
        assertEquals(3, xsoc.getCount());

        Set s = new HashSet();
        s.add("units");
        s.add("id");
        s.add("desc");
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
            } else if (name.equals("desc")) {
                assertEquals(new QName("http://soapinterop.org/types", "desc"),
                             xsa.getQName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "decimal"),
                             xsa.getSchemaTypeName());
                assertEquals("none", xsa.getUse().getValue());
                assertEquals("1.1", xsa.getFixedValue());
            } else {
                fail("The name \"" + name + "\" was not expected.");
            }
            s.remove(name);
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

}