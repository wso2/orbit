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
package tests.w3c;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.custommonkey.xmlunit.*;
import org.w3c.dom.Element;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ListIterator;

/**
 * Class to test a single schema by roundtripping it using XMLUnit 
 * cmd line parms: arg0 - valid|invalid arg1 - path to xsd file
 *
 */
public class TestRoundTripXSD extends XMLTestCase {

    private static boolean debug;
    
    static {
        String debugString = System.getProperty("debug");
        debug = (debugString == null) ? false : debugString.equals("true");
    }
    
    private File fileToTest = null;

    private boolean valid = false;

    public final static void main(String[] args) {
        junit.textui.TestRunner.run(new TestRoundTripXSD(new File(args[1]),
                args[0].equals("valid")));
    }

    
    public TestRoundTripXSD() {
        this(new File(System.getProperty("W3CTestLocation")),
             System.getProperty("W3CTestValidity").equals("valid"));
        
    }
    
    public TestRoundTripXSD(File f, boolean valid) {
        super(basename(f));

        this.fileToTest = f;
        this.valid = valid;
    }

    private static String basename(File f) {
        String path = f.getPath();
        int i = path.lastIndexOf(System.getProperty("file.separator"));
        String retval = path.substring(i+1);
        return retval;
    }
    
    protected void runTest() throws Throwable {
        try {
            testRoundTrip();
        }
        catch (InvocationTargetException e) {
            e.fillInStackTrace();
            throw e.getTargetException();
        }
        catch (IllegalAccessException e) {
            e.fillInStackTrace();
            throw e;
        }
    }

    
    public void testRoundTrip() throws Exception {
        
        XmlSchema schema = null;
        DetailedDiff detaileddiffs = null;
        
        try {
            if (debug) {
                System.out.println("fileToTest=" + this.fileToTest);
                System.out.println("valid=" + this.valid);
            }
            schema = loadSchema(fileToTest);

            // TODO: if we get here and the input was meant to be invalid perhaps
            // should fail. Depends on whether XmlSchema is doing validation. For
            // now we're ignoring invalid tests.

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            schema.write(baos);
            Diff diff = new Diff(new FileReader(fileToTest),
                    new InputStreamReader(new ByteArrayInputStream(baos
                            .toByteArray())));

            detaileddiffs = new DetailedDiff(diff);
            detaileddiffs.overrideDifferenceListener(new SchemaAttrDiff());
            boolean result = detaileddiffs.similar();
            if (!result && debug) printFailureDetail(schema, detaileddiffs); 
            assertTrue("Serialized out schema not similar to original", result);
        } catch (Exception e) {
            if (this.valid) {
                if (debug) printFailureDetail(schema, detaileddiffs);
                throw new Exception(this.fileToTest.getPath(), e);
            }
        }
        

    }

    public XmlSchema loadSchema(File f) throws Exception {
        XmlSchemaCollection col = new XmlSchemaCollection();
        col.setBaseUri(f.getPath());
        XmlSchema xmlSchema = col.read(new FileReader(f), null);
        return xmlSchema;
    }

    static class SchemaAttrDiff extends
            IgnoreTextAndAttributeValuesDifferenceListener {

        public int differenceFound(Difference difference) {

            if (difference.getId() == DifferenceConstants.ELEMENT_NUM_ATTRIBUTES
                    .getId()) {
                // control and test have to be elements
                // check if they are schema elements .. they only
                // seem to have the added attributeFormDefault and
                // elementFormDefault attributes
                // so shldnt have more than 2 attributes difference
                Element actualEl = (Element) difference.getControlNodeDetail()
                        .getNode();

                if (actualEl.getLocalName().equals("schema")) {

                    int expectedAttrs = Integer.parseInt(difference
                            .getControlNodeDetail().getValue());
                    int actualAttrs = Integer.parseInt(difference
                            .getTestNodeDetail().getValue());
                    if (Math.abs(actualAttrs - expectedAttrs) <= 2) {
                        return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
                    }
                }
            } else if (difference.getId() == DifferenceConstants.ATTR_NAME_NOT_FOUND_ID) {
                // sometimes the serializer throws in a few extra attributes...
                Element actualEl = (Element) difference.getControlNodeDetail()
                        .getNode();

                if (actualEl.getLocalName().equals("schema")) {
                    return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
                }
            }

            return super.differenceFound(difference);
        }
    }

    private void printFailureDetail(XmlSchema schema, DetailedDiff detaileddiffs) {
        System.err.println(super.getName() + " failure detail");
        System.err.println("-----");
        schema.write(System.err);
        if (detaileddiffs != null) {
            ListIterator li = detaileddiffs.getAllDifferences().listIterator();

            while (li.hasNext()) {
                System.err.println(li.next());
            }
        }
    }

    
}
