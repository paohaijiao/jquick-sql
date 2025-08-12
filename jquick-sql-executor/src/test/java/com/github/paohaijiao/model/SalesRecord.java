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

import java.time.LocalDate;
import java.util.Objects;

/**
 * packageName com.github.paohaijiao.model
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class SalesRecord {
    private String recordId;       // 记录ID
    private LocalDate saleDate;    // 销售日期
    private String region;        // 地区
    private String country;       // 国家
    private String city;          // 城市
    private String storeId;       // 店铺ID
    private String productId;     // 产品ID
    private String productCategory; // 产品类别
    private String customerSegment; // 客户细分
    private String quarter;       // 季度(Q1,Q2,Q3,Q4)
    private String month;         // 月份
    private double salesAmount;    // 销售额
    private int quantity;         // 销售数量
    private double profit;        // 利润
    private double cost;          // 成本
    private Integer rollupLevel;   // ROLLUP级别(0=总计,1=地区级,2=国家级等)
    private Integer drillLevel;    // 下钻级别(1=汇总,2=细节)
    private boolean isSubtotal;    // 是否小计行
    private boolean isTotal;       // 是否总计行

    public SalesRecord() {
    }

    public SalesRecord(String recordId, LocalDate saleDate, String region,
                       String country, String city, String storeId,
                       String productId, String productCategory,
                       String customerSegment, double salesAmount,
                       int quantity, double profit, double cost) {
        this.recordId = recordId;
        this.saleDate = saleDate;
        this.region = region;
        this.country = country;
        this.city = city;
        this.storeId = storeId;
        this.productId = productId;
        this.productCategory = productCategory;
        this.customerSegment = customerSegment;
        this.salesAmount = salesAmount;
        this.quantity = quantity;
        this.profit = profit;
        this.cost = cost;

        this.quarter = "Q" + ((saleDate.getMonthValue() - 1) / 3 + 1);
        this.month = String.format("%02d", saleDate.getMonthValue());
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
        if (saleDate != null) {
            this.quarter = "Q" + ((saleDate.getMonthValue() - 1) / 3 + 1);
            this.month = String.format("%02d", saleDate.getMonthValue());
        }
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getRollupLevel() {
        return rollupLevel;
    }

    public void setRollupLevel(Integer rollupLevel) {
        this.rollupLevel = rollupLevel;
        this.isSubtotal = rollupLevel != null && rollupLevel > 0;
        this.isTotal = rollupLevel != null && rollupLevel == 0;
    }

    public Integer getDrillLevel() {
        return drillLevel;
    }

    public void setDrillLevel(Integer drillLevel) {
        this.drillLevel = drillLevel;
    }

    public boolean isSubtotal() {
        return isSubtotal;
    }

    public boolean isTotal() {
        return isTotal;
    }
    /**
     * 计算毛利率
     */
    public double getGrossMargin() {
        return salesAmount > 0 ? (profit / salesAmount) * 100 : 0;
    }

    /**
     * 计算单价
     */
    public double getUnitPrice() {
        return quantity > 0 ? salesAmount / quantity : 0;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesRecord that = (SalesRecord) o;
        return Objects.equals(recordId, that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }

    @Override
    public String toString() {
        return "SalesRecord{" +
                "recordId='" + recordId + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", salesAmount=" + salesAmount +
                (isSubtotal ? ", SUBTOTAL" : "") +
                (isTotal ? ", GRAND TOTAL" : "") +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String recordId;
        private LocalDate saleDate;
        private String region;
        private String country;
        private double salesAmount;

        private Builder() {
        }

        public Builder recordId(String recordId) {
            this.recordId = recordId;
            return this;
        }

        public Builder saleDate(LocalDate saleDate) {
            this.saleDate = saleDate;
            return this;
        }


        public SalesRecord build() {
            return new SalesRecord(
                    recordId, saleDate, region, country,
                    null, null, null, null, null,
                    salesAmount, 0, 0, 0
            );
        }
    }
}
