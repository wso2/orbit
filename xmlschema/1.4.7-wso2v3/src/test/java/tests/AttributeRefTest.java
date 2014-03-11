package tests;

import junit.framework.TestCase;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.ws.commons.schema.*;

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
public class AttributeRefTest extends TestCase {

    public void testAttRefsWithNS() throws Exception{
        QName TYPE_QNAME = new QName("http://tempuri.org/attribute",
                                        "TestAttributeReferenceType");


        InputStream is = new FileInputStream(Resources.asURI("attributref.xsd"));
        XmlSchemaCollection schema = new XmlSchemaCollection();
        XmlSchema s = schema.read(new StreamSource(is), null);

        XmlSchemaComplexType typeByName = (XmlSchemaComplexType)s.getTypeByName(TYPE_QNAME);
        assertNotNull(typeByName);

        XmlSchemaAttribute item = (XmlSchemaAttribute)typeByName.getAttributes().getItem(0);
        QName qName = item.getRefName();
        assertNotNull(qName);

        String namspace = qName.getNamespaceURI();
        assertEquals("http://tempuri.org/attribute",namspace);

        for (Iterator toplevelAttributes = s.getAttributes().getValues();toplevelAttributes.hasNext();){
            XmlSchemaAttribute attribute = (XmlSchemaAttribute) toplevelAttributes.next();
            assertEquals("http://tempuri.org/attribute",attribute.getQName().getNamespaceURI());
        }


    }


}
