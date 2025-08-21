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


import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.support.JDataSetRecursiveQuery;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * packageName PACKAGE_NAME
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/20
 */
public class JDataSetRecursiveQueryTest {
    private JDataSet createEmployeeDataSet() {
        JRow ceo = new JRow();
        ceo.put("id", 1);
        ceo.put("name", "Alice");
        ceo.put("manager_id", null);
        JRow manager1 = new JRow();
        manager1.put("id", 2);
        manager1.put("name", "Bob");
        manager1.put("manager_id", 1);

        JRow manager2 = new JRow();
        manager2.put("id", 3);
        manager2.put("name", "Charlie");
        manager2.put("manager_id", 1);

        JRow employee1 = new JRow();
        employee1.put("id", 4);
        employee1.put("name", "David");
        employee1.put("manager_id", 2);

        JRow employee2 = new JRow();
        employee2.put("id", 5);
        employee2.put("name", "Eve");
        employee2.put("manager_id", 3);

        return JDataSet.builder()
                .addColumn("id", Integer.class, "employees")
                .addColumn("name", String.class, "employees")
                .addColumn("manager_id", Integer.class, "employees")
                .addRow(ceo)
                .addRow(manager1)
                .addRow(manager2)
                .addRow(employee1)
                .addRow(employee2)
                .build();
    }

    private JDataSet createLinearHierarchyDataSet() {
        JDataSet.Builder builder = JDataSet.builder()
                .addColumn("value", Integer.class, "hierarchy")
                .addColumn("parent", Integer.class, "hierarchy");
        for (int i = 1; i <= 5; i++) {
            JRow row = new JRow();
            row.put("value", i);
            row.put("parent", i == 1 ? null : i - 1);
            builder.addRow(row);
        }

        return builder.build();
    }
    @Test
    public void testEmployeeHierarchy() {
        JDataSet allEmployees = createEmployeeDataSet();
        JDataSet ceo = allEmployees.filter(row -> row.get("manager_id") == null);
        assertEquals(1, ceo.size());
        assertEquals("Alice", ceo.getRows().get(0).get("name"));
        Function<JDataSet, JDataSet> findSubordinates = currentEmployees -> {
            Set<Integer> managerIds = currentEmployees.getRows().stream()
                    .map(row -> (Integer) row.get("id"))
                    .collect(Collectors.toSet());
            List<JRow> subordinates = allEmployees.getRows().stream()
                    .filter(row -> {
                        Integer managerId = (Integer) row.get("manager_id");
                        return managerId != null && managerIds.contains(managerId);
                    })
                    .collect(Collectors.toList());

            return new JDataSet(allEmployees.getColumns(), subordinates);
        };
        JDataSet allSubordinates = JDataSetRecursiveQuery.withRecursive(
                ceo, findSubordinates, 10, true);
        assertEquals(5, allSubordinates.size());
        assertTrue(allSubordinates.getRows().stream()
                .anyMatch(row -> "Charlie".equals(row.get("name"))));
        assertTrue(allSubordinates.getRows().stream()
                .anyMatch(row -> "Eve".equals(row.get("name"))));
    }

    @Test
    public void testFullOrganizationHierarchy() {
        JDataSet allEmployees = createEmployeeDataSet();
        Function<JDataSet, JDataSet> recursiveFunction = JDataSetRecursiveQuery.buildHierarchicalRecursiveFunction(
                        allEmployees,
                        "manager_id", "id");
        JDataSet ceo = allEmployees.filter(row -> row.get("manager_id") == null);
        JDataSet fullHierarchy = JDataSetRecursiveQuery.withRecursive(ceo, recursiveFunction, 10);
        assertEquals(5, fullHierarchy.size());
        List<String> names = fullHierarchy.getRows().stream()
                .map(row -> (String) row.get("name"))
                .sorted()
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("Alice","Bob", "Charlie", "David", "Eve"), names);
    }

    /**
     * 测试无限递归保护
     */
    @Test
    public void testMaxDepthProtection() {
        JDataSet allEmployees = createEmployeeDataSet();
        JRow cyclicEmployee = new JRow();
        cyclicEmployee.put("id", 99);
        cyclicEmployee.put("name", "Cyclic");
        cyclicEmployee.put("manager_id", 99); // 自己管理自己
        JDataSet cyclicDataSet = JDataSet.builder()
                .addColumn("id", Integer.class, "test")
                .addColumn("name", String.class, "test")
                .addColumn("manager_id", Integer.class, "test")
                .addRow(cyclicEmployee)
                .build();
        Function<JDataSet, JDataSet> recursiveFunction = current -> {
            Integer currentId = (Integer) current.getRows().get(0).get("id");
            return cyclicDataSet.filter(row -> ((Integer) row.get("manager_id")).equals(currentId));
        };
        JDataSet result = JDataSetRecursiveQuery.withRecursive(cyclicDataSet, recursiveFunction, 3);
        assertEquals(3, result.size());
    }

    /**
     * 测试不去重的情况
     */
    @Test
    public void testWithoutDistinct() {
        JDataSet data = createLinearHierarchyDataSet();
        Function<JDataSet, JDataSet> recursiveFunction = current -> {
            Integer currentValue = (Integer) current.getRows().get(0).get("value");
            return data.filter(row ->null!= row.get("parent")&&((Integer) row.get("parent")).equals(currentValue));
        };
        JDataSet result = JDataSetRecursiveQuery.withRecursive(
                data.filter(row -> ((Integer) row.get("value")) == 1),
                recursiveFunction, 5, false);
        assertTrue(result.size() > 5);
    }



    @Test
    public void testEmptyDataSet() {
        JDataSet empty = JDataSet.builder()
                .addColumn("id", Integer.class, "test")
                .build();
        Function<JDataSet, JDataSet> recursiveFunction = data -> data;

        JDataSet result = JDataSetRecursiveQuery.withRecursive(
                empty, recursiveFunction, 5);

        assertTrue(result.isEmpty());
    }

    /**
     * 测试单层递归
     */
    @Test
    public void testSingleLevelRecursion() {
        JDataSet data = createEmployeeDataSet();
        Function<JDataSet, JDataSet> findDirectSubordinates = current -> {
            Set<Integer> managerIds = current.getRows().stream()
                    .map(row -> (Integer) row.get("id"))
                    .collect(Collectors.toSet());
            List<JRow> subordinates = data.getRows().stream()
                    .filter(row -> {
                        Integer managerId = (Integer) row.get("manager_id");
                        return managerId != null && managerIds.contains(managerId);
                    })
                    .collect(Collectors.toList());
            return new JDataSet(data.getColumns(), subordinates);
        };
        JDataSet ceo = data.filter(row -> row.get("manager_id") == null);
        JDataSet directSubordinates = JDataSetRecursiveQuery.withRecursive(
                ceo, findDirectSubordinates, 2);
        assertEquals(3, directSubordinates.size());
        List<String> names = directSubordinates.getRows().stream()
                .map(row -> (String) row.get("name"))
                .sorted()
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("Alice","Bob", "Charlie"), names);
    }
}
