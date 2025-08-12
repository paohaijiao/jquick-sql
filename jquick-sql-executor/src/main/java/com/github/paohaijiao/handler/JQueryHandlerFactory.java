package com.github.paohaijiao.handler;

import com.github.paohaijiao.query.JQueryPlan;

import java.util.List;

public interface JQueryHandlerFactory<T> {

    JQueryHandler<T> createHandler(JQueryPlan plan);

    public List<JQueryHandler<T>> createExecutionChain(JQueryPlan plan);

}
