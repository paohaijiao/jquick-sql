package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JQuickSqlExpressionType;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import lombok.Getter;

import java.util.Map;

@Getter
public class JQuickSqlDiceExpression extends JQuickSqlExpression {


    private Map<JQuickSqlExpression, JQuickSqlExpression> conditions;


    public JQuickSqlDiceExpression(Map<JQuickSqlExpression, JQuickSqlExpression> conditions) {

        this.type = JQuickSqlExpressionType.OLAP;

        this.conditions = conditions;
    }
}
