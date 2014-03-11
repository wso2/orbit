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

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSequence;

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
public class SequenceTest extends TestCase {

    /**
     * This method will test the sequence - the min and max occurences.
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
              <sequence minOccurs="4" maxOccurs="50">
                <element name="desktop" type="string"/>
                <element name="laptop" type="string"/>
              </sequence>
            </complexType>
          </element>

        </schema>
        */

        QName computerElementQname = new QName("http://soapinterop.org/types",
                                        "computer");


        InputStream is = new FileInputStream(Resources.asURI("sequence.xsd"));
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

        XmlSchemaSequence sequence = (XmlSchemaSequence)cType.getParticle();
        assertNotNull(sequence);

        //values from the XML file
        assertEquals(sequence.getMinOccurs(),4);
        assertEquals(sequence.getMaxOccurs(),50);

    }

}
