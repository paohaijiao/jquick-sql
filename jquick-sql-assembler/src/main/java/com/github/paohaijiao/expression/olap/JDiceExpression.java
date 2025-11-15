package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JExpressionType;
import com.github.paohaijiao.expression.JExpression;
import lombok.Getter;

import java.util.Map;

@Getter
public class JDiceExpression extends JExpression {


    private   Map<JExpression, JExpression> conditions;


    public JDiceExpression(Map<JExpression, JExpression> conditions) {

        this.type = JExpressionType.OLAP;

        this.conditions = conditions;
    }
}
