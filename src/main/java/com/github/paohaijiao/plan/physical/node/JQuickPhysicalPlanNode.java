package com.github.paohaijiao.plan.physical.node;

import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.statement.JQuickDataSet;

public interface JQuickPhysicalPlanNode {

    JQuickDataSet execute(JQuickExecutionContext context);

    String getNodeType();

    long getEstimatedCost();
}
