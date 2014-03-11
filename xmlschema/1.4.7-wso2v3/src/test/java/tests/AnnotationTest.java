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
import java.util.Set;


public class AnnotationTest extends TestCase {

    /**
     * This method will test for when the appinfo
     * element of an annotation doesn't include a
     * source attribute and doesn't contain any 
     * content.
     *
     * @throws Exception Any exception encountered
     */
    public void testEmptyAppInfo() throws Exception {

        /*
        <simpleType name="emptyAppinfo">
          <annotation>
            <documentation source="http://test/source/doc" xml:lang="en">testing987</documentation>
            <appinfo/>
          </annotation>
          <restriction base="string">
            <length value="1"/>
          </restriction>
        </simpleType>
        */

        QName TYPE_QNAME = new QName("http://soapinterop.org/types",
                                     "emptyAppinfo");
        InputStream is = new FileInputStream(Resources.asURI("annotation.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaSimpleType simpleType =
            (XmlSchemaSimpleType)schemaCol.getTypeByQName(TYPE_QNAME);
        assertNotNull(simpleType);

        XmlSchemaAnnotation xsa = simpleType.getAnnotation();
        assertNotNull(xsa);

        XmlSchemaObjectCollection col = xsa.getItems();
        assertEquals(1, col.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaDocumentation.class.getName());
        for (int i = 0; i < col.getCount(); i++) {
            XmlSchemaObject o = col.getItem(i);
            if (o instanceof XmlSchemaAppInfo) {
                fail("The appinfo element did not contain a source"
                     + " attribute or any content, so this element"
                     + " was not exptected to be found.");
            } else if (o instanceof XmlSchemaDocumentation) {
                assertEquals("en",
                             ((XmlSchemaDocumentation)o).getLanguage());
                assertEquals("http://test/source/doc",
                             ((XmlSchemaDocumentation)o).getSource());
                NodeList nl = ((XmlSchemaDocumentation)o).getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("testing987", n.getNodeValue());
                    }
                }
            }
            assertTrue(s.remove(o.getClass().getName()));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());
    }

    /**
     * This method will test for when the documentation
     * element of an annotation doesn't include a
     * source attribute or xml:lang attribute and doesn't
     * contain any content.
     *
     * @throws Exception Any exception encountered
     */
    public void testEmptyDocumentation() throws Exception {

        /*
        <simpleType name="emptyDocumentation">
          <annotation>
            <documentation/>
            <appinfo source="http://test/source/appinfo">testing123</appinfo>
          </annotation>
          <restriction base="string">
            <length value="2"/>
          </restriction>
        </simpleType>
        */

        QName TYPE_QNAME = new QName("http://soapinterop.org/types",
                                     "emptyDocumentation");
        InputStream is = new FileInputStream(Resources.asURI("annotation.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaSimpleType simpleType =
            (XmlSchemaSimpleType)schemaCol.getTypeByQName(TYPE_QNAME);
        assertNotNull(simpleType);

        XmlSchemaAnnotation xsa = simpleType.getAnnotation();
        assertNotNull(xsa);

        XmlSchemaObjectCollection col = xsa.getItems();
        assertEquals(1, col.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaAppInfo.class.getName());
        for (int i = 0; i < col.getCount(); i++) {
            XmlSchemaObject o = col.getItem(i);
            if (o instanceof XmlSchemaAppInfo) {
                assertEquals("http://test/source/appinfo",
                             ((XmlSchemaAppInfo)o).getSource());
                NodeList nl = ((XmlSchemaAppInfo)o).getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("testing123", n.getNodeValue());
                    }
                }
            } else if (o instanceof XmlSchemaDocumentation) {
                fail("The documentation element did not contain a source"
                     + " attribute or any content, so this element"
                     + " was not exptected to be found.");
            }
            assertTrue(s.remove(o.getClass().getName()));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());
    }


    /**
     * This method will test for when the documentation
     * and appinfo elements of an annotation don't include
     * anything.
     *
     * @throws Exception Any exception encountered
     */
    public void testEmptyAppinfoDocumentation() throws Exception {

        /*
        <simpleType name="emptyAppinfoDocumentation">
          <annotation>
            <documentation/>
            <appinfo/>
          </annotation>
          <restriction base="string">
            <length value="1"/>
          </restriction>
        </simpleType>
        */

        QName TYPE_QNAME = new QName("http://soapinterop.org/types",
                                     "emptyAppinfoDocumentation");
        InputStream is = new FileInputStream(Resources.asURI("annotation.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaSimpleType simpleType =
            (XmlSchemaSimpleType)schemaCol.getTypeByQName(TYPE_QNAME);
        assertNotNull(simpleType);

        XmlSchemaAnnotation xsa = simpleType.getAnnotation();
        assertNotNull(xsa);

        XmlSchemaObjectCollection col = xsa.getItems();
        assertEquals(0, col.getCount());

    }

    /**
     * This method will test for when the documentation
     * and appinfo elements contain all the information.
     *
     * @throws Exception Any exception encountered
     */
    public void testFullDocumentationAppinfo() throws Exception {

        /*
        <simpleType name="annotationTest">
          <annotation>
            <documentation source="http://test/source/doc" xml:lang="en">testing987</documentation>
            <appinfo source="http://test/source/appinfo">testing123</appinfo>
          </annotation>
          <restriction base="string">
            <length value="1"/>
          </restriction>
        </simpleType>
        */

        QName TYPE_QNAME = new QName("http://soapinterop.org/types",
                                     "annotationTest");
        InputStream is = new FileInputStream(Resources.asURI("annotation.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        XmlSchemaSimpleType simpleType =
            (XmlSchemaSimpleType)schemaCol.getTypeByQName(TYPE_QNAME);
        assertNotNull(simpleType);

        XmlSchemaAnnotation xsa = simpleType.getAnnotation();
        assertNotNull(xsa);

        XmlSchemaObjectCollection col = xsa.getItems();
        assertEquals(2, col.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaAppInfo.class.getName());
        s.add(XmlSchemaDocumentation.class.getName());
        for (int i = 0; i < col.getCount(); i++) {
            XmlSchemaObject o = col.getItem(i);
            if (o instanceof XmlSchemaAppInfo) {
                assertEquals("http://test/source/appinfo",
                             ((XmlSchemaAppInfo)o).getSource());
                NodeList nl = ((XmlSchemaAppInfo)o).getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("testing123", n.getNodeValue());
                    }
                }
            } else if (o instanceof XmlSchemaDocumentation) {
                assertEquals("en",
                             ((XmlSchemaDocumentation)o).getLanguage());
                assertEquals("http://test/source/doc",
                             ((XmlSchemaDocumentation)o).getSource());
                NodeList nl = ((XmlSchemaDocumentation)o).getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("testing987", n.getNodeValue());
                    }
                }
            }
            assertTrue(s.remove(o.getClass().getName()));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());
    }

    /**
     * This method will test for when an annotation is added
     * to the Xml Schema Element.
     *
     * @throws Exception Any exception encountered
     */
    public void testXmlSchemaElementAnnotation() throws Exception {

        /*
        <annotation id="schemaAnnotation">
          <documentation source="http://test101/source/doc" xml:lang="en">testing101</documentation>
          <appinfo source="http://test101/source/appinfo">testing101</appinfo>
        </annotation>
        */

        InputStream is = new FileInputStream(Resources.asURI("annotation.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);
        
        XmlSchemaAnnotation xsa = schema.getAnnotation();
        XmlSchemaObjectCollection col = xsa.getItems();
        assertEquals(2, col.getCount());

        Set s = new HashSet();
        s.add(XmlSchemaAppInfo.class.getName());
        s.add(XmlSchemaDocumentation.class.getName());
        for (int i = 0; i < col.getCount(); i++) {
            XmlSchemaObject o = col.getItem(i);
            if (o instanceof XmlSchemaAppInfo) {
                assertEquals("http://test101/source/appinfo",
                             ((XmlSchemaAppInfo)o).getSource());
                NodeList nl = ((XmlSchemaAppInfo)o).getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("testing101", n.getNodeValue());
                    }
                }
            } else if (o instanceof XmlSchemaDocumentation) {
                assertEquals("en",
                             ((XmlSchemaDocumentation)o).getLanguage());
                assertEquals("http://test101/source/doc",
                             ((XmlSchemaDocumentation)o).getSource());
                NodeList nl = ((XmlSchemaDocumentation)o).getMarkup();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node n = nl.item(j);
                    if (n.getNodeType() == Node.TEXT_NODE) {
                        assertEquals("testing101", n.getNodeValue());
                    }
                }
            }
            assertTrue(s.remove(o.getClass().getName()));
        }
        assertTrue("The set should have been empty, but instead contained: "
                   + s + ".",
                   s.isEmpty());

    }

}
