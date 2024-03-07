/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.h2.osgi.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.Driver;
import org.h2.engine.Database;
import org.h2.engine.Engine;
import org.wso2.carbon.h2.osgi.utils.H2Constants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * H2 Database Manager.
 */
public class H2DatabaseManager {

    private static final H2DatabaseManager instance = new H2DatabaseManager();
    private static final Log log = LogFactory.getLog(H2DatabaseManager.class);

    private H2DatabaseManager() {
    }

    /**
     * Get the instance of the H2DatabaseManager.
     *
     * @return H2DatabaseManager instance.
     */
    public synchronized static H2DatabaseManager getInstance() {

        return instance;
    }

    /**
     * Initialize the H2DatabaseManager.
     */
    public void initialize() {

        Driver.load();
    }

    /**
     * Terminate the H2DatabaseManager.
     */
    public void terminate() {

        closeAllOpenDatabases();
    }

    private void closeAllOpenDatabases() {

        Field fields[] = Engine.class.getDeclaredFields();

        for (Field var : fields) {
            if ((var.getType() == HashMap.class) && (H2Constants.ENGINE_VAR_DATABASES.equals(var.getName()))) {
                var.setAccessible(true);
                Object pass = new Object();
                try {
                    Object fieldValue = var.get(pass);
                    if (fieldValue instanceof HashMap) {
                        Map<String, Database> databases = (HashMap<String, Database>) fieldValue;

                        List<Database> openDatabases = new ArrayList<Database>();
                        openDatabases.addAll(databases.values());

                        for (Iterator iterator = openDatabases.iterator(); iterator.hasNext(); ) {
                            Database database = (Database) iterator.next();
                            Method declaredMethod = null;
                            try {
                                declaredMethod = database.getClass().getDeclaredMethod(H2Constants.ENGINE_METHOD_CLOSE,
                                        new Class[]{boolean.class});
                                if (declaredMethod != null) {
                                    declaredMethod.setAccessible(true);
                                    declaredMethod.invoke(database, new Object[]{true});
                                } else {
                                    log.error("Database close method not found in class " +
                                            database.getClass().getName());
                                }
                            } catch (InvocationTargetException | NoSuchMethodException | SecurityException e) {
                                log.error("Error when closing H2 database", e);
                            }
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    log.error("Error when closing H2 database", e);
                }
            }
        }
    }
}
