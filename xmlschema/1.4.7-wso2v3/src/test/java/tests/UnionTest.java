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
public class UnionTest extends TestCase {

    /**
     * This method will test the union.
     *
     * @throws Exception Any exception encountered
     */
    public void testUnion() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types">
  
           <element name="unionTest">
             <simpleType>
               <union memberTypes="float decimal"/>
             </simpleType>
           </element>

         </schema>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "unionTest");
        InputStream is = new FileInputStream(Resources.asURI("union.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);


        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("unionTest", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "unionTest"),
                     elem.getQName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        assertNotNull(simpleType);

        XmlSchemaSimpleTypeUnion xsstu =
            (XmlSchemaSimpleTypeUnion)simpleType.getContent();
        assertNotNull(xsstu);

        QName[] qname = xsstu.getMemberTypesQNames();
        Set s = new HashSet();
        s.add(new QName("http://www.w3.org/2001/XMLSchema", "float"));
        s.add(new QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        for (int i = 0; i < qname.length; i++) {
            assertTrue(s.remove(qname[i]));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

        assertEquals("float decimal", xsstu.getMemberTypesSource());

    }

}
