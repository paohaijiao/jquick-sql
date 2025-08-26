package com.github.paohaijiao.connector;

import com.github.paohaijiao.dataset.JDataSet;

public interface JDataSetConnector {

    String getConnectorType();

    JDataSet load(Object source);
}
