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
package com.github.paohaijiao.engine;

import com.github.paohaijiao.ast.JQuickSelectStatementNode;
import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.optimizer.JQuickLogicalPlanOptimizer;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.plan.physical.node.JQuickPhysicalPlanNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.visitor.ASTToLogicalPlanVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Map;

public class JQuickSQLEngine {

    private static JQuickSQLEngine instance;
    private final JQuickLogicalPlanOptimizer optimizer = new JQuickLogicalPlanOptimizer();
    private final JQuickPhysicalPlanGenerator physicalGenerator = new JQuickPhysicalPlanGenerator();

    private JQuickSQLEngine() {}

    public static JQuickSQLEngine getInstance() {
        if (instance == null) {
            synchronized (JQuickSQLEngine.class) {
                if (instance == null) {
                    instance = new JQuickSQLEngine();
                }
            }
        }
        return instance;
    }

    public JQuickDataSet execute(String sql) {
        return execute(sql, new JQuickExecutionContext());
    }

    public JQuickDataSet execute(String sql, JQuickExecutionContext context) {
        try {
            JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(sql));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // 2. 语法分析
            JQuickSQLParser parser = new JQuickSQLParser(tokens);
            JQuickSQLParser.QueryContext parseTree = parser.query();

            // 3. 构建AST (需要通过ParseTreeWalker或自定义构建器)
            // 这里简化处理，实际需要完整的AST构建
            JQuickSelectStatementNode ast = buildAST(parseTree);

            // 4. AST → 逻辑计划
            ASTToLogicalPlanVisitor visitor = new ASTToLogicalPlanVisitor();
            JQuickLogicalPlanNode logicalPlan = visitor.visit(ast);

            // 5. 逻辑计划优化
            JQuickLogicalPlanNode optimizedPlan = optimizer.optimize(logicalPlan);

            // 6. 逻辑计划 → 物理计划（带成本优化）
            JQuickPhysicalPlanNode physicalPlan = physicalGenerator.generate(optimizedPlan);

            // 7. 执行物理计划
            return physicalPlan.execute(context);

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL: " + sql, e);
        }
    }

    public JQuickDataSet execute(String sql, Map<String, Object> parameters) {
        JQuickExecutionContext context = new JQuickExecutionContext();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            context.setParameter(entry.getKey(), entry.getValue());
        }
        return execute(sql, context);
    }

    private JQuickSelectStatementNode buildAST(JQuickSQLParser.QueryContext parseTree) {
        return null;
    }
