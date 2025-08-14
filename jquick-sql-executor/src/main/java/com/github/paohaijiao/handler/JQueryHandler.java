package com.github.paohaijiao.handler;

import com.github.paohaijiao.plan.JExecutionPlan;

import java.util.List;

public interface JQueryHandler<T> {

    List<T> handle(List<T> dataset, JExecutionPlan plan);

}
