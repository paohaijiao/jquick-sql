package com.github.paohaijiao.xml.dao;

import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.model.DataStatistics;
import com.github.paohaijiao.xml.param.Param;

import java.util.Date;
import java.util.List;

public interface DataDao {

    public List<JQuickRow>  selectData(@Param("deptId" )Long deptId, @Param("type" )String type, @Param("level" )String level, @Param("startDate" ) Date startDate, @Param("endDate" ) Date endDate);
}
