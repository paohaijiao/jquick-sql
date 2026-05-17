package com.github.paohaijiao.physical.node;

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.statement.JQuickDataSet;

public interface JQuickPhysicalPlanNode {

    JQuickDataSet execute(JQuickExecutionContext context);

    String getNodeType();

    long getEstimatedCost();
}
