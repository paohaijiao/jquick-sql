package com.github.paohaijiao.physical;

import com.github.paohaijiao.physical.node.*;

public interface JQuickPhysicalPlanVisitor {

    void visit(JQuickTableScanPhysicalNode node);

    void visit(JQuickFilterPhysicalNode node);

    void visit(JQuickProjectPhysicalNode node);

    void visit(JQuickHashJoinPhysicalNode node);

    void visit(JQuickNestedLoopJoinPhysicalNode node);

    void visit(JQuickHashAggregatePhysicalNode node);

    void visit(JQuickSortPhysicalNode node);

    void visit(JQuickLimitPhysicalNode node);

    void visit(JQuickExchangePhysicalNode node);

    void visit(JQuickValuesPhysicalNode node);

    void visit(JQuickEmptyPhysicalNode node);

    void visit(JQuickWindowPhysicalNode node);

    void visit(JQuickSetOperationPhysicalNode node);

    void visit(JQuickTopNPhysicalNode node);

    void visit(JQuickRecursiveUnionPhysicalNode node);
}
