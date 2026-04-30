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
package com.github.paohaijiao.condition;

import com.github.paohaijiao.enums.JQuickSqlConditionType;

/**
 * packageName com.github.paohaijiao.condition
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/24
 */
public class JQuickSqlParenthesesCondition extends JQuickSqlCondition {

    private JQuickSqlCondition innerCondition;


    public JQuickSqlParenthesesCondition() {
        this.type = JQuickSqlConditionType.PARENTHESES;
    }

    public JQuickSqlParenthesesCondition(JQuickSqlCondition innerCondition) {
        this();
        this.innerCondition = innerCondition;
    }

    public JQuickSqlCondition getInnerCondition() {
        return innerCondition;
    }

    public void setInnerCondition(JQuickSqlCondition innerCondition) {
        this.innerCondition = innerCondition;
    }

    public boolean hasInnerCondition() {
        return innerCondition != null;
    }
}
