package com.github.paohaijiao.xml.mock;

import com.github.paohaijiao.domain.JQuickTable;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.enums.DeptEnum;
import com.github.paohaijiao.xml.enums.LevelEnum;
import com.github.paohaijiao.xml.enums.TypeEnum;
import com.github.paohaijiao.xml.model.DataStatistics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DataServiceImpl {
    public String getTableName() {
        return "data_catalog_stat";
    }

    public JQuickTable getTable() {
        String tableName = getTableName();
        List<JQuickRow> rows=toRows();
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("dept_id", Long.class, tableName),
                new JQuickColumnMeta("dept_name", String.class, tableName),
                new JQuickColumnMeta("compiled_catalog_total", Integer.class, tableName),
                new JQuickColumnMeta("claimed_catalog_total", Integer.class, tableName),
                new JQuickColumnMeta("claim_ratio", String.class, tableName),
                new JQuickColumnMeta("completed_compilation", Integer.class, tableName),
                new JQuickColumnMeta("pending_compilation", Integer.class, tableName),
                new JQuickColumnMeta("compilation_ratio", String.class, tableName),
                new JQuickColumnMeta("compilation_progress", String.class, tableName),
                new JQuickColumnMeta("type", String.class, tableName),
                new JQuickColumnMeta("level", String.class, tableName),
                new JQuickColumnMeta("createdate", Date.class, tableName)
        );
        JQuickTable virtualTable = new JQuickTable(tableName, columns, rows);
        return virtualTable;
    }

    public List<DataStatistics> getData() {
        List<DataStatistics> list = new ArrayList<>(50);
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCE, 782, 753, "96.3%", 571, 182, "75.8%", "76%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2025-01-03"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_ECONOMY_AND_INFORMATION, 921, 861, "93.5%", 739, 122, "85.8%", "86%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2025-01-17"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_PUBLIC_SECURITY, 1116, 1063, "95.3%", 954, 109, "89.7%", "90%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2025-02-02"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_EDUCATION, 960, 928, "96.7%", 703, 225, "75.8%", "76%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2025-02-19"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_CIVIL_AFFAIRS, 744, 694, "93.3%", 641, 53, "92.4%", "92%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2025-03-06"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_HUMAN_RESOURCES_AND_SOCIAL_SECURITY, 638, 601, "94.2%", 477, 124, "79.4%", "79%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2025-03-21"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_NATURAL_RESOURCES, 812, 779, "95.9%", 650, 129, "83.4%", "83%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2025-04-05"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_HEALTH_COMMISSION, 576, 540, "93.8%", 431, 109, "79.8%", "80%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2025-04-20"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_TRANSPORT, 489, 458, "93.7%", 367, 91, "80.1%", "80%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2025-05-08"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_ECOLOGY_AND_ENVIRONMENT, 705, 671, "95.2%", 556, 115, "82.9%", "83%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2025-05-23"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_MARKET_REGULATION, 690, 660, "95.7%", 521, 139, "78.9%", "79%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2025-06-07"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CITY_CHENGDU, 835, 792, "94.9%", 668, 124, "84.3%", "84%", TypeEnum.THEME, LevelEnum.CITY, "2025-06-22"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_ECONOMY_AND_INFORMATION, 958, 921, "96.1%", 807, 114, "87.6%", "88%", TypeEnum.BASIC, LevelEnum.CITY, "2025-07-09"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_PUBLIC_SECURITY, 612, 580, "94.8%", 451, 129, "77.8%", "78%", TypeEnum.THEME, LevelEnum.CITY, "2025-07-24"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_EDUCATION, 543, 512, "94.3%", 438, 74, "85.5%", "86%", TypeEnum.BASIC, LevelEnum.CITY, "2025-08-08"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_CIVIL_AFFAIRS, 721, 689, "95.6%", 566, 123, "82.1%", "82%", TypeEnum.THEME, LevelEnum.CITY, "2025-08-23"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_HUMAN_RESOURCES_AND_SOCIAL_SECURITY, 889, 846, "95.2%", 712, 134, "84.2%", "84%", TypeEnum.BASIC, LevelEnum.CITY, "2025-09-07"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_NATURAL_RESOURCES, 508, 476, "93.7%", 392, 84, "82.4%", "82%", TypeEnum.THEME, LevelEnum.CITY, "2025-09-22"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_HEALTH_COMMISSION, 465, 431, "92.7%", 344, 87, "79.8%", "80%", TypeEnum.BASIC, LevelEnum.CITY, "2025-10-09"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_TRANSPORT, 602, 569, "94.5%", 466, 103, "81.9%", "82%", TypeEnum.THEME, LevelEnum.CITY, "2025-10-24"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_ECOLOGY_AND_ENVIRONMENT, 398, 371, "93.2%", 302, 69, "81.4%", "81%", TypeEnum.BASIC, LevelEnum.CITY, "2025-11-08"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_MARKET_REGULATION, 456, 428, "93.9%", 356, 72, "83.2%", "83%", TypeEnum.THEME, LevelEnum.CITY, "2025-11-23"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCE, 512, 486, "94.9%", 419, 67, "86.2%", "86%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2025-12-08"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_ECONOMY_AND_INFORMATION, 287, 260, "90.6%", 201, 59, "77.3%", "77%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2025-12-23"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_PUBLIC_SECURITY, 334, 309, "92.5%", 257, 52, "83.2%", "83%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-01-05"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_EDUCATION, 376, 349, "92.8%", 285, 64, "81.7%", "82%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2026-01-18"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_CIVIL_AFFAIRS, 421, 398, "94.5%", 331, 67, "83.2%", "83%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-02-01"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_HUMAN_RESOURCES_AND_SOCIAL_SECURITY, 265, 241, "90.9%", 193, 48, "80.1%", "80%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2026-02-14"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_NATURAL_RESOURCES, 243, 218, "89.7%", 174, 44, "79.8%", "80%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-02-27"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_HEALTH_COMMISSION, 312, 287, "92.0%", 234, 53, "81.5%", "82%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2026-03-12"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_TRANSPORT, 276, 251, "90.9%", 202, 49, "80.5%", "80%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-03-25"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_ECOLOGY_AND_ENVIRONMENT, 321, 298, "92.8%", 245, 53, "82.2%", "82%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2026-04-07"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_MARKET_REGULATION, 298, 276, "92.6%", 231, 45, "83.7%", "84%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-04-20"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CITY_CHENGDU, 187, 166, "88.8%", 129, 37, "77.7%", "78%", TypeEnum.THEME, LevelEnum.CITY, "2026-05-03"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_ECONOMY_AND_INFORMATION, 223, 204, "91.5%", 168, 36, "82.4%", "82%", TypeEnum.BASIC, LevelEnum.CITY, "2026-05-16"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_PUBLIC_SECURITY, 251, 228, "90.8%", 185, 43, "81.1%", "81%", TypeEnum.THEME, LevelEnum.CITY, "2026-05-29"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_EDUCATION, 269, 247, "91.8%", 203, 44, "82.2%", "82%", TypeEnum.BASIC, LevelEnum.CITY, "2026-06-11"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_CIVIL_AFFAIRS, 178, 158, "88.8%", 124, 34, "78.5%", "79%", TypeEnum.THEME, LevelEnum.CITY, "2026-06-24"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_HUMAN_RESOURCES_AND_SOCIAL_SECURITY, 156, 137, "87.8%", 109, 28, "79.6%", "80%", TypeEnum.BASIC, LevelEnum.CITY, "2026-07-07"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_NATURAL_RESOURCES, 204, 183, "89.7%", 149, 34, "81.4%", "81%", TypeEnum.THEME, LevelEnum.CITY, "2026-07-20"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_HEALTH_COMMISSION, 312, 286, "91.7%", 231, 55, "80.8%", "81%", TypeEnum.BASIC, LevelEnum.CITY, "2026-08-02"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_TRANSPORT, 367, 341, "92.9%", 282, 59, "82.7%", "83%", TypeEnum.THEME, LevelEnum.CITY, "2026-08-15"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_ECOLOGY_AND_ENVIRONMENT, 341, 317, "93.0%", 266, 51, "83.9%", "84%", TypeEnum.BASIC, LevelEnum.CITY, "2026-08-28"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.CHENGDU_MARKET_REGULATION, 214, 192, "89.7%", 151, 41, "78.6%", "79%", TypeEnum.THEME, LevelEnum.CITY, "2026-09-10"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCE, 256, 234, "91.4%", 194, 40, "82.9%", "83%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-09-23"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_ECONOMY_AND_INFORMATION, 289, 265, "91.7%", 216, 49, "81.5%", "82%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2026-10-06"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_PUBLIC_SECURITY, 308, 285, "92.5%", 236, 49, "82.8%", "83%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-10-19"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_EDUCATION, 201, 181, "90.0%", 143, 38, "79.0%", "79%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2026-11-01"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_CIVIL_AFFAIRS, 178, 158, "88.8%", 124, 34, "78.5%", "79%", TypeEnum.BASIC, LevelEnum.PROVINCIAL, "2026-11-14"));
        list.add(mockOneDeptDirectoryAnalyzeData(DeptEnum.PROVINCIAL_HUMAN_RESOURCES_AND_SOCIAL_SECURITY, 233, 211, "90.6%", 173, 38, "82.0%", "82%", TypeEnum.THEME, LevelEnum.PROVINCIAL, "2026-11-27"));
        return list;
    }

    public List<JQuickRow> toRows() {
        List<DataStatistics> data=getData();
        List<JQuickRow>  rows=JQuickRow.toRows(data);
        return  rows;
    }

    private static DataStatistics mockOneDeptDirectoryAnalyzeData(DeptEnum dept, Integer compiledCatalogTotal, Integer claimedCatalogTotal, String claimRatio, Integer completedCompilation, Integer pendingCompilation, String compilationRatio, String compilationProgress, TypeEnum type, LevelEnum level, String createDate) {
        DataStatistics d = new DataStatistics();
        d.setDeptId(dept.getId());
        d.setDeptName(dept.getName());
        d.setCompiledCatalogTotal(compiledCatalogTotal);
        d.setClaimedCatalogTotal(claimedCatalogTotal);
        d.setClaimRatio(claimRatio);
        d.setCompletedCompilation(completedCompilation);
        d.setPendingCompilation(pendingCompilation);
        d.setCompilationRatio(compilationRatio);
        d.setCompilationProgress(compilationProgress);
        d.setType(type.getCode());
        d.setLevel(level.getCode());
        d.setCreateDate(Date.from(LocalDate.parse(createDate).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return d;
    }
}
