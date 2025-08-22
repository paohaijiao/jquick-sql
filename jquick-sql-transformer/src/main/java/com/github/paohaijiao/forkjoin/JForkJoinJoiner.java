///*
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
// */
//package com.github.paohaijiao.forkjoin;
//
//import com.github.paohaijiao.condition.JCondition;
//import com.github.paohaijiao.dataset.JDataSet;
//import com.github.paohaijiao.expression.JExpression;
//import com.github.paohaijiao.expression.JFunctionCallExpression;
//import com.github.paohaijiao.expression.JOrderByExpression;
//import com.github.paohaijiao.factory.JDataSetJoinerStrategy;
//import com.github.paohaijiao.func.JoinCondition;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * packageName com.github.paohaijiao.forkjoin
// *
// * @author Martin
// * @version 1.0.0
// * @since 2025/8/17
// */
//public class JForkJoinJoiner implements JDataSetJoinerStrategy {
//    @Override
//    public JDataSet innerJoin(JDataSet left, JDataSet right, JoinCondition condition) {
//        return null;
//    }
//
//    @Override
//    public JDataSet leftJoin(JDataSet left, JDataSet right, JoinCondition condition) {
//        return null;
//    }
//
//    @Override
//    public JDataSet fullOuterJoin(JDataSet left, JDataSet right, JoinCondition condition) {
//        return null;
//    }
//
//    @Override
//    public JDataSet crossJoin(JDataSet left, JDataSet right) {
//        return null;
//    }
//
//    @Override
//    public JDataSet naturalJoin(JDataSet left, JDataSet right) {
//        return null;
//    }
//
//    @Override
//    public JDataSet union(JDataSet ds1, JDataSet ds2) {
//        return null;
//    }
//
//    @Override
//    public JDataSet intersect(JDataSet ds1, JDataSet ds2) {
//        return null;
//    }
//
//    @Override
//    public JDataSet minus(JDataSet ds1, JDataSet ds2) {
//        return null;
//    }
//
//    @Override
//    public JDataSet selectColumns(JDataSet dataset, List<String> columnNames) {
//        return null;
//    }
//
//    @Override
//    public JDataSet filter(JDataSet dataset, JCondition condition) {
//        return null;
//    }
//
//    @Override
//    public JDataSet transform(JDataSet dataset, Map<String, JFunctionCallExpression> transformations) {
//        return null;
//    }
//
//    @Override
//    public JDataSet sort(JDataSet dataset, List<JOrderByExpression> orderByExpressions) {
//        return null;
//    }
//
//    @Override
//    public JDataSet aggregate(JDataSet dataset, List<String> groupBy, Map<String, JAggregateExpression> aggregations) {
//        return null;
//    }
//
//    @Override
//    public JDataSet alias(JDataSet dataset, Map<String, JExpression> aliases) {
//        return null;
//    }
//
//    @Override
//    public JDataSet limit(JDataSet dataset, Integer limit, Integer offset) {
//        return null;
//    }
//}
