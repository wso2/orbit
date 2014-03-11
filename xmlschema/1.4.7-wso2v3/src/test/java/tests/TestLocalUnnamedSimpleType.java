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

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

/**
 * TestElementForm
 */
public class TestLocalUnnamedSimpleType extends TestCase {
    String schemaXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
             "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\"\n" +
             "targetNamespace=\"http://finance.example.com/CreditCardFaults/xsd\"\n" +
             "xmlns:tns=\"http://finance.example.com/CreditCardFaults/xsd\"\n" +
             "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
             "xsi:schemaLocation=\"http://www.w3.org/2001/XMLSchema\n" +
             "              http://www.w3.org/2001/XMLSchema.xsd\">\n" +
             "\n" +
             "<element name=\"tns:CreditCardNumber\" type=\"string\"></element>\n" +
             "\n" +
             "<element name=\"tns:CreditCardType\">\n" +
             "<simpleType>\n" +
             "<restriction base=\"string\">\n" +
             "<enumeration value=\"AMEX\" />\n" +
             "<enumeration value=\"MASTERCARD\" />\n" +
             "<enumeration value=\"VISA\" />\n" +
             "</restriction>\n" +
             "</simpleType>\n" +
             "</element>\n" +
             "</schema> ";

    public void testLocalUnnamedSimpleType() throws Exception {
        XmlSchemaCollection schema = new XmlSchemaCollection();
        schema.read(new StreamSource(new ByteArrayInputStream(schemaXML.getBytes())), null);
    }
}
