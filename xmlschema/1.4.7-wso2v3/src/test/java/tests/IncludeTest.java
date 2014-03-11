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
import org.xml.sax.InputSource;

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
public class IncludeTest extends TestCase {

    /**
     * This method will test the include.
     *
     * @throws Exception Any exception encountered
     */
    public void testInclude() throws Exception {

        /*
        <schema xmlns="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:tns="http://soapinterop.org/types"
                targetNamespace="http://soapinterop.org/types">
  
          <include schemaLocation="include2.xsd"/>
          <include schemaLocation="include3.xsd"/>

        </schema>

        
        <schema xmlns="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:tns="http://soapinterop.org/types"
                targetNamespace="http://soapinterop.org/types">
  
          <element name="test1include" type="string"/>

        </schema>


        <schema xmlns="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:tns="http://soapinterop.org/types"
                targetNamespace="http://soapinterop.org/types">
  
          <element name="test2include" type="integer"/>

        </schema>
        */

        InputStream is = new FileInputStream(Resources.asURI("include.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaObjectCollection c = schema.getIncludes();
        assertEquals(2, c.getCount());

        Set set = new HashSet();
        set.add(Resources.asURI("include2.xsd"));
        set.add(Resources.asURI("include3.xsd"));
        for (int i = 0; i < c.getCount(); i++) {
            XmlSchemaInclude include = (XmlSchemaInclude)c.getItem(i);
            assertNotNull(include);
            XmlSchema s = include.getSchema();
            assertNotNull(s);
            String schemaLocation = include.getSchemaLocation();
            if (schemaLocation.equals(Resources.asURI("include2.xsd"))) {
                XmlSchemaElement xse =
                    s.getElementByName(new
                        QName("http://soapinterop.org/types", "test1include"));
                assertEquals("test1include", xse.getName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "string"),
                             xse.getSchemaTypeName());
            } else if (schemaLocation.equals(Resources.asURI("include3.xsd"))) {
                XmlSchemaElement xse =
                    s.getElementByName(new 
                        QName("http://soapinterop.org/types", "test2include"));
                assertEquals("test2include", xse.getName());
                assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "integer"),
                             xse.getSchemaTypeName());
            } else {
                fail("The schemaLocation of \"" + schemaLocation + "\" was"
                     + " not expected.");
            }
            set.remove(schemaLocation);
        }

        assertTrue("The set should have been empty, but instead contained: "
                   + set + ".",
                   set.isEmpty());

    }

	/**
	 * Test importing a schema without namespace into a schema
	 * with namespace.
	 */
	public void testImportSchemaWithoutNamespace() throws Exception {
        InputStream is = new FileInputStream(Resources.asURI("includingWithNamespace.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.read(new StreamSource(is), null);

        assertNotNull(schemaCol.getTypeByQName(new QName("http://tns.demo.org", "XdwsGroupId")));
	}


    /**
     * Test importing a schema without namespace into a schema
     * with namespace.
     */
    public void testIncludeSchemaWithoutNamespace() throws Exception {
        String uri = Resources.asURI("woden.xsd");
        InputSource is = new InputSource(new FileInputStream(uri));
        is.setSystemId(uri);
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(is, null);

        XmlSchemaObjectCollection c = schema.getIncludes();
        assertEquals(1, c.getCount());
        
        XmlSchemaInclude schemaInclude = (XmlSchemaInclude)c.getItem(0);
        assertNotNull(schemaInclude);

        XmlSchema schema2 = schemaInclude.getSchema();
        assertNull(schema2.getTargetNamespace());
    }

    /**
     * Schema included defined xmlns="http://www.w3.org/2001/XMLSchema"
     * @throws Exception
     */
    public void testSchemaInclude() throws Exception{
        String uri = Resources.asURI("WSCOMMONS-87/includeBase.xsd");
        InputSource isource = new InputSource(new FileInputStream(uri));
        isource.setSystemId(uri);
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(isource, null);
        assertNotNull(schema);
    }
    
    /**
     * Schema included does not define xmlns="http://www.w3.org/2001/XMLSchema"
     * @throws Exception
     */
    public void testSchemaIncludeNoDefaultNS() throws Exception{
        String uri = Resources.asURI("WSCOMMONS-87/includeBaseNoDefaultNS.xsd");
        InputSource isource = new InputSource(new FileInputStream(uri));
        isource.setSystemId(uri);
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(isource, null);
        assertNotNull(schema);
    }
}
