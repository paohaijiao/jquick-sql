package com.github.paohaijiao.xml.service;

import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.param.Param;

import java.util.List;

public interface JQuickUserService {

    JQuickDataSet getUsers(@Param("limit")Integer limit);

    List<JQuickRow> getUsersOne(@Param("limit")Integer limit);

    Integer getUsersTwo(@Param("limit")Integer limit);
}
