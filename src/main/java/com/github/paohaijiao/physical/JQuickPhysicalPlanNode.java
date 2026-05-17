package com.github.paohaijiao.physical;

import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.List;

public interface JQuickPhysicalPlanNode {


    String getNodeType();

    List<JQuickPhysicalPlanNode> getChildren();

    List<JQuickPhysicalColumn> getOutputSchema();

    JQuickPhysicalStats getStats();

    public JQuickPhysicalPlanNode clone();

    void accept(JQuickPhysicalPlanVisitor visitor);
}