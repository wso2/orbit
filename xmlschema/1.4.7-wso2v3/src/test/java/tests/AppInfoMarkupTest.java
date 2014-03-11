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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;


public class AppInfoMarkupTest extends XMLTestCase {

    public void testAppInfo() throws Exception{
        DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document pureDOMDocument = b.parse(new FileInputStream(Resources.asURI("appInfo.xsd")));

        InputStream is = new FileInputStream(Resources.asURI("appInfo.xsd"));
        XmlSchemaCollection schema = new XmlSchemaCollection();
        XmlSchema s = schema.read(new StreamSource(is), null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s.write(baos);

        Document serialzedDoument = b.parse(new ByteArrayInputStream(baos.toByteArray()));
        XMLUnit.compareXML(pureDOMDocument,serialzedDoument);

    }

}
