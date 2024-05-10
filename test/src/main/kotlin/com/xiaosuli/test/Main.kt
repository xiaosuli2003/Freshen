package com.xiaosuli.test

import cn.xiaosuli.freshen.core.crud.query
import cn.xiaosuli.freshen.core.entity.FreshenConfig
import cn.xiaosuli.freshen.core.runFreshen
import com.alibaba.druid.pool.DruidDataSource
import java.time.LocalDateTime
import javax.sql.DataSource

class Student {
    var id: Long? = null
    var name: String? = null
    var gender: String? = null
    var birthday: LocalDateTime? = null
    var phoneNumber: String? = null
    var address: String? = null
    override fun toString(): String {
        return "Student(id=$id, name=$name, gender=$gender, birthday=$birthday, phoneNumber=$phoneNumber, address=$address)"
    }
}

fun main() {
    // 启动 Freshen
    runFreshen {
        FreshenConfig(
            dataSource = getDruidDataSource(),
            tablePrefix = "tb_",
            /*sqlAudit1 = { sql,params ->
                log.debug("sql: $sql")
                params.forEach {
                    log.debug("param ---> index: ${it.index}; type: ${it.type}, value: ${it.value}")
                }
            },*/
            sqlAudit2 = { sql, params,elapsedTime ->
                log.debug("sql: $sql, 耗时: ${elapsedTime}ms")
                params?.forEach {
                    log.debug("param ---> index: ${it.index}; type: ${it.type}, value: ${it.value}")
                }
            }
        )
    }
    // 查询列表
    val accountList = query<Student>()
    accountList.forEach(::println)
}

fun getDruidDataSource(): DataSource = DruidDataSource().apply {
    driverClassName = "com.mysql.cj.jdbc.Driver"
    url = "jdbc:mysql://localhost:3306/db_freshen?serverTimezone=Asia/Shanghai"
    username = "root"
    password = "123456"
}