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
public class ListTest extends TestCase {

    /**
     * This method will test the list.
     *
     * @throws Exception Any exception encountered
     */
    public void testList() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types">
        
           <element name="workDays">
             <simpleType>
               <restriction base="tns:daysInWeek">
                 <length value="5"/>
               </restriction>
             </simpleType>
           </element>

           <simpleType name="daysInWeek">
             <list itemType="xsd:integer"/>
           </simpleType>
  
         </schema>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                "workDays");
        InputStream is = new FileInputStream(Resources.asURI("list.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("workDays", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "workDays"),
                     elem.getQName());

        XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)elem.getSchemaType();
        assertNotNull(simpleType);

        XmlSchemaSimpleTypeRestriction r =
            (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
        assertNotNull(r);

        QName baseTypeName = r.getBaseTypeName();
        assertEquals(new QName("http://soapinterop.org/types", "daysInWeek"),
                     baseTypeName);
        XmlSchemaType type = schemaCol.getTypeByQName(baseTypeName);

        XmlSchemaSimpleTypeContent content = ((XmlSchemaSimpleType)type).getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "integer"),
                   ((XmlSchemaSimpleTypeList)content).getItemTypeName());

    }

}
