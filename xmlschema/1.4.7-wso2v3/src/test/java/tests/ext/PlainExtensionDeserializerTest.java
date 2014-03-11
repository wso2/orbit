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
package tests.ext;

import junit.framework.TestCase;

import java.util.Map;
import java.util.Iterator;

import tests.Resources;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Test the custom extension dserialization without any specialized
 * hooks
 */
public class PlainExtensionDeserializerTest extends TestCase {

     public void testDeserialization() throws Exception {

           //create a DOM document
           DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
           documentBuilderFactory.setNamespaceAware(true);
           Document doc = documentBuilderFactory.newDocumentBuilder().
                   parse(Resources.asURI("/external/externalAnnotations.xsd"));

           XmlSchemaCollection schemaCol = new XmlSchemaCollection();
           XmlSchema schema = schemaCol.read(doc,null);
           assertNotNull(schema);

          // get the elements and check whether their annotations are properly
          // populated
           Iterator values = schema.getElements().getValues();
           while (values.hasNext()) {
               XmlSchemaElement elt =  (XmlSchemaElement) values.next();
               assertNotNull(elt);
               Map metaInfoMap = elt.getMetaInfoMap();
               assertNotNull(metaInfoMap);

           }
     }


    public void testDeserialization1() throws Exception {

           //create a DOM document
           DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
           documentBuilderFactory.setNamespaceAware(true);
           Document doc = documentBuilderFactory.newDocumentBuilder().
                   parse(Resources.asURI("/external/externalElementAnnotations.xsd"));

           XmlSchemaCollection schemaCol = new XmlSchemaCollection();
           XmlSchema schema = schemaCol.read(doc,null);
           assertNotNull(schema);

          // get the elements and check whether their annotations are properly
          // populated
           Iterator values = schema.getElements().getValues();
           while (values.hasNext()) {
               XmlSchemaElement elt =  (XmlSchemaElement) values.next();
               assertNotNull(elt);
               Map metaInfoMap = elt.getMetaInfoMap();
               assertNotNull(metaInfoMap);

           }
     }
}
