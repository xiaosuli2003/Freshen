package com.xiaosuli.test

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FlexIdIsExperimentalApi
import cn.xiaosuli.freshen.core.anno.SnowflakeIdIsExperimentalApi
import cn.xiaosuli.freshen.core.builder.QueryConditionScope
import cn.xiaosuli.freshen.core.crud.*
import cn.xiaosuli.freshen.core.entity.FreshenConfig
import cn.xiaosuli.freshen.core.entity.KeyGenerator
import cn.xiaosuli.freshen.core.entity.LogicDelete
import cn.xiaosuli.freshen.core.runFreshen
import com.alibaba.druid.pool.DruidDataSource
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.sql.JDBCType
import java.time.LocalDateTime
import javax.sql.DataSource
import kotlin.reflect.KProperty1

class QueryTests {

    @Test
    fun testUpdate() {
        val student = Student().apply {
            gender = "女"
        }
        val rows = update<Student>(student){
            where(Student::name.eq("官人"))
        }
        if (rows > 0) {
            log.info("修改成功!")
        } else {
            log.info("修改失败!")
        }
    }

    @Test
    fun testDelete() {
        val rows = delete<Student>{
            where(Student::name like "%小苏苏%")
        }
        if (rows > 0) {
            log.info("删除成功!")
        } else {
            log.info("删除失败!")
        }
    }

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
    fun test01() {

        val accountList = query<Student>{}
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
                    logicDelete = LogicDelete.Enable("del_flag",0,1),
                    sqlAudit1 = { sql, params ->
                        log.debug("sqlAudit1 ---> sql: $sql")
                        params.forEachIndexed { index, param ->
                            log.debug("sqlAudit1 ---> param ---> index: {}; value: {}", index + 1, param.value)
                        }
                    },
                    sqlAudit2 = { sql, params, elapsedTime ->
                        // log.debug("sqlAudit2 ---> sql: $sql, 耗时: ${elapsedTime}ms")
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