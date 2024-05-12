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
import cn.xiaosuli.freshen.core.anno.Column
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.anno.Id
import cn.xiaosuli.freshen.core.anno.Table
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.entity.SQLWithParams
import cn.xiaosuli.freshen.core.utils.toUnderscore
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * 查询语句构造器（适用于单表查询）
 * 用于query，queryOne,queryAsync,queryOneAsync
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
     * SQL占位参数列表
     *
     * TODO: "要实现这里的逻辑"
     */
    override val params: List<PrepareStatementParam>
        get() = emptyList()

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
            column.forEach { append("${it.toUnderscore()},") }
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
     * 设置要查询的列
     *
     * @param columns 列名
     */
    fun select(columns: List<KProperty1<T, *>>) {
        select = buildString {
            append("select ")
            columns.forEach { column ->
                val value = column.annotations.filterIsInstance<Id>().firstOrNull()?.value
                    ?: column.annotations.filterIsInstance<Column>().firstOrNull()?.value
                    ?: column.name.toUnderscore()
                append("${value},")
            }
        }.dropLast(1)
    }

    /**
     * 属性转字段（列）
     *
     * @return 所有字段集合
     */
    val KProperty1<T, *>.column: String
        get() = name.toUnderscore()

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
        val table = kClass.annotations.filterIsInstance<Table>().firstOrNull()?.value
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
        val table = T::class.annotations.filterIsInstance<Table>().firstOrNull()?.value
            ?: FreshenRuntimeConfig.tablePrefix?.let {
                "$it${T::class.simpleName!!.toUnderscore()}"
            }
            ?: T::class.simpleName!!.toUnderscore()
        from = "from $table"
    }

    // where ==================================================

    /**
     * 构建where子句
     * TODO: 这两个where需要改为？占位形式
     *
     * @param queryCondition 查询条件
     */
    fun where(queryCondition: QueryCondition) {
        where = "where ${queryCondition.toSql()}"
    }

    /**
     * 构建where子句
     *  TODO: 这两个where需要改为？占位形式
     *
     * @param action 查询lambda
     */
    fun where(action: QueryCondition.() -> QueryCondition) {
        val queryCondition = QueryCondition.EmptyCondition
        where = "where ${queryCondition.action().toSql()}"
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
                append(it.name.toUnderscore())
                append(",")
            }
        }.dropLast(1)
    }

    // having ==================================================

    fun having() {
        having = "having"
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
        get() = OrderByCondition("${name.toUnderscore()} asc")

    /**
     * 设置降序
     */
    val KProperty1<*, *>.desc: OrderByCondition
        get() = OrderByCondition("${name.toUnderscore()} desc")

    // limit ==================================================

    /**
     * 设置limit
     * TODO: 这两个limit需要改为？占位形式
     *
     * @param pageSize 每页行数
     * @param pageNum 页码
     */
    fun limit(pageSize: Int, pageNum: Int) {
        val offset = (pageNum - 1) * pageSize
        limit = "limit $offset, $pageSize"
    }

    /**
     * 设置limit
     * TODO: 这两个limit需要改为？占位形式
     *
     * @param row 行数
     */
    fun limit(row: Int) {
        limit = "limit $row"
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
        return SQLWithParams(sql, params)
    }
}