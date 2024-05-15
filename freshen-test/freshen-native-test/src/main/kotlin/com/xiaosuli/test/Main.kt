package com.xiaosuli.test

import cn.xiaosuli.freshen.core.crud.paginate
import cn.xiaosuli.freshen.core.crud.query
import cn.xiaosuli.freshen.core.crud.queryAs
import cn.xiaosuli.freshen.core.crud.queryOne
import cn.xiaosuli.freshen.core.entity.Page
import cn.xiaosuli.freshen.core.runFreshen
import com.alibaba.druid.pool.DruidDataSource
import javax.sql.DataSource

fun main() {
    runFreshen(getDruidDataSource())
    queryAs<Student,Student>{
        where{
            Student::name like "%xiaosuli"
        }
    }
}

private fun getDruidDataSource(): DataSource = DruidDataSource().apply {
    driverClassName = "com.mysql.cj.jdbc.Driver"
    url = "jdbc:mysql://localhost:3306/db_freshen?serverTimezone=Asia/Shanghai"
    username = "root"
    password = "123456"
}