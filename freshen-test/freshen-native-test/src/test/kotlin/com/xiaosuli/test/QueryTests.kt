package com.xiaosuli.test

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FlexIdIsExperimentalApi
import cn.xiaosuli.freshen.core.anno.Id
import cn.xiaosuli.freshen.core.anno.SnowflakeIdIsExperimentalApi
import cn.xiaosuli.freshen.core.builder.QueryConditionScope
import cn.xiaosuli.freshen.core.crud.insert
import cn.xiaosuli.freshen.core.crud.insertBatch
import cn.xiaosuli.freshen.core.crud.paginate
import cn.xiaosuli.freshen.core.crud.query
import cn.xiaosuli.freshen.core.entity.FreshenConfig
import cn.xiaosuli.freshen.core.entity.KeyGenerator
import cn.xiaosuli.freshen.core.runFreshen
import com.alibaba.druid.pool.DruidDataSource
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.sql.JDBCType
import java.time.LocalDateTime
import javax.sql.DataSource
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class QueryTests {

    @Test
    fun testInsertBatch() {
        val list: MutableList<Student> = mutableListOf()
        repeat(100) {
            list.add(
                Student().apply {
                    name = "小苏苏$it"
                    gender = "男"
                    birthday = LocalDateTime.now()
                    phoneNumber = "13${it}4935${it}"
                    address = "陕西西安${it}"
                }
            )
        }
        val rows = insertBatch(list)
        log.info("影响行数：$rows")
    }

    @Test
    fun testInsert() {
        val stu = Student().apply {
            id = 1234564565435643
            name = "小苏苏"
            gender = "男"
            birthday = LocalDateTime.now()
            phoneNumber = "13134567"
            address = "陕西西安}"
        }
        val rows = insert(stu)
        log.info("影响行数：$rows")
    }

    @Test
    fun testPage() {
        paginate<Student2>(1, 3).records.forEach { log.info(it.toString()) }
    }

    @Test
    fun test04() {
        val sql = """select * from tb_student 
            where  (id = ? or address = ?) 
            or (id = ? or id in (?))    
            limit ?"""
        val connection = FreshenRuntimeConfig.dataSource.connection
        val statement = connection.prepareStatement(sql)
        statement.setString(1, "1")
        statement.setString(2, "2")
        statement.setString(3, "3")
        statement.setString(4, "4")
        statement.setInt(5, 5)
    }

    class A<T> : QueryConditionScope<T>

    @Test
    fun test03() {
        val a = A<Student2>()
        with(a) {
            val condition = Student2::id.eq("2").and {
                Student2::id.eq("3") or Student2::address.eq("4")
            }.or {
                Student2::id.eq("5") or Student2::id.`in`(6)
            }
            condition.queryParams.forEach { log.info(it.toString()) }
        }
    }

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
            where(Student2::id eq 1)
            // where 方式2
            // where {
            //     and {
            //         Student2::id.eq(3) or Student2::address.eq("4")
            //     }.or {
            //         Student2::id.eq(5) or Student2::id.`in`(6)
            //     }
            // }

            // group by 子句开发完毕
            // groupBy(Student2::id, Student2::birthday)

            // having 方式1
            // having(Student2::id.eq("7") and Student2::name.eq("8"))
            // // having 方式2
            // having {
            //     and {
            //         Student2::id.eq("9") or Student2::address.eq("10")
            //     }.or {
            //         Student2::id.eq("11") or Student2::address.eq("12")
            //     }
            // }

            // order by 子句开发完毕
            // orderBy(Student2::id.desc, Student2::birthday.asc)

            // limit 子句开发完毕
            limit(13)
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
        @OptIn(SnowflakeIdIsExperimentalApi::class, FlexIdIsExperimentalApi::class)
        @JvmStatic
        @BeforeAll
        fun init() {
            // 启动 Freshen
            runFreshen(getDruidDataSource())
            // 启动方式二
            runFreshen(
                FreshenConfig(dataSource = getDruidDataSource(),
                    tablePrefix = "tb_",
                    keyGenerator = KeyGenerator.NONE,
                    sqlAudit1 = { sql, params ->
                        log.debug("sqlAudit1 ---> sql: $sql")
                        // params.forEachIndexed { index, param ->
                        //     log.debug("sqlAudit1 ---> param ---> index: {}; value: {}", index + 1, param.value)
                        // }
                    },
                    sqlAudit2 = { sql, params, elapsedTime ->
                        //log.debug("sqlAudit2 ---> sql: $sql, 耗时: ${elapsedTime}ms")
                        // params.forEachIndexed { index, param ->
                        //     log.debug("sqlAudit2 ---> param ---> index: {}; value: {}", index + 1, param.value)
                        // }
                    })
            )
        }

        private fun getDruidDataSource(): DataSource = DruidDataSource().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            url = "jdbc:mysql://localhost:3306/db_freshen?serverTimezone=Asia/Shanghai"
            username = "root"
            password = "123456"
        }
    }
}