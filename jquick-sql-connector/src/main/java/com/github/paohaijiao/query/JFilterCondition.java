package com.github.paohaijiao.query;

import com.github.paohaijiao.enums.JOperator;
import lombok.Data;

import java.util.List;
@Data
public class JFilterCondition {

    private String field;

    private JOperator operator;

    private Object value;

    private List<JFilterCondition> andConditions;

    private List<JFilterCondition> orConditions;
}
