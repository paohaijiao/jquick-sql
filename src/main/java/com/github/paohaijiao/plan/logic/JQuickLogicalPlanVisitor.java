package com.github.paohaijiao.plan.logic;

import com.github.paohaijiao.plan.logic.domain.*;
import redis.clients.jedis.search.querybuilder.UnionNode;

public interface JQuickLogicalPlanVisitor {

    void visit(JQuickTableScanNode node);

    void visit(JQuickProjectNode node);

    void visit(JQuickFilterNode node);

    void visit(JQuickJoinNode node);

    void visit(JQuickGroupByNode node);

    void visit(JQuickSortNode node);

    void visit(JQuickLimitNode node);

    void visit(JQuickWithNode node);

    void visit(JQuickSetOperationNode node);

    void visit(JQuickEmptyNode node);

    void visit(JQuickSubqueryNode node);

    void visit(JQuickValuesNode node);

    void visit(JQuickWindowNode node);

    void visit(JQuickUnionNode node);

    void visit(JQuickAggregateNode node);
}
