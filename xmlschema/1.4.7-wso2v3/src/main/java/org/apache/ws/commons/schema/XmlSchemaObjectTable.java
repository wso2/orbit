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

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A collection class that provides read-only helpers for XmlSchemaObject
 * objects. This class is used to provide the collections for contained
 * elements that are within the schema as collections that are accessed
 * from the XmlSchema class (for example, Attributes, AttributeGroups,
 * Elements, and so on).
 */

public class XmlSchemaObjectTable {

    HashMap collection;

    /**
     * Creates new XmlSchemaObjectTable
     */
    public XmlSchemaObjectTable() {
        this.collection = new HashMap();
    }

    public int getCount() {
        return this.collection.size();
    }

    public XmlSchemaObject getItem(QName name) {
        return (XmlSchemaObject) collection.get(name);
    }

    public Iterator getNames() {
        return collection.keySet().iterator();
    }

    public Iterator getValues() {
        return collection.values().iterator();
    }

    public boolean contains(QName name) {
        return collection.containsKey(name);
    }

    public void add(QName name, XmlSchemaObject value) {
        collection.put(name, value);
    }
}
