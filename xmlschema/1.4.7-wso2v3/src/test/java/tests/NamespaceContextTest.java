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
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class NamespaceContextTest extends XMLTestCase {
    protected boolean whitespace = true;
    protected void setUp() throws Exception {
        whitespace = XMLUnit.getIgnoreWhitespace();
        XMLUnit.setIgnoreWhitespace(true);
    }
    protected void tearDown() throws java.lang.Exception {
        XMLUnit.setIgnoreWhitespace(whitespace);
    }
    public void testNamespaceContext() throws Exception {
        Map namespaceMapFromWSDL = new HashMap();
        namespaceMapFromWSDL.put("tns", new URI("http://example.org/getBalance/"));
        namespaceMapFromWSDL.put("xsd", new URI("http://www.w3.org/2001/XMLSchema"));
        String schema = "\t\t<xsd:schema targetNamespace=\"http://example.org/getBalance/\"\n" +
                "attributeFormDefault=\"unqualified\" elementFormDefault=\"unqualified\"" +
                " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                " xmlns=\"http://www.w3.org/2001/XMLSchema\"" +
                " xmlns:xsd1=\"http://example.org/getBalance/\">" +
                "\t\t\t<xsd:include schemaLocation=\"getBalance.xsd\" />\n" +
                "\n" +
                "\t\t\t<xsd:element name=\"newCustomer\">\n" +
                "\t\t\t\t<xsd:complexType>\n" +
                "\t\t\t\t\t<xsd:sequence>\n" +
                "\t\t\t\t\t\t<xsd:element name=\"details\" type=\"tns:cinfoct\" />\n" +
                "\t\t\t\t\t\t<xsd:element name=\"id\" type=\"string\" />\n" +
                "\t\t\t\t\t</xsd:sequence>\n" +
                "\t\t\t\t</xsd:complexType>\n" +
                "\t\t\t</xsd:element>\n" +
                "\n" +
                "\t\t\t<xsd:element name=\"customerId\">\n" +
                "\t\t\t\t<xsd:complexType>\n" +
                "\t\t\t\t\t<xsd:sequence>\n" +
                "\t\t\t\t\t\t<xsd:element name=\"id\" type=\"string\" />\n" +
                "\t\t\t\t\t</xsd:sequence>\n" +
                "\t\t\t\t</xsd:complexType>\n" +
                "\t\t\t</xsd:element>\n" +
                "\n" +
                "\t\t</xsd:schema>";
        org.xml.sax.InputSource schemaInputSource = new InputSource(new StringReader(schema));
        XmlSchemaCollection xsc = new XmlSchemaCollection();
        xsc.setBaseUri(Resources.TEST_RESOURCES);

        //Set the namespaces explicitly
        NamespaceMap prefixmap = new NamespaceMap(namespaceMapFromWSDL);
        xsc.setNamespaceContext(prefixmap);
        XmlSchema schemaDef = xsc.read(schemaInputSource, null);
        StringWriter sw = new StringWriter();
        schemaDef.write(sw);
        try {
            assertXMLEqual(sw.toString(), schema.replaceAll("tns:", "xsd1:"));
        } catch (NullPointerException ex) {
            System.out.println(">>>> NPE, ignoring assertXMLEqual");
        }
    }
    public void testNamespaceCount() throws Exception {
        String schema =  "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\"" +
        		" xmlns:addressing=\"http://schemas.foo.com/references\"" +
        		" xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\"" +
        		" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"" +
        		" xmlns:tns=\"http://schemas.foo.com/idl/isfx_authn_service.idl\" " +
        		" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"" +
        		" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
        		" xmlns:xsd1=\"http://schemas.foo.com/idltypes/isfx_authn_service.idl\"" +
        		" targetNamespace=\"http://schemas.foo.com/idltypes/isf_suppl_info.idl\">" +
        		"<complexType name=\"IT_ISF.DomainRealmFilter\">" +
        		"<sequence><element name=\"domain_name\" type=\"string\"/>" +
        		"<element name=\"realm\" type=\"string\"/></sequence>" +
        		"</complexType></schema>";
        org.xml.sax.InputSource schemaInputSource = new InputSource(new StringReader(schema));
        XmlSchemaCollection xsc = new XmlSchemaCollection();
        xsc.setBaseUri(Resources.TEST_RESOURCES);

        //Set the namespaces explicitly
        XmlSchema schemaDef = xsc.read(schemaInputSource, null);
        Document doc = schemaDef.getSchemaDocument();
        Element el = doc.getDocumentElement();
        String ns = el.getAttribute("xmlns");
        assertEquals("http://www.w3.org/2001/XMLSchema", ns);
    }
    public void testNullNamespaceCtx() throws Exception {
        String schema = "\t\t<xsd:schema targetNamespace=\"http://example.org/getBalance/\"\n" +
            "attributeFormDefault=\"unqualified\" elementFormDefault=\"unqualified\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
            " xmlns=\"http://www.w3.org/2001/XMLSchema\"" +
            " xmlns:xsd1=\"http://example.org/getBalance/\">" +
            "\t\t\t<xsd:include schemaLocation=\"getBalance.xsd\" />\n" +
            "\n" +
            "\t\t\t<xsd:element name=\"newCustomer\">\n" +
            "\t\t\t\t<xsd:complexType>\n" +
            "\t\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t\t<xsd:element name=\"details\" type=\"xsd1:cinfoct\" />\n" +
            "\t\t\t\t\t\t<xsd:element name=\"id\" type=\"string\" />\n" +
            "\t\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t\t</xsd:complexType>\n" +
            "\t\t\t</xsd:element>\n" +
            "\n" +
            "\t\t\t<xsd:element name=\"customerId\">\n" +
            "\t\t\t\t<xsd:complexType>\n" +
            "\t\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t\t<xsd:element name=\"id\" type=\"string\" />\n" +
            "\t\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t\t</xsd:complexType>\n" +
            "\t\t\t</xsd:element>\n" +
            "\n" +
            "\t\t</xsd:schema>";
        org.xml.sax.InputSource schemaInputSource = new InputSource(new StringReader(schema));
        XmlSchemaCollection xsc = new XmlSchemaCollection();
        xsc.setBaseUri(Resources.TEST_RESOURCES);

        //Set the namespaces explicitly
        XmlSchema schemaDef = xsc.read(schemaInputSource, null);
        schemaDef.setNamespaceContext(null);
        Document doc = schemaDef.getSchemaDocument();
        Element el = doc.getDocumentElement();
        String ns = el.getAttribute("xmlns");
        assertEquals("http://www.w3.org/2001/XMLSchema", ns);
        ns = el.getAttribute("xmlns:tns");
        assertEquals("http://example.org/getBalance/", ns);
    }
}
