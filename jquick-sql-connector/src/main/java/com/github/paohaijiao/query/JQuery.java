package com.github.paohaijiao.query;

import com.github.paohaijiao.model.JSortField;
import javafx.scene.control.Pagination;
import lombok.Data;

import java.util.List;
@Data
public class JQuery {

    private String entityName;

    private List<String> fields;

    private JFilterCondition condition;

    private List<JSortField> sortFields;

    private Pagination pagination;
}
