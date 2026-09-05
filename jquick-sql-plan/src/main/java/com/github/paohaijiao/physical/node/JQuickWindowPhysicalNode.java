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
package com.github.paohaijiao.physical.node;

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickColumnStats;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JQuickWindowPhysicalNode extends JQuickAbstractPhysicalNode {

    private final List<WindowFunction> windowFunctions;

    public static class WindowFunction {

        private final String functionName;

        private final JQuickExpression argument;

        private final WindowSpec windowSpec;

        private final String alias;

        public WindowFunction(String functionName, JQuickExpression argument, WindowSpec windowSpec, String alias) {
            this.functionName = functionName;
            this.argument = argument;
            this.windowSpec = windowSpec;
            this.alias = alias;
        }

        public String getFunctionName() { return functionName; }

        public JQuickExpression getArgument() { return argument; }

        public WindowSpec getWindowSpec() { return windowSpec; }

        public String getAlias() { return alias; }
    }

    public static class WindowSpec {

        private final List<JQuickExpression> partitionKeys;

        private final List<JQuickSortPhysicalNode.OrderByItem> orderKeys;

        private final WindowFrame frame;

        public WindowSpec(List<JQuickExpression> partitionKeys, List<JQuickSortPhysicalNode.OrderByItem> orderKeys, WindowFrame frame) {
            this.partitionKeys = partitionKeys != null ? new ArrayList<>(partitionKeys) : new ArrayList<>();
            this.orderKeys = orderKeys != null ? new ArrayList<>(orderKeys) : new ArrayList<>();
            this.frame = frame;
        }

        public List<JQuickExpression> getPartitionKeys() { return partitionKeys; }

        public List<JQuickSortPhysicalNode.OrderByItem> getOrderKeys() { return orderKeys; }

        public WindowFrame getFrame() { return frame; }
    }

    public static class WindowFrame {

        public enum FrameType { ROWS, RANGE }

        public enum BoundaryType { UNBOUNDED_PRECEDING, PRECEDING, CURRENT_ROW, FOLLOWING, UNBOUNDED_FOLLOWING }

        private final FrameType frameType;

        private final BoundaryType startType;

        private final JQuickExpression startOffset;

        private final BoundaryType endType;

        private final JQuickExpression endOffset;

        public WindowFrame(FrameType frameType, BoundaryType startType, JQuickExpression startOffset, BoundaryType endType, JQuickExpression endOffset) {
            this.frameType = frameType;
            this.startType = startType;
            this.startOffset = startOffset;
            this.endType = endType;
            this.endOffset = endOffset;
        }

        public FrameType getFrameType() { return frameType; }

        public BoundaryType getStartType() { return startType; }

        public JQuickExpression getStartOffset() { return startOffset; }

        public BoundaryType getEndType() { return endType; }

        public JQuickExpression getEndOffset() { return endOffset; }
    }

    public JQuickWindowPhysicalNode(List<WindowFunction> windowFunctions, JQuickPhysicalPlanNode child) {
        super(child);
        this.windowFunctions = new ArrayList<>(windowFunctions);
    }

    @Override
    public String getNodeType() {
        return "Window";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        List<JQuickPhysicalColumn> schema = new ArrayList<>(children.get(0).getOutputSchema());
        for (WindowFunction wf : windowFunctions) {
            schema.add(new JQuickPhysicalColumn(wf.getAlias(), Object.class, null, true));
        }
        return schema;
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        return new JQuickWindowPhysicalNode(windowFunctions, children.get(0).clone());
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public List<WindowFunction> getWindowFunctions() { return windowFunctions; }

    @Override
    public JQuickPhysicalStats getStats() {
        JQuickPhysicalPlanNode child = getChild();
        if (child == null) {
            return JQuickPhysicalStats.empty();
        }
        JQuickPhysicalStats childStats = child.getStats();
        long estimatedRows = childStats.getEstimatedRowCount();
        int windowFunctionCount = windowFunctions != null ? windowFunctions.size() : 0;
        long estimatedDataSize = childStats.getEstimatedDataSize() + (estimatedRows * windowFunctionCount * 100);
        Map<String, JQuickColumnStats> columnStats = new HashMap<>(childStats.getColumnStats());
        return new JQuickPhysicalStats(estimatedRows, estimatedDataSize, columnStats);
    }
}
