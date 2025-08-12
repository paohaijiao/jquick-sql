package com.github.paohaijiao.handler;

import com.github.paohaijiao.query.JQueryPlan;

import java.util.List;

public interface JQueryHandler<T> {

    List<T> handle(List<T> dataset, JQueryPlan plan);

}
