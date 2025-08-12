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
package com.github.paohaijiao.join;

import com.github.paohaijiao.query.JQueryPlan;
import lombok.Getter;

/**
 * packageName com.github.paohaijiao.join
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
@Getter
public class JTableSource {
    private String tableName;
    private String alias;
    private JQueryPlan subQuery;

    private boolean isEntityList = false;

    private Class<?> entityClass;  // 实体类类型

    public JTableSource(String tableName) {
        this.tableName = tableName;
    }

    public JTableSource(String tableName, String alias) {
        this(tableName);
        this.alias = alias;
    }

    public JTableSource(JQueryPlan subQuery, String alias) {
        this.subQuery = subQuery;
        this.alias = alias;
    }

    public JTableSource(Class<?> entityClass, String alias) {
        this.entityClass = entityClass;
        this.alias = alias;
        this.isEntityList = true;
        this.tableName = entityClass.getSimpleName();
    }
}
