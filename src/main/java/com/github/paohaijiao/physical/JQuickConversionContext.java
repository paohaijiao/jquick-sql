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
package com.github.paohaijiao.physical;

import com.github.paohaijiao.physical.node.JQuickPhysicalPlanNode;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.plan.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/16
 */
public class JQuickConversionContext {

    private final Map<String, JQuickPhysicalPlanNode> cteCache = new HashMap<>();

    public void cacheCTE(String name, JQuickPhysicalPlanNode plan) {
        cteCache.put(name, plan);
    }

    public JQuickPhysicalPlanNode getCachedCTE(String name) {
        return cteCache.get(name);
    }
}
