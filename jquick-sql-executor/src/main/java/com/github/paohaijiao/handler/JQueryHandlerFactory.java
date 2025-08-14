package com.github.paohaijiao.handler;

import com.github.paohaijiao.engine.JEntityQueryEngine;
import com.github.paohaijiao.plan.JExecutionPlan;

import java.util.List;

public interface JQueryHandlerFactory<T> {

    JQueryHandler<T> createHandler(JExecutionPlan plan);

    public List<JQueryHandler<T>> createExecutionChain(JEntityQueryEngine<?> engine, JExecutionPlan plan);

}
