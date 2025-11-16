package com.github.paohaijiao.evalue;


import java.util.Map;

public interface JSqlEvaluator<R, T> {

    public T evaluate(R expression, Map<String, Object> row);
}
