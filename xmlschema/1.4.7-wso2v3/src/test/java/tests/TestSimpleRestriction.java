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
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaType;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.InputStream;

public class TestSimpleRestriction extends TestCase {
    public void testSimpleRestriction() throws Exception {
        QName TYPE_QNAME = new QName("http://soapinterop.org/types",
                "layoutComponentType");
        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                "foo");

        InputStream is = new FileInputStream(Resources.asURI("SimpleContentRestriction.xsd"));
        XmlSchemaCollection schema = new XmlSchemaCollection();
        schema.read(new StreamSource(is), null);

        XmlSchemaType simpleType = schema.getTypeByQName(TYPE_QNAME);
        assertNotNull(simpleType);

        XmlSchemaElement elem = schema.getElementByQName(ELEMENT_QNAME);
        assertNotNull(elem);

        XmlSchemaType type = elem.getSchemaType();
        assertNotNull(type);
    }

    public void testSimpleTypeRestrictionWithoutNamespace() throws Exception {
    	InputStream is = new FileInputStream(Resources.asURI("includedWithoutNamespace.xsd"));
    	XmlSchemaCollection schema = new XmlSchemaCollection();
    	schema.read(new StreamSource(is), null);
    	XmlSchemaType principalId = schema.getTypeByQName(new QName("", "XdwsPrincipalId"));
    	assertNotNull(principalId);
    	XmlSchemaSimpleType groupId = (XmlSchemaSimpleType)schema.getTypeByQName(new QName("", "XdwsGroupId"));
    	assertNotNull(groupId);
    	QName baseName = ((XmlSchemaSimpleTypeRestriction)groupId.getContent()).getBaseTypeName();

    	assertEquals(principalId.getQName(), baseName);
    }
}
