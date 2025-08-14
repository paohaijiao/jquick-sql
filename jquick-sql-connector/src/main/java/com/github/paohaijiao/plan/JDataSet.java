package com.github.paohaijiao.plan;

import com.github.paohaijiao.model.JMetadata;
import lombok.Data;

import java.util.List;
@Data
public class JDataSet {

    private JMetadata metadata;

    private List<JDataRow> rows;
}
