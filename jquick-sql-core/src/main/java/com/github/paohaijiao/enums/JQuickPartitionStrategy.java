package com.github.paohaijiao.enums;

public enum JQuickPartitionStrategy {
    HASH,           // 哈希分区
    RANGE,          // 范围分区
    ROUND_ROBIN,    // 轮询分区
    BUCKET,         // 桶分区
    REPLICATE       // 复制（广播）
}
