package com.github.paohaijiao.support;


import java.util.Map;

public interface JEvaluator <R,T>{

    public T evaluate(R expression, Map<String, Object> row);
}
