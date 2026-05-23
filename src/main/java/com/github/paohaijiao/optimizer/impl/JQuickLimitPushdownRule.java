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
package com.github.paohaijiao.optimizer.impl;

import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickLimitNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSortNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

/**
 * packageName com.github.paohaijiao.optimizer.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public class JQuickLimitPushdownRule implements JQuickOptimizerRule {

    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickLimitNode) {
            JQuickLimitNode limit = (JQuickLimitNode) node;
            JQuickLogicalPlanNode child = limit.getChild();
            if (child instanceof JQuickSortNode) {
                // LIMIT + ORDER BY 可以优化为 Top-N
                JQuickSortNode sort = (JQuickSortNode) child;
                return new JQuickLimitNode(limit.getLimit(), limit.getOffset(), sort);
            } else if (child instanceof JQuickProjectNode) {//安全：投影不改变行数
                // 交换Limit和Project
                JQuickProjectNode project = (JQuickProjectNode) child;
                return new JQuickProjectNode(project.getSelectItems(), new JQuickLimitNode(limit.getLimit(), limit.getOffset(), project.getChild()), project.isDistinct());
            } else if (child instanceof JQuickFilterNode) {//过滤不改变行数关系
                // 保持顺序
                JQuickFilterNode filter = (JQuickFilterNode) child;
                return new JQuickFilterNode(filter.getPredicate(), new JQuickLimitNode(limit.getLimit(), limit.getOffset(), filter.getChild()));
            }
        }
        return node;
    }
}
