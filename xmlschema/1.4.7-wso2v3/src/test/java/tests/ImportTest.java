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
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.namespace.QName;
import java.io.File;

public class ImportTest extends TestCase {

    public void testSchemaImport() throws Exception{
        //create a DOM document
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document doc = documentBuilderFactory.newDocumentBuilder().
                parse(Resources.asURI("importBase.xsd"));

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.setBaseUri(Resources.TEST_RESOURCES);
        XmlSchema schema = schemaCol.read(doc,null);
        assertNotNull(schema);

        // attempt with slash now
        schemaCol = new XmlSchemaCollection();
        schemaCol.setBaseUri(Resources.TEST_RESOURCES + "/");
        schema = schemaCol.read(doc,null);
        assertNotNull(schema);
    }

    /**
     * variation of above don't set the base uri.
     * @throws Exception
     */
    public void testSchemaImport2() throws Exception{
        File file = new File(Resources.asURI("importBase.xsd"));
        //create a DOM document
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document doc = documentBuilderFactory.newDocumentBuilder().
                parse(file.toURL().toString());

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(doc,file.toURL().toString(),null);
        assertNotNull(schema);

    }

    /**
     * see whether we can reach the types of the imported schemas.
     * @throws Exception
     */
    public void testSchemaImport3() throws Exception{
        File file = new File(Resources.asURI("importBase.xsd"));
        //create a DOM document
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document doc = documentBuilderFactory.newDocumentBuilder().
                parse(file.toURL().toString());

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(doc,file.toURL().toString(),null);
        assertNotNull(schema);

        assertNotNull(schema.getTypeByName(new QName("http://soapinterop.org/xsd2","SOAPStruct")));
        assertNotNull(schema.getElementByName(new QName("http://soapinterop.org/xsd2","SOAPWrapper")));
    }
}
