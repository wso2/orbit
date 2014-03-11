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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.InputStream;

public class AnnotationDeepTest extends TestCase {
	
    /**
     * The appinfo element has no source attribute
     * but it has content.
     *
     * @throws Exception Any exception encountered
     */
    public void testAppInfoNoSource() throws Exception {
    	
        /*
		<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
		        targetNamespace="http://www.abc.com/validation"
		        xmlns="http://www.abc.com/validation"
		        xmlns:xsns="http://www.abc.com/validation"
		        xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
		        elementFormDefault="qualified">
		    <xs:annotation>
		        <xs:appinfo>
		            <jaxb:schemaBindings>
		                <jaxb:package  name="com.abc.validation"/>
		            </jaxb:schemaBindings>
		        </xs:appinfo>
		    </xs:annotation>
		
		    <simpleType name="emptyAppinfo">
		        <restriction base="string">
		            <length value="1"/>
		        </restriction>
		    </simpleType>
		
		</xs:schema>
        */

        InputStream is = new FileInputStream(Resources.asURI("annotation-appinfo-no-source.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);
        
        XmlSchemaAnnotation annotation = schema.getAnnotation();
        assertTrue("annotation is retrieved ok", null != annotation);
        XmlSchemaObjectCollection items = annotation.getItems();
        assertEquals("Annotation contains an appinfo and yet this fails", 1, items.getCount());

    }

    /**
     * The appinfo element has a source attribute
     * but the content is deep markup.
     *
     * @throws Exception Any exception encountered
     */
    public void testAppInfoDeep() throws Exception {
    	
        /*
		<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
		        targetNamespace="http://www.abc.com/validation"
		        xmlns="http://www.abc.com/validation"
		        xmlns:xsns="http://www.abc.com/validation"
		        xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
		        elementFormDefault="qualified">
		    <xs:annotation>
		        <xs:appinfo source="anything">
		            <jaxb:schemaBindings>
		                <jaxb:package  name="com.abc.validation"/>
		            </jaxb:schemaBindings>
		        </xs:appinfo>
		    </xs:annotation>
		
		    <simpleType name="emptyAppinfo">
		        <restriction base="string">
		            <length value="1"/>
		        </restriction>
		    </simpleType>
		
		</xs:schema>
        */

        InputStream is = new FileInputStream(Resources.asURI("annotation-appinfo-deep.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);
        
        XmlSchemaAnnotation annotation = schema.getAnnotation();
        assertTrue("annotation is retrieved ok", null != annotation);
        XmlSchemaObjectCollection items = annotation.getItems();
        assertTrue(items.getItem(0) instanceof XmlSchemaAppInfo);
        XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) items.getItem(0);
        NodeList markup = appInfo.getMarkup();
        assertTrue("The markup exists", null != markup);
        Node node = markup.item(1);
        assertTrue(node instanceof Element);
        Element el = (Element) node;
        assertEquals("First level child is retrieved ok",
        		"http://java.sun.com/xml/ns/jaxb", node.getNamespaceURI());
        assertEquals("First level child is retrieved ok",
        		"schemaBindings", node.getLocalName());
        assertTrue("schemaBindings should have a child", el.getChildNodes().getLength() > 0);
        NodeList l = el.getElementsByTagNameNS("http://java.sun.com/xml/ns/jaxb", "package");
        assertTrue("ok this is actually working",l.getLength() > 0);

    }
}
