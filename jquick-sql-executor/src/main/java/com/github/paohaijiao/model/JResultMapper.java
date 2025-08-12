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

import com.github.paohaijiao.util.JEntityAccessor;

import java.util.Map;

/**
 * packageName com.github.paohaijiao.model
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JResultMapper<T>{
    private final Class<T> targetClass;
    private final JEntityAccessor<T> targetAccessor;

    public JResultMapper(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.targetAccessor = new JEntityAccessor<>(targetClass);
    }

    public T map(JoinResult<T> joinResult) {
        try {
            T result = targetClass.newInstance();
            copyMainRecordFields(joinResult.getMainRecord(), result);
            for (Map.Entry<String, Object> entry : joinResult.getJoinedRecords().entrySet()) {
                String prefix = entry.getKey() + "_";
                copyJoinedRecordFields(entry.getValue(), result, prefix);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map join result", e);
        }
    }

    private void copyMainRecordFields(T source, T target) {
        targetAccessor.getFieldNames().forEach(field -> {
            Object value = targetAccessor.getValue(source, field);
            targetAccessor.setValue(target, field, value);
        });
    }

    private void copyJoinedRecordFields(Object joinedRecord, T target, String prefix) {
        if (joinedRecord == null) return;
        JEntityAccessor<?> sourceAccessor = new JEntityAccessor<>(joinedRecord.getClass());
        sourceAccessor.getFieldNames().forEach(field -> {
            String targetField = prefix + field;
            if (targetAccessor.hasField(targetField)) {
//                Object value = sourceAccessor.getValue(joinedRecord, field);
//                targetAccessor.setValue(target, targetField, null);//value
            }
        });
    }
}
