package com.github.paohaijiao.enums;

public enum JQuickSubqueryType {
    SCALAR,      // 标量子查询 (SELECT ...) 返回单行单列
    EXISTS,      // EXISTS 子查询
    NOT_EXISTS,  // NOT EXISTS 子查询
    IN,          // IN 子查询
    NOT_IN,      // NOT IN 子查询
    ANY,         // ANY 子查询 (SOME)
    ALL          // ALL 子查询
}
