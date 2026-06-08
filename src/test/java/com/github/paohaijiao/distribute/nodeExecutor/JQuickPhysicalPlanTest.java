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
package com.github.paohaijiao.distribute.nodeExecutor;

import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickPhysicalPlanTest {
    private JQuickPhysicalPlanGenerator generator;
    private JQuickFragmenter fragmenter;
    private JQuickFragmenter verboseFragmenter;
    @Before
    public void setUp() {
        generator = new JQuickPhysicalPlanGenerator();
        fragmenter = new JQuickFragmenter(4);
        verboseFragmenter = new JQuickFragmenter(8);
    }
    @Test
    public void testTableScanConversion() {
        JQuickTableScanNode tableScan = new JQuickTableScanNode("users", "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(tableScan);
        assertNotNull(physicalPlan);
        assertEquals("TableScan", physicalPlan.getNodeType());
        assertTrue(physicalPlan instanceof JQuickTableScanPhysicalNode);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertEquals("users", scanNode.getTableName());
        assertEquals("u", scanNode.getAlias());
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(scanNode);
        fragmenter.printFragments(distributedPlan);
    }
}
