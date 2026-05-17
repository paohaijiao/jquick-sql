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
package com.github.paohaijiao.physical.node;

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.List;

/**
 * 提前终止过滤 - 当找到足够数据时提前停止扫描
 */
public class JQuickEarlyTerminationFilterPhysicalNode implements JQuickPhysicalPlanNode {

    private final JQuickExpression predicate;

    private final JQuickPhysicalPlanNode child;

    private static final int BATCH_SIZE = 10000;

    public JQuickEarlyTerminationFilterPhysicalNode(JQuickExpression predicate, JQuickPhysicalPlanNode child) {
        this.predicate = predicate;
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);
        List<JQuickRow> filtered = new ArrayList<>();

        // 分批处理，每批检查是否需要提前终止
        for (int i = 0; i < data.getRows().size(); i++) {
            JQuickRow row = data.get(i);
            Object result = predicate.evaluate(row);
            if (result instanceof Boolean && (Boolean) result) {
                filtered.add(row);
            }

            // 每BATCH_SIZE行检查一次，如果需要提前终止则停止
            if (filtered.size() >= BATCH_SIZE && shouldTerminateEarly(filtered)) {
                break;
            }
        }

        return new JQuickDataSet(data.getColumns(), filtered);
    }

    private boolean shouldTerminateEarly(List<JQuickRow> filtered) {
        // 根据业务逻辑判断是否需要提前终止
        return filtered.size() >= 1000; // 示例：找到1000条就停止
    }

    @Override
    public String getNodeType() {
        return "EarlyTerminationFilter";
    }

    @Override
    public long getEstimatedCost() {
        return child.getEstimatedCost() / 2;
    }
}
