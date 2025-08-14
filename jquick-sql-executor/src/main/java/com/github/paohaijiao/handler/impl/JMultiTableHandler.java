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
package com.github.paohaijiao.handler.impl;

import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.handler.JQueryHandler;
import com.github.paohaijiao.join.JEvaluateJoinCondition;
import com.github.paohaijiao.join.JoinPart;
import com.github.paohaijiao.join.JTableSource;
import com.github.paohaijiao.model.JoinResult;
import com.github.paohaijiao.model.JResultMapper;
import com.github.paohaijiao.plan.JExecutionPlan;
import com.github.paohaijiao.util.JEntityAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.handler.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JMultiTableHandler<T> implements JQueryHandler<T> {

    private final JTableSource mainTable;
    private final List<JoinPart> joinParts;
    private final Map<String, List<?>> tableDataMap;
    private final JEntityAccessor<T> mainEntityAccessor;
    private final JResultMapper<T> resultMapper;

    public JMultiTableHandler(Class<T> mainEntityClass, JTableSource mainTable, List<JoinPart> joinParts, Map<String, List<?>> tableDataMap) {
        this.mainTable = mainTable;
        this.joinParts = joinParts;
        this.tableDataMap = tableDataMap;
        this.mainEntityAccessor = new JEntityAccessor<>(mainEntityClass);
        this.resultMapper = new JResultMapper<>(mainEntityClass);
    }

    @Override
    public List<T> handle(List<T> dataset, JExecutionPlan plan) {
        List<T> mainData = getMainTableData();
        List<JoinResult<T>> joinResults = processJoins(mainData);
        return mapToResultEntities(joinResults);
    }

    private List<T> getMainTableData() {
        String mainTableName= mainTable.getAlias() != null ? mainTable.getAlias() : mainTable.getTableName();
        List<?> data = tableDataMap.get(mainTableName);
        return data.stream()
                .map(item -> (T) item)
                .collect(Collectors.toList());
    }

    private List<JoinResult<T>> processJoins(List<T> mainData) {
        List<JoinResult<T>> results = new ArrayList<>();
        for (T mainRecord : mainData) {
            JoinResult<T> joinResult = new JoinResult<>(mainRecord);
            for (JoinPart join : joinParts) {
                processSingleJoin(joinResult, join);
            }
            results.add(joinResult);
        }

        return results;
    }

    private void processSingleJoin(JoinResult<T> joinResult, JoinPart join) {
        JTableSource joinTable = join.getTableSource();
        List<?> joinData = getJoinTableData(joinTable);
        JExpression condition = join.getOnCondition();
        switch (join.getJoinType()) {
            case INNER:
                processInnerJoin(joinResult, joinTable, joinData, condition);
                break;
            case LEFT:
                processLeftJoin(joinResult, joinTable, joinData, condition);
                break;
        }
    }

    private void processInnerJoin(JoinResult<T> joinResult,
                                  JTableSource joinTable,
                                  List<?> joinData,
                                  JExpression condition) {
        boolean matched = false;
        T mainRecord = joinResult.getMainRecord();
        for (Object joinRecord : joinData) {
            if (evaluateJoinCondition(mainRecord, joinRecord, condition)) {
                joinResult.addJoinedRecord(joinTable.getAlias(), joinRecord);
                matched = true;
            }
        }

        if (!matched) {
            joinResult.setExcluded(true);
        }
    }

    private void processLeftJoin(JoinResult<T> joinResult,
                                 JTableSource joinTable,
                                 List<?> joinData,
                                 JExpression condition) {
        boolean matched = false;
        T mainRecord = joinResult.getMainRecord();
        for (Object joinRecord : joinData) {
            if (evaluateJoinCondition(mainRecord, joinRecord, condition)) {
                joinResult.addJoinedRecord(joinTable.getAlias(), joinRecord);
                matched = true;
            }
        }

        if (!matched) {
            joinResult.addJoinedRecord(joinTable.getAlias(), null);
        }
    }

    private List<T> mapToResultEntities(List<JoinResult<T>> joinResults) {
        return joinResults.stream()
                .filter(result -> !result.isExcluded())
                .map(resultMapper::map)
                .collect(Collectors.toList());
    }

    private boolean evaluateJoinCondition(T leftRecord, Object rightRecord, JExpression condition) {
        Class<T> rightEntityAccessor= (Class<T>) rightRecord.getClass();
        return new JEvaluateJoinCondition(mainEntityAccessor,new JEntityAccessor<>(rightEntityAccessor)).evaluateJoinCondition(leftRecord,rightRecord,condition);
    }

    private List<?> getJoinTableData(JTableSource table) {
      String tableName=table.getAlias() != null ? table.getAlias() : table.getTableName();
        return tableDataMap.get(tableName);
    }
}
