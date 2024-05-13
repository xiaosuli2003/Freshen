/*
 * Copyright 2024 是晓酥梨呀(2060988267@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.xiaosuli.freshen.core.builder

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.anno.Table
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.entity.SQLWithParams
import cn.xiaosuli.freshen.core.utils.column
import cn.xiaosuli.freshen.core.utils.toUnderscore
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * 查询语句构造器（适用于单表查询）
 * 适用于query，queryOne,queryFlow
 *
 * @param T 表对应的实体类
 */
@FreshenInternalApi
open class QueryBuilder<T : Any> : QueryConditionScope<T>, QueryMethodScope<T>, SQLBuilder {
    /**
     * select [ column1, column2, ··· ]
     */
    var select = "select *"

    /**
     * from 表名
     */
    var from = ""

    /**
     * where condition
     */
    var where = ""

    /**
     * group by column
     */
    var groupBy = ""

    /**
     * having condition
     */
    var having = ""

    /**
     * order by column asc/desc
     */
    var orderBy = ""

    /**
     * limit 1 / limit 1, 1 / limit 1 offset 1
     */
    var limit = ""

    /**
     * where占位参数列表
     */
    private var whereParams: Array<PrepareStatementParam> = emptyArray()

    /**
     * having占位参数列表
     */
    private var havingParams: Array<PrepareStatementParam> = emptyArray()

    /**
     * limit占位参数列表
     */
    private var limitParams: Array<PrepareStatementParam> = emptyArray()

    // select ==================================================

    /**
     * 设置要查询的列
     * * 建议直接硬编码列名，不要通过外面传入，否则可能会导致SQL注入问题
     * * 建议使用select(vararg columns: KProperty1<T,*>)这样的方法
     *
     * @param column 列名
     */
    fun select(vararg column: String) {
        select = buildString {
            append("select ")
            column.forEach { append("${it},") }
        }.dropLast(1)
    }

    /**
     * 设置要查询的列，并去重
     * * 建议直接硬编码列名，不要通过外面传入，否则可能会导致SQL注入问题
     * * 建议使用selectDistinct(vararg columns: KProperty1<T,*>)这样的方法
     *
     * @param column 列名
     */
    fun selectDistinct(vararg column: String) {
        select = buildString {
            append("select distinct")
            column.forEach { append("${it},") }
        }.dropLast(1)
    }

    /**
     * 设置要查询的列
     *
     * @param columns 列名
     */
    fun select(vararg columns: KProperty1<T, *>) {
        select(columns.toList())
    }

    /**
     * 设置要查询的列，并去重
     *
     * @param columns 列名
     */
    fun selectDistinct(vararg columns: KProperty1<T, *>) {
        selectDistinct(columns.toList())
    }

    /**
     * 设置要查询的列
     *
     * @param columns 列名
     */
    fun select(columns: List<KProperty1<T, *>>) {
        select = buildString {
            append("select ")
            columns.forEach { append("${it.column},") }
        }.dropLast(1)
    }

    /**
     * 设置要查询的列，并去重
     *
     * @param columns 列名
     */
    fun selectDistinct(columns: List<KProperty1<T, *>>) {
        select = buildString {
            append("select distinct")
            columns.forEach { append("${it.column},") }
        }.dropLast(1)
    }

    /**
     * 查询全部字段
     *
     * @return 所有字段集合
     */
    val KClass<T>.all: List<KProperty1<T, *>>
        get() = memberProperties.toList()

    // from ==================================================

    /**
     * 设置要查询的表
     * * 请直接硬编码表名，不要通过外面传入，否则可能会导致SQL注入问题
     * * 建议使用from<T>()和from(KClass)方法
     *
     * @param table 表名
     */
    fun from(table: String) {
        from = "from $table"
    }

    /**
     * 设置要查询的表
     *
     * @param kClass 表对应的实体类引用
     */
    fun from(kClass: KClass<T>) {
        // 获取表名，如果有注解，则使用注解的值，否则走统一前缀
        // 如果设置统一前缀，则标名为统一前缀+类名，否则直接使用类名
        val table = kClass.findAnnotation<Table>()?.value
            ?: FreshenRuntimeConfig.tablePrefix?.let {
                "$it${kClass.simpleName!!.toUnderscore()}"
            }
            ?: kClass.simpleName!!.toUnderscore()
        from = "from $table"
    }

    /**
     * 设置要查询的表
     *
     * @param T 表对应的实体类引用
     */
    inline fun <reified T : Any> from() {
        // 获取表名，如果有注解，则使用注解的值，否则走统一前缀
        // 如果设置统一前缀，则标名为统一前缀+类名，否则直接使用类名
        val table = T::class.findAnnotation<Table>()?.value
            ?: FreshenRuntimeConfig.tablePrefix?.let {
                "$it${T::class.simpleName!!.toUnderscore()}"
            }
            ?: T::class.simpleName!!.toUnderscore()
        from = "from $table"
    }

    // where ==================================================

    /**
     * 构建where子句
     *
     * @param queryCondition 查询条件
     */
    fun where(queryCondition: QueryCondition) {
        where = "where ${queryCondition.toSql()}"
        whereParams = queryCondition.queryParams
    }

    /**
     * 构建where子句
     *
     * @param action 查询lambda
     */
    fun where(action: QueryCondition.() -> QueryCondition) {
        val emptyCondition = QueryCondition.EmptyCondition
        val queryCondition = emptyCondition.action()
        where = "where ${queryCondition.toSql()}"
        whereParams = queryCondition.queryParams
    }

    // group by ==================================================

    /**
     * 设置要分组的列
     *
     * @param columns 要分组的列
     */
    fun groupBy(vararg columns: KProperty1<T, *>) {
        groupBy = buildString {
            append("group by ")
            columns.forEach {
                append(it.column)
                append(",")
            }
        }.dropLast(1)
    }

    // having ==================================================

    /**
     * 构建having子句
     *
     * @param queryCondition 查询条件
     */
    fun having(queryCondition: QueryCondition) {
        having = "having ${queryCondition.toSql()}"
        havingParams = queryCondition.queryParams
    }

    /**
     * 构建having子句
     *
     * @param action 查询lambda
     */
    fun having(action: QueryCondition.() -> QueryCondition) {
        val emptyCondition = QueryCondition.EmptyCondition
        val queryCondition = emptyCondition.action()
        having = "having ${queryCondition.toSql()}"
        havingParams = queryCondition.queryParams
    }

    // order by ==================================================

    /**
     * 设置要排序的列和条件
     *
     * @param orderBys 要排序的列和条件
     */
    fun orderBy(vararg orderBys: OrderByCondition) {
        orderBy = buildString {
            append("order by ")
            orderBys.forEach {
                append(it.orderBy)
                append(", ")
            }
        }.dropLast(2)
    }

    /**
     * 保存排序条件的实体
     *
     * @param orderBy 要排序的列和条件
     */
    class OrderByCondition(val orderBy: String)

    /**
     * 设置升序
     */
    val KProperty1<*, *>.asc: OrderByCondition
        get() = OrderByCondition("$column asc")

    /**
     * 设置降序
     */
    val KProperty1<*, *>.desc: OrderByCondition
        get() = OrderByCondition("$column desc")

    // limit ==================================================

    /**
     * 设置limit
     *
     * @param pageSize 每页行数
     * @param pageNum 页码
     */
    fun limit(pageSize: Int, pageNum: Int) {
        val offset = (pageNum - 1) * pageSize
        limit = "limit ?, ?"
        limitParams = arrayOf(
            PrepareStatementParam(offset::class, offset),
            PrepareStatementParam(pageSize::class, pageSize)
        )
    }

    /**
     * 设置limit
     *
     * @param row 行数
     */
    fun limit(row: Int) {
        limit = "limit ?"
        limitParams = arrayOf(PrepareStatementParam(Int::class, row))
    }

    // sql build ==================================================

    /**
     * 拼接SQL
     *
     * @return SQL
     */
    override fun build(): SQLWithParams {
        val sql = buildString {
            append(select)
            append(" ")
            append(from)
            append(" ")
            append(where)
            append(" ")
            append(groupBy)
            append(" ")
            append(having)
            append(" ")
            append(orderBy)
            append(" ")
            append(limit)
        }
        val params = arrayOf(*whereParams, *havingParams, *limitParams)
        return SQLWithParams(sql, params)
    }
}