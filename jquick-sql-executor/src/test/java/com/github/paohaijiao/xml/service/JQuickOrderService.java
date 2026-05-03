package com.github.paohaijiao.xml.service;

import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.xml.param.Param;

public interface JQuickOrderService {

    JQuickDataSet getOrders(@Param("limit")Integer limit);
}
