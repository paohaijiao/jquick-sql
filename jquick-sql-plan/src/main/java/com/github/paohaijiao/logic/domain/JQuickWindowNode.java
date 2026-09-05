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
package com.github.paohaijiao.logic.domain;

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 窗口函数节点 - 描述 OVER 子句
 */
public class JQuickWindowNode implements JQuickLogicalPlanNode {

    private final List<WindowFunction> windowFunctions;

    private final JQuickLogicalPlanNode child;

    public JQuickWindowNode(List<WindowFunction> windowFunctions, JQuickLogicalPlanNode child) {
        this.windowFunctions = Collections.unmodifiableList(new ArrayList<>(windowFunctions));
        this.child = child;
    }

    @Override
    public String getNodeType() {
        return "Window";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.singletonList(child);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        List<String> columns = new ArrayList<>(child.getOutputColumns());
        for (WindowFunction wf : windowFunctions) {
            columns.add(wf.getAlias());
        }
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        List<WindowFunction> clonedFunctions = new ArrayList<>();
        for (WindowFunction wf : windowFunctions) {
            clonedFunctions.add(wf.clone());
        }
        return new JQuickWindowNode(clonedFunctions, child.clone());
    }

    public List<WindowFunction> getWindowFunctions() { return windowFunctions; }

    public JQuickLogicalPlanNode getChild() { return child; }

    /**
     * 窗口函数定义
     */
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

        public WindowFunction clone() {
            return new WindowFunction(functionName, argument != null ? argument.clone() : null, windowSpec.clone(), alias);
        }
    }

    /**
     * 窗口规格
     */
    public static class WindowSpec {

        private final List<JQuickExpression> partitionKeys;

        private final List<JQuickSortNode.OrderByItem> orderKeys;

        private final WindowFrame frame;

        public WindowSpec(List<JQuickExpression> partitionKeys, List<JQuickSortNode.OrderByItem> orderKeys, WindowFrame frame) {
            this.partitionKeys = partitionKeys != null ? new ArrayList<>(partitionKeys) : new ArrayList<>();
            this.orderKeys = orderKeys != null ? new ArrayList<>(orderKeys) : new ArrayList<>();
            this.frame = frame;
        }

        public List<JQuickExpression> getPartitionKeys() { return partitionKeys; }

        public List<JQuickSortNode.OrderByItem> getOrderKeys() { return orderKeys; }

        public WindowFrame getFrame() { return frame; }

        public WindowSpec clone() {
            List<JQuickExpression> clonedPartitionKeys = new ArrayList<>();
            for (JQuickExpression key : partitionKeys) {
                clonedPartitionKeys.add(key.clone());
            }
            List<JQuickSortNode.OrderByItem> clonedOrderKeys = new ArrayList<>();
            for (JQuickSortNode.OrderByItem key : orderKeys) {
                clonedOrderKeys.add(key.clone());
            }
            WindowFrame clonedFrame = frame != null ? frame.clone() : null;
            return new WindowSpec(clonedPartitionKeys, clonedOrderKeys, clonedFrame);
        }
    }

    /**
     * 窗口帧
     */
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

        public WindowFrame clone() {
            return new WindowFrame(frameType, startType, startOffset != null ? startOffset.clone() : null, endType, endOffset != null ? endOffset.clone() : null);
        }
    }
}