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
import java.util.Iterator;

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
public class AttributeGroupTest extends TestCase {

    /**
     * This method will test the list.
     *
     * @throws Exception Any exception encountered
     */
    public void testAttributeGroup() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types"
                 attributeFormDefault="qualified" >
  
           <attributeGroup name="department">
             <attribute name="name" type="string"/>
             <attribute name="id" type="integer"/>
           </attributeGroup>
  
           <element name="member">
             <complexType>
               <attributeGroup ref="tns:department"/>
             </complexType>
           </element>

         </schema>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "member");
        InputStream is = new FileInputStream(Resources.asURI("attributegroup.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("member", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "member"),
                     elem.getQName());
        
        XmlSchemaComplexType t = (XmlSchemaComplexType)elem.getSchemaType();
        assertNotNull(t);

        XmlSchemaObjectCollection c = t.getAttributes();
        for (Iterator i = c.getIterator(); i.hasNext(); ) {
            XmlSchemaAttributeGroupRef agrn = (XmlSchemaAttributeGroupRef)i.next();
            assertEquals(new QName("http://soapinterop.org/types",
                                   "department"), agrn.getRefName()); 
        }

        XmlSchemaObjectTable attG = schema.getAttributeGroups();
        assertNotNull(attG);
        assertEquals(1, attG.getCount());
        
        for (Iterator i = attG.getNames(); i.hasNext(); ) {
            assertEquals("department", ((QName)i.next()).getLocalPart());
        }

        for (Iterator i = attG.getValues(); i.hasNext(); ) {
            Object obj1 = i.next();
            if (obj1 instanceof XmlSchemaAttributeGroup) {
                assertEquals("department", ((XmlSchemaAttributeGroup)obj1).getName().getLocalPart());
                XmlSchemaObjectCollection attributes =
                    ((XmlSchemaAttributeGroup)obj1).getAttributes();
                assertNotNull(attributes);
                assertEquals(2, attributes.getCount());
                for (Iterator j = attributes.getIterator(); j.hasNext(); ) {
                    XmlSchemaAttribute obj2 = (XmlSchemaAttribute)j.next();
                    String name = obj2.getName();
                    if (name.equals("id")) {
                        assertEquals(new QName("http://soapinterop.org/types", "id"),
                                     obj2.getQName());
                        assertEquals(new QName("http://www.w3.org/2001/XMLSchema",
                                               "integer"), obj2.getSchemaTypeName());
                    } else if (name.equals("name")) {
                        assertEquals(new QName("http://soapinterop.org/types", "name"),
                                     obj2.getQName());
                        assertEquals(new QName("http://www.w3.org/2001/XMLSchema",
                                               "string"), obj2.getSchemaTypeName());
                    } else {
                        fail("The name \"" + name + "\" should not have been found "
                             + "for an attribute.");

                    }
                }
            } else {
                fail("There should have been one instance of the "
                     + "class " + XmlSchemaAttributeGroup.class.getName()
                     + " , but instead " + obj1.getClass().getName() + " was"
                     + " found.");
            }
        }

    }

}