package com.github.paohaijiao.manage;

import com.github.paohaijiao.adaptor.JDataSourceAdapter;
import com.github.paohaijiao.model.JMetadata;
import com.github.paohaijiao.query.JDataSet;
import com.github.paohaijiao.query.JQuery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JMetadataManager {

    private Map<String, JDataSourceAdapter> adapters = new ConcurrentHashMap<>();

    public void registerAdapter(String dataSourceId, JDataSourceAdapter adapter) {
        adapters.put(dataSourceId, adapter);
    }

    public JMetadata getMetadata(String dataSourceId) {
        return adapters.get(dataSourceId).getMetadata();
    }

    public JDataSet query(String dataSourceId, JQuery query) {
        return adapters.get(dataSourceId).executeQuery(query);
    }

    public void close(String dataSourceId) {
        adapters.get(dataSourceId).close();
    }
}
