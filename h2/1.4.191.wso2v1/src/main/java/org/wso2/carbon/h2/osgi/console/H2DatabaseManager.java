/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.h2.osgi.console;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.Driver;
import org.h2.engine.Database;
import org.h2.engine.Engine;
import org.wso2.carbon.h2.osgi.utils.CarbonUtils;
import org.wso2.carbon.h2.osgi.utils.H2Constants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class H2DatabaseManager {
    private static H2DatabaseManager instance;
    private static final Log log = LogFactory.getLog(H2DatabaseManager.class);

    private ConsoleService console;

    private H2DatabaseManager() {
    }

    public synchronized static H2DatabaseManager getInstance() {
        if (instance == null)
            instance = new H2DatabaseManager();
        return instance;
    }

    public void initialize() throws SQLException {
        Driver.load();
        startH2Server();
    }

    public void terminate() {
        stopH2Server();
        closeAllOpenDatabases();
    }

    private void closeAllOpenDatabases() {
        Engine s;
        s = Engine.getInstance();
        Field f[] = s.getClass().getDeclaredFields();

        for (Field var : f) {
            if ((var.getType() == HashMap.class) && (var.getName().equals(H2Constants.ENGINE_VAR_DATABASES))) {
                var.setAccessible(true);
                Object pass = new Object();
                try {
                    Object fieldValue = var.get(pass);
                    if (fieldValue.getClass() == HashMap.class) {
                        HashMap DATABASES = (HashMap) fieldValue;

                        ArrayList<Database> openDatabases = new ArrayList<Database>();
                        for (Iterator iterator = DATABASES.values().iterator(); iterator.hasNext(); ) {
                            Database database = (Database) iterator.next();
                            openDatabases.add(database);
                        }

                        for (Iterator iterator = openDatabases.iterator(); iterator.hasNext(); ) {
                            Database database = (Database) iterator.next();
                            Method declaredMethod = null;
                            try {
                                declaredMethod = database.getClass().getDeclaredMethod(H2Constants.ENGINE_METHOD_CLOSE,
                                                                                       new Class[] { boolean.class });
                                if (declaredMethod != null) {
                                    declaredMethod.setAccessible(true);
                                    declaredMethod.invoke(database, new Object[] { true });
                                } else {
                                    log.error("Database close method not found in class " +
                                              database.getClass().getName());
                                }
                            } catch (SecurityException e) {
                                log.error("H2", e);
                            } catch (NoSuchMethodException e) {
                                log.error("H2", e);
                            } catch (InvocationTargetException e) {
                                log.error("H2", e);
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    log.error("H2", e);
                } catch (IllegalAccessException e) {
                    log.error("H2", e);
                }
            }
        }

    }

    public void startH2Server() throws SQLException {
        if (console == null) {
            console = new ConsoleService();
        } else if (console.isServerRunning())
            stopH2Server();
        console.runTool(getH2Parameters());
    }

    public void stopH2Server() {
        if ((console != null) && (console.isServerRunning()))
            console.shutdown();
    }

    private String[] getH2Parameters() {
        List parameterList = new ArrayList();
        Map parameters = CarbonUtils.getH2Parameters();
        for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext(); ) {
            String paraName = (String) iterator.next();
            parameterList.add("-" + paraName);
            if ((parameters.get(paraName) != null) && (!parameters.get(paraName).equals(""))) {
                String value = parameters.get(paraName).toString();
                parameterList.add(value);
            }
        }
        return (String[]) parameterList.toArray(new String[] {});
    }

}
