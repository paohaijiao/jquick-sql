/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.factory;

import com.github.paohaijiao.connector.JDataSetConnector;
import com.github.paohaijiao.dataset.JDataSet;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * packageName com.github.paohaijiao.factory
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/26
 */
public class JDataSetConnectorFactory {

    private static final JDataSetConnectorFactory INSTANCE = new JDataSetConnectorFactory();

    private final Map<String, JDataSetConnector> connectorRegistry = new HashMap<>();

    private JDataSetConnectorFactory() {
        loadConvertersFromSPI();
    }
    public static JDataSetConnectorFactory getInstance() {
        return INSTANCE;
    }
    public void registerConverter(String connectType, JDataSetConnector connector) {
        connectorRegistry.put(connectType, connector);
    }
    public void unregisterConverter(String connectType) {
        connectorRegistry.remove(connectType.toLowerCase());
    }
    public Set<String> getSupportedConnectType() {
        return connectorRegistry.keySet();
    }
    public boolean supports(String connectType) {
        return connectorRegistry.containsKey(connectType);
    }
    public JDataSetConnector connectTypeOf(String connectType) {
        JDataSetConnector connector = connectorRegistry.get(connectType);
        if (connector == null) {
            throw new IllegalArgumentException("unsupported connectType: " + connectType );
        }
        return connector;
    }
    public JDataSet convert(String connectType, Object source) {
        JDataSetConnector connector = connectTypeOf(connectType);
        return connector.load(source);
    }
    private void loadConvertersFromSPI() {
        ServiceLoader<JDataSetConnector> loader = ServiceLoader.load(JDataSetConnector.class);
        for (JDataSetConnector connector : loader) {
            registerConverter(connector.getConnectorType(), connector);
        }
    }
}
