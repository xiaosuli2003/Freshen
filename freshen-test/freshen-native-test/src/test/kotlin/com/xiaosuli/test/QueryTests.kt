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
        query<Student2> {
            // select 子句开发完毕
            // select(Student2::id, Student2::name)
            // select("id", "name", "birthday")
            // select(Student2::id.column, "birthday")
            // select(Student2::class.all)
            // select多表才会用到以下重载
            // select(Student2::class.all,Student2::class.all)
            // select(Student2::class.all, Student2::birthday)
            // select(Student2::class.columns, "birthday")

            // from 子句开发完毕
            // from<Student2>()
            // from(Student2::class)
            // from("tb_student")

            // where 方式1
            // where(Student2::id.eq("3") and Student2::name.eq("xiaosuli"))
            // 方式2
            // where {
            //     and {
            //         Student2::id.eq("1") or Student2::address.eq("1")
            //     }.or {
            //         Student2::id.eq("1") or Student2::address.eq("1")
            //     }
            // }

            // group by 子句开发完毕
            // groupBy(Student2::id, Student2::birthday)

            // having 方式1
            having(Student2::id.eq("3") and Student2::name.eq("xiaosuli"))
            // having 方式2
            having {
                and {
                    Student2::id.eq("1") or Student2::address.eq("1")
                }.or {
                    Student2::id.eq("1") or Student2::address.eq("1")
                }
            }

            // order by 子句开发完毕
            // orderBy(Student2::id.desc, Student2::birthday.asc)

            // limit 子句开发完毕
            // limit(1)
            // limit(1, 1)
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