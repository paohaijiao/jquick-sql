package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JExpressionType;
import com.github.paohaijiao.expression.JExpression;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
@Getter
public class JRollUpExpression extends JExpression {

    private  List<String> groupByColumns;

    private Map<String, Function<List<Object>, Object>> aggregations;


    public JRollUpExpression(List<String> groupByColumns, Map<String, Function<List<Object>, Object>> aggregations) {
        this.type = JExpressionType.OLAP;
        this.groupByColumns = groupByColumns;
        this.aggregations = aggregations;
    }
}
