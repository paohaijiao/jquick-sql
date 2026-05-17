package com.github.paohaijiao.logic;

import com.github.paohaijiao.logic.domain.*;

public interface JQuickLogicalPlanVisitor {

    void visit(JQuickTableScanNode node);
    void visit(JQuickProjectNode node);
    void visit(JQuickFilterNode node);
    void visit(JQuickJoinNode node);
    void visit(JQuickGroupByNode node);
    void visit(JQuickSortNode node);
    void visit(JQuickLimitNode node);
    void visit(JQuickWindowNode node);
    void visit(JQuickWithNode node);
    void visit(JQuickSetOperationNode node);
    void visit(JQuickAggregateNode node);
    void visit(JQuickValuesNode node);
    void visit(JQuickEmptyNode node);
}
