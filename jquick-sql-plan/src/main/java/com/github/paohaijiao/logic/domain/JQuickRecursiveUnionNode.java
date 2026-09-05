package com.github.paohaijiao.logic.domain;

import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 递归CTE的Union节点
 * 用于表示 WITH RECURSIVE 定义的递归公共表表达式
 */
public class JQuickRecursiveUnionNode implements JQuickLogicalPlanNode {

    private final String cteName;

    private final List<String> columnNames;

    private final JQuickLogicalPlanNode initialPlan;

    private final JQuickLogicalPlanNode recursivePlan;

    private final boolean unionAll;

    public JQuickRecursiveUnionNode(String cteName, List<String> columnNames, JQuickLogicalPlanNode initialPlan, JQuickLogicalPlanNode recursivePlan, boolean unionAll) {
        this.cteName = cteName;
        this.columnNames = columnNames != null ? new ArrayList<>(columnNames) : null;
        this.initialPlan = initialPlan;
        this.recursivePlan = recursivePlan;
        this.unionAll = unionAll;
    }

    public String getCteName() {
        return cteName;
    }

    public List<String> getColumnNames() {
        return columnNames != null ? Collections.unmodifiableList(columnNames) : null;
    }

    public JQuickLogicalPlanNode getInitialPlan() {
        return initialPlan;
    }

    public JQuickLogicalPlanNode getRecursivePlan() {
        return recursivePlan;
    }

    public boolean isUnionAll() {
        return unionAll;
    }

    @Override
    public String getNodeType() {
        return "RecursiveUnion";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        List<JQuickLogicalPlanNode> children = new ArrayList<>();
        if (initialPlan != null) {
            children.add(initialPlan);
        }
        if (recursivePlan != null) {
            children.add(recursivePlan);
        }
        return Collections.unmodifiableList(children);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        if (visitor == null) {
            return;
        }
        // 先访问当前节点
        visitor.visit(this);
        // 然后访问子节点
        if (initialPlan != null) {
            initialPlan.accept(visitor);
        }
        if (recursivePlan != null) {
            recursivePlan.accept(visitor);
        }
    }

    @Override
    public List<String> getOutputColumns() {
        if (columnNames != null && !columnNames.isEmpty()) {
            return Collections.unmodifiableList(columnNames);
        }
        if (initialPlan != null) {
            return initialPlan.getOutputColumns();
        }
        return Collections.emptyList();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        JQuickLogicalPlanNode clonedInitial = initialPlan != null ? initialPlan.clone() : null;
        JQuickLogicalPlanNode clonedRecursive = recursivePlan != null ? recursivePlan.clone() : null;
        List<String> clonedColumnNames = null;
        if (columnNames != null) {
            clonedColumnNames = new ArrayList<>(columnNames);
        }

        return new JQuickRecursiveUnionNode(cteName, clonedColumnNames, clonedInitial, clonedRecursive, unionAll);
    }

    @Override
    public String toString() {
        String sb = "RecursiveUnionNode{" +
                "cteName='" + cteName + '\'' +
                ", columnNames=" + columnNames +
                ", unionAll=" + unionAll +
                '}';
        return sb;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        JQuickRecursiveUnionNode that = (JQuickRecursiveUnionNode) obj;
        if (unionAll != that.unionAll) return false;
        if (!Objects.equals(cteName, that.cteName)) return false;
        if (!Objects.equals(columnNames, that.columnNames)) return false;
        if (!Objects.equals(initialPlan, that.initialPlan)) return false;
        return Objects.equals(recursivePlan, that.recursivePlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cteName, columnNames, initialPlan, recursivePlan, unionAll);
    }
}