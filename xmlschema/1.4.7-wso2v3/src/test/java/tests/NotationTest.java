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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
 * @author Brent Ulbricht 
 */
public class NotationTest extends TestCase {

    /**
     * This method will test the notation.
     *
     * @throws Exception Any exception encountered
     */
    public void testNotation() throws Exception {

        /*
         <schema xmlns="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                 xmlns:tns="http://soapinterop.org/types"
                 targetNamespace="http://soapinterop.org/types">
  
           <notation name="teamLogo"
                     system="com/team/graphics/teamLogo"
                     public="http://www.team.com/graphics/teamLogo"
                     id="notation.teamLogo">
             <annotation>
               <documentation xml:lang="en">Location of the corporate logo.</documentation>
             </annotation>
           </notation>

           <notation name="teamMascot"
                     system="com/team/graphics/teamMascot"
                     public="http://www.team.com/graphics/teamMascot"
                     id="notation.teamMascot">
             <annotation>
               <documentation xml:lang="en">Location of the corporate mascot.</documentation>
             </annotation>
           </notation>

           <element name="demoNotation">
             <simpleType>
               <restriction base="NOTATION">
                 <enumeration value="tns:teamLogo"/>
                 <enumeration value="tns:teamMascot"/>
               </restriction>
             </simpleType>
           </element>

         </schema>
        */

        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                                        "demoNotation");
        QName notationName = new QName("http://soapinterop.org/types",
                                                "teamLogo");



        InputStream is = new FileInputStream(Resources.asURI("notation.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaObjectTable notations = schema.getNotations();
        assertNotNull(notations.getItem(notationName));

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);
        assertEquals("demoNotation", elem.getName());
        assertEquals(new QName("http://soapinterop.org/types", "demoNotation"),
                     elem.getQName());

        XmlSchemaSimpleType type =
            (XmlSchemaSimpleType)elem.getSchemaType();
        assertNotNull(type);

        XmlSchemaSimpleTypeRestriction xsstc =
            (XmlSchemaSimpleTypeRestriction)type.getContent();
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema","NOTATION"),
                     xsstc.getBaseTypeName());

        XmlSchemaObjectCollection xsoc = xsstc.getFacets();
        assertEquals(2, xsoc.getCount());
        Set s = new HashSet();
        s.add("tns:teamLogo");
        s.add("tns:teamMascot");
        for (int i = 0; i < xsoc.getCount(); i++) {
            XmlSchemaEnumerationFacet xsef =
                (XmlSchemaEnumerationFacet)xsoc.getItem(i);
            String value = (String)xsef.getValue();
            if (!(value.equals("tns:teamLogo")
                   || value.equals("tns:teamMascot"))) {
                fail("An unexpected value of \"" + value
                     + "\" was found.");
            }
            assertTrue(s.remove(value));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

        XmlSchemaObjectTable xsot = schema.getNotations();
        assertEquals(2, xsot.getCount());
        
        s.clear();
        s.add("teamMascot");
        s.add("teamLogo");
        for (Iterator i = xsot.getNames(); i.hasNext(); ) {
            String name = ((QName)i.next()).getLocalPart();
            if (!(name.equals("teamLogo")
                   || name.equals("teamMascot"))) {
                fail("An unexpected name of \"" + name
                     + "\" was found.");
            }
            assertTrue(s.remove(name));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

        s.clear();
        s.add("teamMascot");
        s.add("teamLogo");
        for (Iterator i = xsot.getValues(); i.hasNext(); ) {
            XmlSchemaNotation xsn = (XmlSchemaNotation)i.next();
            String name = xsn.getName();
            XmlSchemaAnnotation xsa = xsn.getAnnotation();
            XmlSchemaObjectCollection col = xsa.getItems();
            assertEquals(1, col.getCount());
            XmlSchemaDocumentation xsd = null; 
            for (int k = 0; k < col.getCount(); k++) {
                xsd = (XmlSchemaDocumentation)col.getItem(k);
            }
            if (name.equals("teamMascot")) {
                assertEquals("http://www.team.com/graphics/teamMascot",
                             xsn.getPublic());
                assertEquals("com/team/graphics/teamMascot",
                             xsn.getSystem());
                assertEquals("notation.teamMascot", xsn.getId());
                assertEquals("en", xsd.getLanguage());
                NodeList nl = xsd.getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("Location of the corporate mascot.",
                                     n.getNodeValue());
                    }
                }
            } else if (name.equals("teamLogo")) {
                assertEquals("http://www.team.com/graphics/teamLogo",
                             xsn.getPublic());
                assertEquals("com/team/graphics/teamLogo",
                             xsn.getSystem());
                assertEquals("notation.teamLogo", xsn.getId());
                assertEquals("en", xsd.getLanguage());
                NodeList nl = xsd.getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("Location of the corporate logo.",
                                     n.getNodeValue());
                    }
                }
            } else {
                fail("An unexpected name of \"" + name
                     + "\" was found.");
            }
            assertTrue(s.remove(name));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

}