package com.github.paohaijiao.adaptor;

import com.github.paohaijiao.config.JDataSourceConfig;
import com.github.paohaijiao.model.JMetadata;
import com.github.paohaijiao.query.JDataSet;
import com.github.paohaijiao.query.JQuery;

public interface JDataSourceAdapter {

    void init(JDataSourceConfig config);

    JMetadata getMetadata();


    JDataSet executeQuery(JQuery query);


    void close();

    boolean testConnection();
}
