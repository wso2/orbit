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

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

public class ElementRefsTest extends TestCase {
    public void testElementRefs() throws Exception {
        QName ELEMENT_QNAME = new QName("http://soapinterop.org/types",
                "attTests");
        InputStream is = new FileInputStream(Resources.asURI("elementreferences.xsd"));
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(new StreamSource(is), null);

        XmlSchemaElement elem = schemaCol.getElementByQName(ELEMENT_QNAME);

        assertNotNull(elem);

        XmlSchemaComplexType cmplxType = (XmlSchemaComplexType)elem.getSchemaType();
        XmlSchemaObjectCollection items = ((XmlSchemaSequence)cmplxType.getParticle()).getItems();

        Iterator it = items.getIterator();
        while (it.hasNext()) {
            XmlSchemaElement innerElement =  (XmlSchemaElement)it.next();
            assertNotNull(innerElement.getRefName());
        }

        // test writing
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        schema.write(bos);
    }

}
