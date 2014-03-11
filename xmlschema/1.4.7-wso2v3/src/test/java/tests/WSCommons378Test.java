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

import org.apache.ws.commons.schema.*;
import org.apache.ws.commons.schema.constants.Constants;
import org.w3c.dom.Attr;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

/**
 * Test case for <a
 * href="https://issues.apache.org/jira/browse/WSCOMMONS-378">WSCOMMONS-378</a>
 * 
 * @author Sergey Vladimirov (vlsergey at gmail dot com)
 */
public class WSCommons378Test extends TestCase {

    /**
     * Tests that {@link SchemaBuilder} correctly reads additional enumeration
     * facet attributes
     * 
     * @throws Exception
     *             Any exception encountered
     */
    public void test() throws Exception {
	InputStream is = new FileInputStream(Resources
		.asURI("wscommons-378.xsd"));
	XmlSchemaCollection schemaCol = new XmlSchemaCollection();
	schemaCol.read(new StreamSource(is), null);

	XmlSchemaSimpleType type = (XmlSchemaSimpleType) schemaCol
		.getTypeByQName(new QName("foo"));
	XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction) type
		.getContent();
	XmlSchemaObjectCollection facets = restriction.getFacets();

	assertEquals(2, facets.getCount());

	XmlSchemaEnumerationFacet facet1 = (XmlSchemaEnumerationFacet) facets
		.getItem(0);
	XmlSchemaEnumerationFacet facet2 = (XmlSchemaEnumerationFacet) facets
		.getItem(1);

	final Map externalAttributes1 = (Map) facet1.getMetaInfoMap().get(
		Constants.MetaDataConstants.EXTERNAL_ATTRIBUTES);
	final Map externalAttributes2 = (Map) facet2.getMetaInfoMap().get(
		Constants.MetaDataConstants.EXTERNAL_ATTRIBUTES);

	assertNotNull(externalAttributes1);
	assertNotNull(externalAttributes2);

	assertEquals(1, externalAttributes1.size());
	assertEquals(1, externalAttributes2.size());

	Attr attr1 = (Attr) externalAttributes1.values().iterator().next();
	Attr attr2 = (Attr) externalAttributes2.values().iterator().next();

	assertNotNull(attr1);
	assertNotNull(attr2);

	assertEquals("http://testuri.org/", attr1.getNamespaceURI());
	assertEquals("http://testuri.org/", attr2.getNamespaceURI());

	assertEquals("test", attr1.getPrefix());
	assertEquals("test", attr2.getPrefix());

	assertEquals("attr1", attr1.getLocalName());
	assertEquals("attr2", attr2.getLocalName());

	assertEquals("attr1-value", attr1.getValue());
	assertEquals("attr2-value", attr2.getValue());
    }
}
