package com.github.paohaijiao.visitor;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.parser.JQuickSQLParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class JQuickSQLSelectStatement extends JQuickSQLSelectSpecVisitor{


    @Override
    public JQuickHavingClauseNode visitHavingClause(JQuickSQLParser.HavingClauseContext ctx) {
        JQuickFilterConditionNode filterCondition = (JQuickFilterConditionNode) visit(ctx.filterCondition());
        return new JQuickHavingClauseNode(filterCondition);
    }
    @Override
    public JQuickGroupByClauseNode visitGroupByClause(JQuickSQLParser.GroupByClauseContext ctx) {
        JQuickExpressionsNode expressions = (JQuickExpressionsNode) visit(ctx.expressions());
        return new JQuickGroupByClauseNode(expressions);
    }
    @Override
    public JQuickWhereClauseNode visitWhereClause(JQuickSQLParser.WhereClauseContext ctx) {
        JQuickFilterConditionNode filterCondition = (JQuickFilterConditionNode) visit(ctx.filterCondition());
        return new JQuickWhereClauseNode(filterCondition);
    }
    @Override
    public JQuickSelectClauseNode visitSelectClause(JQuickSQLParser.SelectClauseContext ctx) {
        JQuickSelectClauseNode.Builder builder = new JQuickSelectClauseNode.Builder();
        if (ctx.selectSpec() != null) {
            JQuickSelectSpecNode selectSpec = (JQuickSelectSpecNode) visit(ctx.selectSpec());
            builder.setSelectSpec(selectSpec);
        }
        JQuickSelectElementsNode selectElements = (JQuickSelectElementsNode) visit(ctx.selectElements());
        builder.setSelectElements(selectElements);
        JQuickFromClauseNode fromClause = (JQuickFromClauseNode) visit(ctx.fromClause());
        builder.setFromClause(fromClause);
        for (JQuickSQLParser.JoinClauseContext joinCtx : ctx.joinClause()) {
            JQuickJoinClauseNode joinClause = (JQuickJoinClauseNode) visit(joinCtx);
            builder.addJoinClause(joinClause);
        }
        if (ctx.whereClause() != null) {
            JQuickWhereClauseNode whereClause = (JQuickWhereClauseNode) visit(ctx.whereClause());
            builder.setWhereClause(whereClause);
        }
        if (ctx.groupByClause() != null) {
            JQuickGroupByClauseNode groupByClause = (JQuickGroupByClauseNode) visit(ctx.groupByClause());
            builder.setGroupByClause(groupByClause);
        }
        if (ctx.havingClause() != null) {
            JQuickHavingClauseNode havingClause = (JQuickHavingClauseNode) visit(ctx.havingClause());
            builder.setHavingClause(havingClause);
        }
        if (ctx.orderByClause() != null) {
            JQuickOrderByClauseNode orderByClause = (JQuickOrderByClauseNode) visit(ctx.orderByClause());
            builder.setOrderByClause(orderByClause);
        }
        if (ctx.limitClause() != null) {
            JQuickLimitClauseNode limitClause = (JQuickLimitClauseNode) visit(ctx.limitClause());
            builder.setLimitClause(limitClause);
        }
        return builder.build();
    }
    @Override
    public JQuickDataSetOpNode visitDatasetOp(JQuickSQLParser.DatasetOpContext ctx) {
        List<JQuickSelectClauseNode> selectClauses = new ArrayList<>();
        List<JQuickSQLOperationType> operators = new ArrayList<>();
        selectClauses.add((JQuickSelectClauseNode) visit(ctx.selectClause(0)));
        for (int i = 1; i < ctx.selectClause().size(); i++) {
            int operatorIndex = i * 2 - 1;
            TerminalNode operatorNode = (TerminalNode) ctx.getChild(operatorIndex);
            if (operatorNode.getSymbol().getType() == JQuickSQLParser.UNION) {
                operators.add(JQuickSQLOperationType.UNION);
            } else if (operatorNode.getSymbol().getType() == JQuickSQLParser.MINUS) {
                operators.add(JQuickSQLOperationType.MINUS);
            } else if (operatorNode.getSymbol().getType() == JQuickSQLParser.INTERSECT) {
                operators.add(JQuickSQLOperationType.INTERSECT);
            }
            selectClauses.add((JQuickSelectClauseNode) visit(ctx.selectClause(i)));
        }
        return new JQuickDataSetOpNode(selectClauses, operators);
    }
    @Override
    public JQuickSelectExpressionNode visitSelectExpression(JQuickSQLParser.SelectExpressionContext ctx) {
        JQuickDataSetOpNode dataSetOp = (JQuickDataSetOpNode) visit(ctx.datasetOp());
        return new JQuickSelectExpressionNode(dataSetOp);
    }
}
