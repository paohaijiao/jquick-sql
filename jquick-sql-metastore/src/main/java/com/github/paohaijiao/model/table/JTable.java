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
package com.github.paohaijiao.model.table;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.model.table
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
@Data
public class JTable {

    private String dbName;

    private String tableName;

    private String owner;

    private String tableType;

    private List<JColumn> columns;

    private List<JColumn> partitionKeys;

    private JStorageDescriptor sd;

    private Map<String,String> parameters;

    private long createTime;

    private long lastAccessTime;
}
