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
package com.github.paohaijiao.service;

import com.github.paohaijiao.core.JMetastore;
import com.github.paohaijiao.model.table.JTable;

/**
 * packageName com.github.paohaijiao.service
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class TableService {
    private  JMetastore metastore;
    //private final JStatsCollector statsCollector;

    public JTable createTable(JTable table) {
        validateTable(table);

        metastore.createTable(table);

        //EventPublisher.publish(new TableCreatedEvent(table));

        return table;
    }

    private void validateTable(JTable table) {
    }
}
