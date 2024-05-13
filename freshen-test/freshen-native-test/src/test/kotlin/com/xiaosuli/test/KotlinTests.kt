package com.xiaosuli.test

import cn.xiaosuli.freshen.core.builder.QueryCondition.EmptyCondition.exists
import cn.xiaosuli.freshen.core.builder.QueryCondition.EmptyCondition.not
import cn.xiaosuli.freshen.core.builder.QueryConditionScope
import org.junit.jupiter.api.Test

class KotlinTests {

    class A<T> : QueryConditionScope<T>

    @Test
    fun test01() {
        with(A<Student2>()) {
            val condition = Student2::id.eq("1")
                .and(exists("select * from aaa"))
            log.info("===============")
            log.info("where sql: ${condition.toSql()}")
            log.info("===============")
        }
    }
}