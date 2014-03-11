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
public class ComplexContentRestrictionTest extends TestCase {

    /**
     * This method will test complex content restriction.
     *
     * @throws Exception Any exception encountered
     */
    public void testComplexContentRestriction() throws Exception {

        /*
        <schema xmlns="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:tns="http://soapinterop.org/types"
                targetNamespace="http://soapinterop.org/types">


          <complexType name="AssemblyRequiredProduct">
            <sequence>
              <element name="Name" type="string"/>
              <element name="Description" type="string" nillable="true"/>
              <element name="Parts" type="string" maxOccurs="unbounded"/>
            </sequence>
          </complexType>
  
          <complexType name="NoAssemblyRequiredProduct">
            <complexContent>
              <restriction base="tns:AssemblyRequiredProduct">
                <sequence>
                  <element name="Name" type="string"/>
                  <element name="Description" type="string" nillable="true"/>
                  <element name="Parts" type="string"/>
                </sequence>
              </restriction>
            </complexContent>
          </complexType>  

        </schema>
        */

        QName TYPE_QNAME = new QName("http://soapinterop.org/types",
                                     "NoAssemblyRequiredProduct");
        InputStream is = new FileInputStream(Resources.asURI("deriverestriction.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaComplexType cType =
            (XmlSchemaComplexType)schemaCol.getTypeByQName(TYPE_QNAME);
        assertNotNull(cType);
        
        XmlSchemaContentModel xscm = cType.getContentModel();
        assertNotNull(xscm);

        XmlSchemaComplexContentRestriction xsccr =
            (XmlSchemaComplexContentRestriction)xscm.getContent();
        assertEquals(new QName("http://soapinterop.org/types",
                               "AssemblyRequiredProduct"),
                     xsccr.getBaseTypeName());

        XmlSchemaSequence xsp = (XmlSchemaSequence)xsccr.getParticle();
        assertNotNull(xsp);

        XmlSchemaObjectCollection col = xsp.getItems();

        Set s = new HashSet();
        s.add("Name");
        s.add("Description");
        s.add("Parts");
        for (int i = 0; i < col.getCount(); i++) {
            XmlSchemaElement xse = (XmlSchemaElement)col.getItem(i);
            String name = xse.getName();
            if (name.equals("Name")) {
                assertEquals(new QName("", "Name"),
                             xse.getQName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema",
                                       "string"),
                                       xse.getSchemaTypeName());
                assertTrue(!xse.isAbstract());
                assertTrue(!xse.isNillable());
            } else if (name.equals("Description")) {
                assertEquals(new QName("", "Description"),
                             xse.getQName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema",
                                       "string"),
                                       xse.getSchemaTypeName());
                assertTrue(!xse.isAbstract());
                assertTrue(xse.isNillable());
            } else if (name.equals("Parts")) {
                assertEquals(new QName("", "Parts"),
                             xse.getQName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema",
                                       "string"),
                                       xse.getSchemaTypeName());
            } else {
                fail("An invalid name of \"" + name + "\" was found.");
            }
            s.remove(name);
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

}
