/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.distributed.worker;

import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.proto.JQuickColumnMetaProto;
import com.github.paohaijiao.proto.JQuickDataSetProto;
import com.github.paohaijiao.proto.JQuickRowProto;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.google.protobuf.Any;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据转换服务 - 处理 Proto 和内部数据结构的转换
 */
public class JQuickDataConverter {

    /**
     * 将内部 DataSet 转换为 Proto
     */
    public JQuickDataSetProto convertToProto(JQuickDataSet data) {
        JQuickDataSetProto.Builder builder = JQuickDataSetProto.newBuilder();
        for (JQuickColumnMeta col : data.getColumns()) {
            builder.addColumns(JQuickColumnMetaProto.newBuilder().setName(col.getName())
            .setTypeName(col.getType().getName()).setSource(col.getSource()).build());
        }
        for (JQuickRow row : data.getRows()) {
            JQuickRowProto.Builder rowBuilder = JQuickRowProto.newBuilder();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                Any anyValue = Any.pack(Value.newBuilder().setStringValue(entry.getValue() != null ? entry.getValue().toString() : "").build());
                rowBuilder.putData(entry.getKey(), anyValue);
            }
            builder.addRows(rowBuilder.build());
        }
        builder.setTotalRows(data.size());
        return builder.build();
    }

    /**
     * 将 Proto 转换为内部 DataSet
     */
    public JQuickDataSet convertFromProto(JQuickDataSetProto proto) {
        List<JQuickColumnMeta> columns = new ArrayList<>();
        for (JQuickColumnMetaProto colProto : proto.getColumnsList()) {
            try {
                Class<?> clazz = Class.forName(colProto.getTypeName());
                columns.add(new JQuickColumnMeta(colProto.getName(), clazz, colProto.getSource()));
            } catch (ClassNotFoundException e) {
                columns.add(new JQuickColumnMeta(colProto.getName(), Object.class, colProto.getSource()));
            }
        }
        List<JQuickRow> rows = new ArrayList<>();
        for (JQuickRowProto rowProto : proto.getRowsList()) {
            JQuickRow row = new JQuickRow();
            for (Map.Entry<String, Any> entry : rowProto.getDataMap().entrySet()) {
                try {
                    if (!entry.getValue().is(Value.class)) {
                        row.put(entry.getKey(), entry.getValue().toString());
                    } else {
                        Value value = entry.getValue().unpack(Value.class);
                        row.put(entry.getKey(), value.getStringValue());
                    }
                } catch (Exception e) {
                    row.put(entry.getKey(), entry.getValue().toString());
                }
            }
            rows.add(row);
        }
        return new JQuickDataSet(columns, rows);
    }

    /**
     * 构建输出 Schema
     */
    public List<JQuickPhysicalColumn> buildOutputSchema(JQuickPhysicalPlanNode node) {
        if (node == null) {
            return new ArrayList<>();
        }
        List<JQuickPhysicalColumn> outputSchema = node.getOutputSchema();
        if (outputSchema == null) {
            return new ArrayList<>();
        }
        List<JQuickPhysicalColumn> defensiveCopy = new ArrayList<>();
        for (JQuickPhysicalColumn col : outputSchema) {
            defensiveCopy.add(new JQuickPhysicalColumn(col.getName(), col.getType(), col.getSourceTable(), col.isNullable()));
        }
        return defensiveCopy;
    }
}
