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

import com.github.paohaijiao.enums.JoinType;
import com.github.paohaijiao.expression.JExpression;
import lombok.Getter;

/**
 * packageName com.github.paohaijiao.join
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
@Getter
public class JoinPart {

    private JoinType joinType;

    private JTableSource tableSource;

    private JExpression onCondition;

    public JoinPart(JoinType joinType, JTableSource tableSource) {
        this.joinType = joinType;
        this.tableSource = tableSource;
    }

    public JoinPart(JoinType joinType, JTableSource tableSource, JExpression onCondition) {
        this(joinType, tableSource);
        this.onCondition = onCondition;
    }
}
