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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EncodingTest extends TestCase {


    public void testExternalAtt() throws Exception{
             //create a DOM document
           DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
           documentBuilderFactory.setNamespaceAware(true);
           DocumentBuilder newDocumentBuilder = documentBuilderFactory.newDocumentBuilder();

           // Skip test in JDK1.4 as it uses crimson parser and an old DOM implementation
           try {
               Document.class.getMethod("getInputEncoding", new Class[]{});
           } catch (NoSuchMethodException e) {
               System.out.println(">>>> Ignoring test as it needs DOM3");
               return;
           }

		   Document doc = newDocumentBuilder.
                   parse(Resources.asURI("other_encoding/japaneseElementForm.xsd"));
           
           XmlSchemaCollection schemaCol = new XmlSchemaCollection();
           XmlSchema s1 = schemaCol.read(doc.getDocumentElement());
           assertNotNull(s1);
           assertEquals("EUC-JP", s1.getInputEncoding().toUpperCase());
           
          //write it back to a stream - re read it and check the encoding
           //we need to explicitly say to have the xml header
           Map options = new HashMap();
           options.put(OutputKeys.OMIT_XML_DECLARATION, "no");
           
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           s1.write(baos,options);
           
           schemaCol = new XmlSchemaCollection();
           Document doc2 = newDocumentBuilder.parse(new ByteArrayInputStream(baos.toByteArray()));
           XmlSchema s2 = schemaCol.read(doc2.getDocumentElement());
           assertNotNull(s2);
           assertEquals("EUC-JP", s2.getInputEncoding().toUpperCase());
            
           
           

       }

}
