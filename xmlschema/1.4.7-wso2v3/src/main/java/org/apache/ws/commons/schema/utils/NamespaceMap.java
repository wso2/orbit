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
package org.apache.ws.commons.schema.utils;

import java.util.*;

public class NamespaceMap extends HashMap implements NamespacePrefixList {
    
    private static final long serialVersionUID = 1L;

    public NamespaceMap() {
    }
    
    public NamespaceMap(Map map) {
        super(map);
    }

    public void add(String prefix, String namespaceURI) {
        put(prefix, namespaceURI);
    }

    public String[] getDeclaredPrefixes() {
        Set keys = keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    public String getNamespaceURI(String prefix) {
        Object o = get(prefix);
        return o == null ? null : o.toString();
    }

    public String getPrefix(String namespaceURI) {
        Iterator iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String val = entry.getValue() == null ? null : entry.getValue().toString();
            if (namespaceURI.equals(val)) {
                return (String)entry.getKey();
            }
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        ArrayList list = new ArrayList();
        Iterator iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String val = entry.getValue() == null ? null : entry.getValue().toString();
            if (namespaceURI.equals(val)) {
                list.add(entry.getKey());
            }
        }
        return list.iterator();
    }
}
