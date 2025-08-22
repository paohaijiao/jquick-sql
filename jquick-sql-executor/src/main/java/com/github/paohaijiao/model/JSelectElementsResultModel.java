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
package com.github.paohaijiao.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.model
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/22
 */
public class JSelectElementsResultModel {
    private final boolean hasStar;
    private final List<JSelectElementModel> elements;

    public JSelectElementsResultModel(boolean hasStar, List<JSelectElementModel> elements) {
        this.hasStar = hasStar;
        this.elements = elements;
    }

    public boolean hasStar() {
        return hasStar;
    }

    public List<JSelectElementModel> getElements() {
        return elements;
    }
    public boolean hasAggregateFunction() {
        List<JSelectElementModel> list=elements.stream().filter(JSelectElementModel::isAggregate).collect(Collectors.toList());
        return !list.isEmpty();
    }
    public List<JSelectElementModel> getAggregateFunction() {
        List<JSelectElementModel> list=elements.stream().filter(JSelectElementModel::isAggregate).collect(Collectors.toList());
        return list;
    }
}
