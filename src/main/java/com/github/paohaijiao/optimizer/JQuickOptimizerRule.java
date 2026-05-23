package com.github.paohaijiao.optimizer;

import com.github.paohaijiao.logic.JQuickLogicalPlanNode;

public interface JQuickOptimizerRule {

    JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node);

}
