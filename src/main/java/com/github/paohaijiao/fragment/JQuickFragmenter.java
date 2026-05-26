package com.github.paohaijiao.fragment;

import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickFragmentType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计划切分器 - 将物理计划切分为分布式片段
 *
 * 核心算法：
 * 1. 深度优先遍历物理计划树
 * 2. 识别切分点（Join、Aggregate、Exchange）
 * 3. 在切分点创建新的 Fragment
 * 4. 建立 Fragment 之间的数据依赖关系
 */
public class JQuickFragmenter {

    private final int defaultParallelism;

    private JQuickFragment currentFragment;

    private final Map<JQuickPhysicalPlanNode, JQuickFragment> nodeToFragment = new HashMap<>();

    private final AtomicInteger exchangeIdGenerator = new AtomicInteger(0);

    private final AtomicInteger fragmentIdGenerator = new AtomicInteger(0);


    private final Set<JQuickPhysicalPlanNode> processedSources = new HashSet<>();

    public JQuickFragmenter() {
        this(4);
    }

    public JQuickFragmenter(int defaultParallelism) {
        this.defaultParallelism = defaultParallelism;
    }

    /**
     * 将物理计划切分为分布式计划
     */
    public JQuickDistributedPlan fragment(JQuickPhysicalPlanNode rootPlan) {
        if (rootPlan == null) {
            throw new IllegalArgumentException("Root plan cannot be null");
        }
        nodeToFragment.clear();
        processedSources.clear();
        exchangeIdGenerator.set(0);
        fragmentIdGenerator.set(0);
        JQuickFragment rootFragment = createFragment(JQuickFragmentType.SINK, rootPlan, 1);
        currentFragment = rootFragment;
        processNode(rootPlan, rootFragment, new HashSet<>());
        JQuickDistributedPlan plan = new JQuickDistributedPlan(rootFragment);
        plan.setDefaultParallelism(defaultParallelism);
        return plan;
    }

    /**
     * 创建新的 Fragment
     */
    private JQuickFragment createFragment(JQuickFragmentType type, JQuickPhysicalPlanNode plan, int parallelism) {
        JQuickFragment fragment = new JQuickFragment(type, plan);
        fragment.setParallelism(parallelism);
        return fragment;
    }

    /**
     * 处理物理计划节点
     */
    private void processNode(JQuickPhysicalPlanNode node, JQuickFragment fragment, Set<JQuickPhysicalPlanNode> visited) {
        if (node == null || visited.contains(node)) {
            return;
        }
        visited.add(node);
        nodeToFragment.put(node, fragment);
        List<JQuickPhysicalPlanNode> children = getChildren(node);
        if (shouldCreateNewFragment(node)) {
            createNewFragmentForNode(node, fragment, children, visited);
            return;
        }
        for (JQuickPhysicalPlanNode child : children) {
            if (shouldBeSourceFragment(child)) {
                createSourceFragment(child, fragment, visited);
            } else {
                processNode(child, fragment, visited);
            }
        }
    }

    /**
     * 判断是否应该为节点创建新的 Fragment
     */
    private boolean shouldCreateNewFragment(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickExchangePhysicalNode) {
            return true;
        }
        if (node instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) node;
            if (join.getDistribution() == JQuickHashJoinPhysicalNode.JoinDistribution.BROADCAST_HASH) {
                return false;
            }
            return true;
        }

        if (node instanceof JQuickNestedLoopJoinPhysicalNode) {
            return true;
        }
        if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            if (agg.getStage() == JQuickHashAggregatePhysicalNode.AggregateStage.FINAL) {
                return false;
            }
            return !agg.getGroupKeys().isEmpty();
        }

        return false;
    }

    /**
     * 判断是否应该作为 SOURCE fragment
     */
    private boolean shouldBeSourceFragment(JQuickPhysicalPlanNode node) {
        return node instanceof JQuickTableScanPhysicalNode && !processedSources.contains(node);
    }

    /**
     * 为节点创建新的 Fragment
     */
    private void createNewFragmentForNode(JQuickPhysicalPlanNode node, JQuickFragment parentFragment, List<JQuickPhysicalPlanNode> children, Set<JQuickPhysicalPlanNode> visited) {
        JQuickFragment newFragment = createFragment(JQuickFragmentType.INTERMEDIATE, node, defaultParallelism);
        JQuickExchangeNode outputExchange = createOutputExchange(node);
        newFragment.setOutput(outputExchange);
        JQuickExchangeNode inputExchange = createInputExchange(outputExchange);
        parentFragment.addInput(inputExchange);
        parentFragment.addChild(newFragment);
        nodeToFragment.put(node, newFragment);
        JQuickFragment savedFragment = currentFragment;
        currentFragment = newFragment;
        for (JQuickPhysicalPlanNode child : children) {
            if (shouldBeSourceFragment(child)) {
                createSourceFragment(child, newFragment, visited);
            } else {
                processNode(child, newFragment, visited);
            }
        }

        currentFragment = savedFragment;
    }

    /**
     * 创建 SOURCE fragment（数据源）
     */
    private void createSourceFragment(JQuickPhysicalPlanNode node, JQuickFragment parentFragment, Set<JQuickPhysicalPlanNode> visited) {
        if (processedSources.contains(node)) {
            return;
        }
        JQuickFragment sourceFragment = createFragment(JQuickFragmentType.SOURCE, node, defaultParallelism);
        JQuickExchangeNode outputExchange = createOutputExchange(node);
        sourceFragment.setOutput(outputExchange);
        JQuickExchangeNode inputExchange = createInputExchange(outputExchange);
        parentFragment.addInput(inputExchange);
        parentFragment.addChild(sourceFragment);
        nodeToFragment.put(node, sourceFragment);
        processedSources.add(node);
    }

    /**
     * 创建输出 Exchange
     */
    private JQuickExchangeNode createOutputExchange(JQuickPhysicalPlanNode node) {
        String exchangeId = "exchange_" + exchangeIdGenerator.incrementAndGet();
        if (node instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) node;
            List<JQuickExpression> partitionKeys = extractJoinPartitionKeys(join);
            return new JQuickExchangeNode(exchangeId, JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, partitionKeys, defaultParallelism);
        }

        if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            List<JQuickExpression> groupKeys = agg.getGroupKeys();
            if (!groupKeys.isEmpty()) {
                return new JQuickExchangeNode(exchangeId, JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, groupKeys, defaultParallelism);
            }
            return new JQuickExchangeNode(exchangeId, JQuickExchangeType.GATHER, JQuickPartitionStrategy.REPLICATE, (List<JQuickExpression>) null, 1);
        }
        if (node instanceof JQuickTableScanPhysicalNode) {
            return new JQuickExchangeNode(exchangeId, JQuickExchangeType.REPARTITION,JQuickPartitionStrategy.ROUND_ROBIN, (List<JQuickExpression>) null, defaultParallelism);
        }
        if (node instanceof JQuickExchangePhysicalNode) {
            JQuickExchangePhysicalNode exchange = (JQuickExchangePhysicalNode) node;
            return new JQuickExchangeNode(exchangeId, exchange.getExchangeType(), exchange.getPartitionStrategy(), exchange.getPartitionKeys(), exchange.getTargetParallelism());
        }
        return new JQuickExchangeNode(exchangeId, JQuickExchangeType.BROADCAST, JQuickPartitionStrategy.REPLICATE, (List<JQuickExpression>) null, defaultParallelism
        );
    }

    /**
     * 创建输入 Exchange（接收数据）
     */
    private JQuickExchangeNode createInputExchange(JQuickExchangeNode outputExchange) {
        return new JQuickExchangeNode("input_" + outputExchange.getExchangeId(), JQuickExchangeType.RECEIVE, outputExchange.getPartitionStrategy(), outputExchange.getPartitionKeys(), outputExchange.getParallelism());
    }

    /**
     * 提取 JOIN 的分区键
     */
    private List<JQuickExpression> extractJoinPartitionKeys(JQuickHashJoinPhysicalNode join) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = join.getJoinKeys();
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        if (joinKeys != null && !joinKeys.isEmpty()) {
            for (JQuickHashJoinPhysicalNode.JoinKeyPair keyPair : joinKeys) {
                if (join.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT) {
                    partitionKeys.add(keyPair.getRightKey());
                } else {
                    partitionKeys.add(keyPair.getLeftKey());
                }
            }
        }

        return partitionKeys;
    }


    /**
     * 获取物理节点的子节点
     */
    private List<JQuickPhysicalPlanNode> getChildren(JQuickPhysicalPlanNode node) {
        if (node == null) {
            return Collections.emptyList();
        }
        List<JQuickPhysicalPlanNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            return children;
        }
        if (node instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) node;
            List<JQuickPhysicalPlanNode> result = new ArrayList<>();
            JQuickPhysicalPlanNode left = join.getLeft();
            JQuickPhysicalPlanNode right = join.getRight();
            if (left != null) result.add(left);
            if (right != null) result.add(right);
            return result;
        }
        if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            JQuickPhysicalPlanNode child = agg.getChild();
            return child != null ? Collections.singletonList(child) : Collections.emptyList();
        }
        if (node instanceof JQuickAbstractPhysicalNode) {
            JQuickAbstractPhysicalNode absNode = (JQuickAbstractPhysicalNode) node;
            return absNode.getChildren();
        }
        return Collections.emptyList();
    }

    /**
     * 打印片段信息（用于调试）
     */
    public void printFragments(JQuickDistributedPlan plan) {
        System.out.println("=== JQuickDistributedPlan ===");
        System.out.println("Default Parallelism: " + plan.getDefaultParallelism());
        System.out.println();
        JQuickFragment root = plan.getRootFragment();
        printFragment(root, 0);
    }

    private void printFragment(JQuickFragment fragment, int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        System.out.println(indent + "┌─ Fragment " + fragment.getFragmentId());
        System.out.println(indent + "│   Type: " + fragment.getType());
        System.out.println(indent + "│   Parallelism: " + fragment.getParallelism());
        System.out.println(indent + "│   Plan Node: " + fragment.getPlan().getNodeType());
        JQuickExchangeNode output = fragment.getOutput();
        if (output != null) {
            System.out.println(indent + "│   Output: " + output);
        }
        if (!fragment.getInputs().isEmpty()) {
            System.out.println(indent + "│   Inputs: " + fragment.getInputs().size());
        }
        List<JQuickFragment> children = fragment.getChildren();
        if (!children.isEmpty()) {
            System.out.println(indent + "│");
            for (int i = 0; i < children.size(); i++) {
                boolean isLast = (i == children.size() - 1);
                System.out.println(indent + (isLast ? "└── " : "├── ") + "Child Fragment " +
                        children.get(i).getFragmentId());
                printFragment(children.get(i), depth + 1);
            }
        } else {
            System.out.println(indent + "└── (leaf)");
        }
    }
}