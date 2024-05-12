package com.xiaosuli.test

import cn.xiaosuli.freshen.core.builder.QueryConditionScope
import org.junit.jupiter.api.Test

class KotlinTests {

    class A<T> : QueryConditionScope<T>

    @Test
    fun test01() {
        with(A<Student2>()) {
            val condition = Student2::id.eq("1")
                .or(Student2::name.eq("%张三%"))
                .and(Student2::address.eq("色的"))
                .or(Student2::address.eq("色的"))
            println("===============")
            println("where sql: ${condition.toSql()}")
            println("===============")
        }
    }
}