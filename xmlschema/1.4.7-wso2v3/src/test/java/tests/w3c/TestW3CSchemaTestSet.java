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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Class to represent a set of schema tests as described by a .testSet file
 * When executed each of the schemas described in the .testSet file is round-trip
 * tested.   
 * cmd line parms: arg0 - location of the .testSet file. Defaults to:
 *                        ./target/xmlschema2002-01-16/NISTXMLSchema1-0-20020116.testSet
 *
 */
public class TestW3CSchemaTestSet extends TestSuite {

    private List schemaTests = null;
    
    private File testSetFile = null;
    
    // If junit called from cmd line without any args, use the NIST test bucket
    private static String testSetLocation = "./target/xmlschema2002-01-16/NISTXMLSchema1-0-20020116.testSet";

    private TestW3CSchemaTestSet(String name, File testSetFile) {
        super(name);
        this.testSetFile = testSetFile;
    }
    
    public static void main(String[] args) {
        try {
            if (args[0] != null) {
                testSetLocation = args[0]; 
            }
            junit.textui.TestRunner.run(TestW3CSchemaTestSet.suite(new File(testSetLocation)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Test suite() throws Exception {
        testSetLocation =  System.getProperty("W3CTestLocation", testSetLocation);
        return suite(new File(testSetLocation));
    }
    
    /**
     * Returns a suite of TestRoundTripXSD test case instances. One for each of the tests
     * described in the testSetFile
     * @param testSetFile the File object of the .testSet file 
     * @throws Exception
     */
    public static Test suite(File testSetFile) throws Exception {
        TestW3CSchemaTestSet suite = new TestW3CSchemaTestSet("Test for tests", testSetFile);
        String testSetLocation = suite.testSetFile.getPath();
        suite.schemaTests = getSchemaTests(testSetLocation);
        ListIterator li = suite.schemaTests.listIterator();
        while (li.hasNext()) {
            SchemaTest st = (SchemaTest) li.next();
            File f = new File(testSetFile.getParent(), st.schemaDocumentLink);

            if (st.currentStatus!=null) {
                if (!st.currentStatus.equals("accepted")) {
                    System.out.println("Warning: SchemaTest which isn't accepted: " + st);
                } else if (st.isValid()){
                    // for now only test schemas that are valid
                    suite.addTest(new TestRoundTripXSD(f, true));
                }
            }
        }
        return suite;
    }

    /**
     * Returns a list of SchemaTest objects created from the .testSet xml file passed
     * in to the testSet parameter
     * @param testSet the filename of the .testSet file
     * @return List of SchemaTest objects describing the schema files to test
     * @throws Exception
     */
    private static List getSchemaTests(String testSet) throws Exception {
        List schemaTests = new ArrayList();
        Document doc = getDocument(new InputSource(testSet));
        NodeList testGroups = doc.getElementsByTagName("testGroup");
        for (int i = 0; i < testGroups.getLength(); i++) {
            Node testGroup = testGroups.item(i);
            NodeList testGroupChildren = testGroup.getChildNodes();
            Element schemaTestElem = null;
            for (int j = 0; j < testGroupChildren.getLength(); j++) {
                Node n = testGroupChildren.item(j);
                if (!(n instanceof Element))
                    continue;
                schemaTestElem = (Element) n;
                if (schemaTestElem.getNodeName().equals("schemaTest")) {
                    break;
                }
            }
            if (schemaTestElem != null) {
                try {
                    
                SchemaTest schemaTest = new SchemaTest((Element) schemaTestElem);
                if (schemaTest.schemaDocumentLink != null) schemaTests.add(schemaTest);
                } catch (Exception e) {
                    
                }
            }
        }

        return schemaTests; 
    }

    /**
     * Returns a DOM Document of the file passed in as the inputsource parameter
     * @param inputSource input to read in as DOM Document
     * @return DOM Document of the input source
     * @throws Exception can be IOException or SAXException
     */
    private static Document getDocument(InputSource inputSource)
            throws ParserConfigurationException, SAXException,  IOException  {
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	dbf.setNamespaceAware(true);
    	dbf.setValidating(false);

    	return dbf.newDocumentBuilder().parse(inputSource);
    }
}
