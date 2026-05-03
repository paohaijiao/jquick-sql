package com.github.paohaijiao.client;

public enum ClientType {

    SPARK("spark", "Apache Spark 分布式计算客户端"),

    FLINK("flink", "Apache Flink 流式计算客户端"),

    FORK_JOIN("forkjoin", "ForkJoin 并行计算客户端"),

    LOCAL("local", "本地单线程客户端"),

    LOCAL_N("local_n", "本地多线程客户端"),

    MAPREDUCE("mapreduce", "MapReduce 客户端"),

    AUTO("auto", "自动选择客户端");

    private final String type;
    private final String description;

    ClientType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static ClientType fromType(String type) {
        for (ClientType ct : values()) {
            if (ct.type.equalsIgnoreCase(type)) {
                return ct;
            }
        }
        return AUTO;
    }

    @Override
    public String toString() {
        return type;
    }
}
