package com.github.paohaijiao.example;

import com.github.paohaijiao.adaptor.JCsvAdapter;
import com.github.paohaijiao.config.JDataSourceConfig;
import com.github.paohaijiao.model.JMetadata;
import com.github.paohaijiao.plan.JDataSet;
import com.github.paohaijiao.plan.JQuery;
import org.junit.Test;

import java.io.IOException;

public class JExampleTest {
    @Test
    public void length() throws IOException {
        JDataSourceConfig config = new JDataSourceConfig();
        config.setType("csv");
        config.setPath("D:\\my\\jquick-sql\\jquick-sql-connector\\src\\main\\resources\\data\\products.csv");
        config.put("hasHeader", "true");
        config.put("delimiter", ",");
        config.put("encoding", "UTF-8");
        JCsvAdapter adapter = new JCsvAdapter();
        adapter.init(config);
        JMetadata metadata = adapter.getMetadata();
        System.out.println("CSV结构:");
        metadata.getEntities().forEach(entity -> {
            System.out.println("表: " + entity.getName());
            entity.getFields().forEach(field -> {
                System.out.printf("  %s: %s%n", field.getName(), field.getType());
            });
        });

        // 执行查询
        JQuery query = new JQuery();
        query.setEntityName(metadata.getEntities().get(0).getName());
        JDataSet dataSet = adapter.executeQuery(query);


    }
}
