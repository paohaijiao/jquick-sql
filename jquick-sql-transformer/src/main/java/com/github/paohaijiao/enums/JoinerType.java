package com.github.paohaijiao.enums;

import jdk.nashorn.internal.objects.annotations.Getter;

@Getter
public enum JoinerType {
    SEQUENTIAL,
    MULTI_THREADED,
    LAMBDA,
    SPARK,
    MAP_REDUCE,
    FLINK,
    FORK_JOIN
}
