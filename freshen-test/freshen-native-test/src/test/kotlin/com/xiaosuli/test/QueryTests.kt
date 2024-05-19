package com.xiaosuli.test

import cn.xiaosuli.freshen.core.crud.query
import cn.xiaosuli.freshen.core.runFreshen
import com.alibaba.druid.pool.DruidDataSource
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class QueryTests {

    @Test
    fun testQuery() {
        query<Student> {
            select(Student::class.all)
        }.forEach(::println)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            runFreshen(getDruidDataSource())
        }

        private fun getDruidDataSource(): DataSource = DruidDataSource().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            url = "jdbc:mysql://localhost:3306/db_freshen?serverTimezone=Asia/Shanghai"
            username = "root"
            password = "123456"
        }
    }
}