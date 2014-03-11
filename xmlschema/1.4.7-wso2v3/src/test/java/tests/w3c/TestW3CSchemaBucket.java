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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class to represent a bucket of tests described by a set of .testSet files.
 * All of the tests described in all of the .testSet files present in the top
 * level of the directory supplied will be round-trip tested.
 * Note: Subdirs are not traversed because the .testSet files in the top level
 * of the xmlschema2002-01-16 bucket describe all the tests in the bucket.   
 * cmd line parms: arg0 - location of the directory containing .testSet files
 *                        defaults to ./target/xmlschema2002-01-16
 *
 */
public class TestW3CSchemaBucket extends TestSuite {

    private static List allTestSetFiles;
    
    // If tests run from cmd line without any args, run the full suite
    private static String testSetsLocation = "./target/xmlschema2002-01-16";

    public TestW3CSchemaBucket(String name) {
        super(name);
    }

    public static void main(String[] args) {
        try {
            junit.textui.TestRunner.run(TestW3CSchemaBucket.suite());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Test suite() throws Exception {
        testSetsLocation =  System.getProperty("W3CTestLocation", testSetsLocation);
        TestSuite suite = new TestSuite("Test for tests");
        allTestSetFiles = getTestSetFiles(testSetsLocation);
        ListIterator li = allTestSetFiles.listIterator();
        while (li.hasNext()) {
            Object o = li.next();
            File testSet = null;
            if (o instanceof File) {
                testSet = (File) o;  
            }
            suite.addTest(TestW3CSchemaTestSet.suite(testSet));
        }
        return suite;
    }

    private static List getTestSetFiles(String testSetsLocation) throws Exception {
        File dir = new File(testSetsLocation);
        if (!dir.isDirectory()) {
            throw new Exception ("testSet files location must be a directory");
        }
        ArrayList testSetFiles = new ArrayList();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getAbsolutePath().endsWith("testSet")) {
                testSetFiles.add(files[i]);
            }
        }
        return testSetFiles;
    }
}
