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

import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.support.JQuickSqlDataSetRecursiveQuery;
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
    private JQuickDataSet createEmployeeDataSet() {
        JQuickRow ceo = new JQuickRow();
        ceo.put("id", 1);
        ceo.put("name", "Alice");
        ceo.put("manager_id", null);
        JQuickRow manager1 = new JQuickRow();
        manager1.put("id", 2);
        manager1.put("name", "Bob");
        manager1.put("manager_id", 1);

        JQuickRow manager2 = new JQuickRow();
        manager2.put("id", 3);
        manager2.put("name", "Charlie");
        manager2.put("manager_id", 1);

        JQuickRow employee1 = new JQuickRow();
        employee1.put("id", 4);
        employee1.put("name", "David");
        employee1.put("manager_id", 2);

        JQuickRow employee2 = new JQuickRow();
        employee2.put("id", 5);
        employee2.put("name", "Eve");
        employee2.put("manager_id", 3);

        return JQuickDataSet.builder()
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

    private JQuickDataSet createLinearHierarchyDataSet() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder()
                .addColumn("value", Integer.class, "hierarchy")
                .addColumn("parent", Integer.class, "hierarchy");
        for (int i = 1; i <= 5; i++) {
            JQuickRow row = new JQuickRow();
            row.put("value", i);
            row.put("parent", i == 1 ? null : i - 1);
            builder.addRow(row);
        }

        return builder.build();
    }

    @Test
    public void testEmployeeHierarchy() {
        JQuickDataSet allEmployees = createEmployeeDataSet();
        JQuickDataSet ceo = allEmployees.filter(row -> row.get("manager_id") == null);
        assertEquals(1, ceo.size());
        assertEquals("Alice", ceo.getRows().get(0).get("name"));
        Function<JQuickDataSet, JQuickDataSet> findSubordinates = currentEmployees -> {
            Set<Integer> managerIds = currentEmployees.getRows().stream()
                    .map(row -> (Integer) row.get("id"))
                    .collect(Collectors.toSet());
            List<JQuickRow> subordinates = allEmployees.getRows().stream()
                    .filter(row -> {
                        Integer managerId = (Integer) row.get("manager_id");
                        return managerId != null && managerIds.contains(managerId);
                    })
                    .collect(Collectors.toList());

            return new JQuickDataSet(allEmployees.getColumns(), subordinates);
        };
        JQuickDataSet allSubordinates = JQuickSqlDataSetRecursiveQuery.withRecursive(
                ceo, findSubordinates, 10, true);
        assertEquals(5, allSubordinates.size());
        assertTrue(allSubordinates.getRows().stream()
                .anyMatch(row -> "Charlie".equals(row.get("name"))));
        assertTrue(allSubordinates.getRows().stream()
                .anyMatch(row -> "Eve".equals(row.get("name"))));
    }

    @Test
    public void testFullOrganizationHierarchy() {
        JQuickDataSet allEmployees = createEmployeeDataSet();
        Function<JQuickDataSet, JQuickDataSet> recursiveFunction = JQuickSqlDataSetRecursiveQuery.buildHierarchicalRecursiveFunction(
                allEmployees,
                "manager_id", "id");
        JQuickDataSet ceo = allEmployees.filter(row -> row.get("manager_id") == null);
        JQuickDataSet fullHierarchy = JQuickSqlDataSetRecursiveQuery.withRecursive(ceo, recursiveFunction, 10);
        assertEquals(5, fullHierarchy.size());
        List<String> names = fullHierarchy.getRows().stream()
                .map(row -> (String) row.get("name"))
                .sorted()
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve"), names);
    }

    /**
     * 测试无限递归保护
     */
    @Test
    public void testMaxDepthProtection() {
        JQuickRow cyclicEmployee = new JQuickRow();
        cyclicEmployee.put("id", 99);
        cyclicEmployee.put("name", "Cyclic");
        cyclicEmployee.put("manager_id", 99); // 自己管理自己
        JQuickDataSet cyclicDataSet = JQuickDataSet.builder()
                .addColumn("id", Integer.class, "test")
                .addColumn("name", String.class, "test")
                .addColumn("manager_id", Integer.class, "test")
                .addRow(cyclicEmployee)
                .build();
        Function<JQuickDataSet, JQuickDataSet> recursiveFunction = current -> {
            Integer currentId = (Integer) current.getRows().get(0).get("id");
            return cyclicDataSet.filter(row -> row.get("manager_id").equals(currentId));
        };
        JQuickDataSet result = JQuickSqlDataSetRecursiveQuery.withRecursive(cyclicDataSet, recursiveFunction, 3);
        assertEquals(3, result.size());
    }

    /**
     * 测试不去重的情况
     */
    @Test
    public void testWithoutDistinct() {
        JQuickDataSet data = createLinearHierarchyDataSet();
        Function<JQuickDataSet, JQuickDataSet> recursiveFunction = current -> {
            Integer currentValue = (Integer) current.getRows().get(0).get("value");
            return data.filter(row -> null != row.get("parent") && row.get("parent").equals(currentValue));
        };
        JQuickDataSet result = JQuickSqlDataSetRecursiveQuery.withRecursive(
                data.filter(row -> ((Integer) row.get("value")) == 1),
                recursiveFunction, 5, false);
        assertTrue(result.size() > 5);
    }


    @Test
    public void testEmptyDataSet() {
        JQuickDataSet empty = JQuickDataSet.builder()
                .addColumn("id", Integer.class, "test")
                .build();
        Function<JQuickDataSet, JQuickDataSet> recursiveFunction = data -> data;
        JQuickDataSet result = JQuickSqlDataSetRecursiveQuery.withRecursive(
                empty, recursiveFunction, 5);
        assertTrue(result.isEmpty());
    }

    /**
     * 测试单层递归
     */
    @Test
    public void testSingleLevelRecursion() {
        JQuickDataSet data = createEmployeeDataSet();
        Function<JQuickDataSet, JQuickDataSet> findDirectSubordinates = current -> {
            Set<Integer> managerIds = current.getRows().stream()
                    .map(row -> (Integer) row.get("id"))
                    .collect(Collectors.toSet());
            List<JQuickRow> subordinates = data.getRows().stream()
                    .filter(row -> {
                        Integer managerId = (Integer) row.get("manager_id");
                        return managerId != null && managerIds.contains(managerId);
                    })
                    .collect(Collectors.toList());
            return new JQuickDataSet(data.getColumns(), subordinates);
        };
        JQuickDataSet ceo = data.filter(row -> row.get("manager_id") == null);
        JQuickDataSet directSubordinates = JQuickSqlDataSetRecursiveQuery.withRecursive(
                ceo, findDirectSubordinates, 2);
        assertEquals(3, directSubordinates.size());
        List<String> names = directSubordinates.getRows().stream()
                .map(row -> (String) row.get("name"))
                .sorted()
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), names);
    }
}
