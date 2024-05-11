package com.xiaosuli.test

import cn.xiaosuli.freshen.core.crud.query
import cn.xiaosuli.freshen.core.entity.FreshenConfig
import cn.xiaosuli.freshen.core.runFreshen
import com.alibaba.druid.pool.DruidDataSource
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.sql.JDBCType
import javax.sql.DataSource
import kotlin.reflect.KProperty1

class QueryTests {

    @Test
    fun test02() {
        query<Student> {
            // select子句完毕
            // select(Student::class.full())
            // select(Student::id, Student::name)
            // select("id","name")

            // from子句完毕
            // from<Student>()
            // from(Student::class)
            // from("tb_student")

            // order by子句完毕
            // orderBy(Student::id).desc()
            // orderBy(Student::id).asc()

            // limit子句完毕
            // limit(1)
            // limit(1,1)
        }.forEach(::println)
    }

    @Test
    fun test01() {
        // 查询多条记录
        val map: Map<KProperty1<*, *>, JDBCType> = mapOf(
            Student2::id to JDBCType.BIGINT,
            Student2::name to JDBCType.VARCHAR,
            Student2::gender to JDBCType.VARCHAR,
            Student2::birthday to JDBCType.TIMESTAMP,
            Student2::phoneNumber to JDBCType.VARCHAR,
            Student2::address to JDBCType.VARCHAR
        )
        val accountList = query<Student2>(map) {}
        accountList.forEach(::println)
        println(if (accountList.isEmpty()) "没有查到值" else "查到值了")
        // 查询一条记录
        // println(queryOne<Student2>() ?: "没有查到值")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            // 启动 Freshen
            runFreshen {
                FreshenConfig(
                    dataSource = getDruidDataSource(),
                    tablePrefix = "tb_",
                    sqlAudit1 = { sql, params ->
                        log.debug("sql: $sql")
                        params.forEach {
                            log.debug("param ---> index: ${it.index}; type: ${it.type}, value: ${it.value}")
                        }
                    },
                    sqlAudit2 = { sql, params, elapsedTime ->
                        log.debug("sql: $sql, 耗时: ${elapsedTime}ms")
                        params?.forEach {
                            log.debug("param ---> index: ${it.index}; type: ${it.type}, value: ${it.value}")
                        }
                    }
                )
            }
        }

        private fun getDruidDataSource(): DataSource = DruidDataSource().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            url = "jdbc:mysql://localhost:3306/db_freshen?serverTimezone=Asia/Shanghai"
            username = "root"
            password = "123456"
        }
    }
}