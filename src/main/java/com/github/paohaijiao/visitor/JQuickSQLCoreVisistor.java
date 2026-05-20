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
package com.github.paohaijiao.visitor;

import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLBaseVisitor;
import com.github.paohaijiao.parser.JQuickSQLParser;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLCoreVisistor extends JQuickSQLBaseVisitor {

    protected JContext context=new JContext();

    protected JQuickSqlConfig config=new JQuickSqlConfig();

    protected JQuickJoinType parseJoinType(JQuickSQLParser.JoinTypeContext ctx) {
        if (ctx.INNER() != null) {
            return JQuickJoinType.INNER;
        } else if (ctx.CROSS() != null) {
            return JQuickJoinType.CROSS;
        } else if (ctx.LEFT() != null) {
            return JQuickJoinType.LEFT;
        } else if (ctx.RIGHT() != null) {
            return JQuickJoinType.RIGHT;
        } else if (ctx.NATURAL() != null) {
            return JQuickJoinType.NATURAL;
        } else if (ctx.FULL() != null) {
            return JQuickJoinType.FULL;
        }
        return JQuickJoinType.INNER;
    }
}
