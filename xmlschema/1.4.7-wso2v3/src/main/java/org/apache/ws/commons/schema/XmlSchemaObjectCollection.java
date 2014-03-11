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

package org.apache.ws.commons.schema;

import java.util.Iterator;
import java.util.Vector;

/**
 * An object collection class to handle XmlSchemaObjects when collections
 * are returned from method calls.
 */
public class XmlSchemaObjectCollection {

    Vector objects;

    /**
     * Creates new XmlSchemaObjectCollection
     */
    public XmlSchemaObjectCollection() {
        objects = new Vector();
    }

    public int getCount() {
        return objects.size();
    }

    public XmlSchemaObject getItem(int i) {
        return (XmlSchemaObject) objects.elementAt(i);
    }

    public void setItem(int i, XmlSchemaObject item) {
        objects.insertElementAt(item, i);
    }

    public void add(XmlSchemaObject item) {
        objects.addElement(item);
    }

    public boolean contains(XmlSchemaObject item) {
        return objects.contains(item);
    }

    public int indexOf(XmlSchemaObject item) {
        return objects.indexOf(item);
    }

    public void remove(XmlSchemaObject item) {
        objects.remove(item);
    }

    public void removeAt(int index) {
        objects.removeElementAt(index);
    }

    public Iterator getIterator() {
        return objects.iterator();
    }

    public String toString(String prefix, int tab) {
        String xml = new String();

        for (int i = 0; i < getCount(); i++) {
            xml += getItem(i).toString(prefix, tab);
        }


        return xml;

    }
}
