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

package cn.xiaosuli.freshen.core.crud

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.builder.QueryAsBuilder
import cn.xiaosuli.freshen.core.builder.QueryBuilder
import cn.xiaosuli.freshen.core.entity.Page
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.utils.closeAndAudit
import cn.xiaosuli.freshen.core.utils.getObject
import cn.xiaosuli.freshen.core.utils.setParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.sql.Connection
import java.sql.JDBCType
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.math.ceil
import kotlin.reflect.KProperty1

/**
 * 查询多条记录
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return List<T>
 */
inline fun <reified T : Any> query(
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): List<T> = queryAs<T, T>(typeMap, init)

/**
 * 查询多条记录
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return Flow<T>
 */
inline fun <reified T : Any> queryFlow(
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Flow<T> = queryFlowAs<T, T>(typeMap, init)

/**
 * 查询一条记录
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return List<T>
 */
inline fun <reified T : Any> queryOne(
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): T? {
    val start = System.currentTimeMillis()
    val queryBuilder = QueryBuilder<T>()
    queryBuilder.from(T::class)
    init?.invoke(queryBuilder)
    // 无论用户设置limit为何值，这里强制设置为1
    queryBuilder.limit(1)
    val (sql, params) = queryBuilder.build()
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    return executeQueryAndResult<T>(sql, params, typeMap, start)
}

/**
 * 查询多条记录
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any, reified R : Any> queryAs(
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryAsBuilder<T>.() -> Unit)? = null
): List<R> {
    val (sql, params, start) = buildQueryAsSQL<T>(init)
    return executeQueryAndResultList<R>(sql, params, typeMap, start)
}

/**
 * 查询多条记录
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return Flow<R>
 */
inline fun <reified T : Any, reified R : Any> queryFlowAs(
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryAsBuilder<T>.() -> Unit)? = null
): Flow<R> {
    val (sql, params, start) = buildQueryAsSQL<T>(init)
    return executeQueryAndResultFlow<R>(sql, params, typeMap, start)
}

/**
 * 查询一个值
 * * 该方法只适用于查询一列，且值不为null，否则JDBC会抛异常
 * * 例如：`select count(*) from table`
 *
 * @param column 列名
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any> queryValue(
    column: String,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Any {
    val (sql, params, start) = buildQuerySQL<T>(init)
    val (connection, statement, resultSet) = executeQuery(sql, params)
    // TODO: 未检查next()的返回值，这里可能会抛异常
    resultSet.next()
    val value = resultSet.getObject(column)
    connection.closeAndAudit(statement, resultSet, sql, params, start)
    return value
}

/**
 * 查询一个值
 *
 * @param column 列名
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any> queryValueOrNull(
    column: String,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Any? {
    val (sql, params, start) = buildQuerySQL<T>(init)
    val (connection, statement, resultSet) = executeQuery(sql, params)
    val value = if (resultSet.next()) {
        resultSet.getObject(column)
    } else null
    connection.closeAndAudit(statement, resultSet, sql, params, start)
    return value
}

/**
 * 分页
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any> paginate(
    pageNumber: Long,
    pageSize: Long,
    totalRow: Long = -1L,
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Page<T> = paginateAs<T, T>(pageNumber, pageSize, totalRow, typeMap, init)

/**
 * 分页
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any, reified R : Any> paginateAs(
    pageNumber: Long,
    pageSize: Long,
    totalRow: Long = -1L,
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Page<R> {
    val start = System.currentTimeMillis()
    val queryBuilder = QueryBuilder<T>()
    queryBuilder.from(T::class)
    init?.invoke(queryBuilder)
    // 设置分页
    queryBuilder.limit(pageSize, pageNumber)
    val (sql, params) = queryBuilder.build()
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    val resultList = executeQueryAndResultList<R>(sql, params, typeMap, start)
    val total = if (totalRow == -1L) {
        queryValue<T>("count(*)") { select(count()) } as Long
    } else totalRow
    val totalPage = ceil(totalRow / pageSize.toDouble()).toLong()
    return Page(pageNumber, pageSize, totalPage, total, resultList)
}

/**
 * query系列函数的构建查询语句
 *
 * @param init 在这里构建查询语句
 * @return Triple<String, List<PrepareStatementParam>, Long>
 */
@FreshenInternalApi
inline fun <reified T : Any> buildQuerySQL(
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Triple<String, Array<PrepareStatementParam>, Long> {
    val start = System.currentTimeMillis()
    val queryBuilder = QueryBuilder<T>()
    queryBuilder.from(T::class)
    init?.invoke(queryBuilder)
    val (sql, params) = queryBuilder.build()
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    return Triple(sql, params, start)
}

/**
 * queryAs系列函数的构建查询语句
 *
 * @param init 在这里构建查询语句
 * @return Triple<String, List<PrepareStatementParam>, Long>
 */
@FreshenInternalApi
inline fun <reified T : Any> buildQueryAsSQL(
    noinline init: (QueryAsBuilder<T>.() -> Unit)? = null
): Triple<String, Array<PrepareStatementParam>, Long> {
    val start = System.currentTimeMillis()
    val queryAsBuilder = QueryAsBuilder<T>()
    queryAsBuilder.from(T::class)
    init?.invoke(queryAsBuilder)
    val (sql, params) = queryAsBuilder.build()
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    return Triple(sql, params, start)
}

/**
 * 执行查询，并返回处理好的结果
 *
 * @param sql SQL语句
 * @param params 参数列表
 * @param typeMap 属性（列）和JDBCType的映射
 * @param start 开始时间
 * @return T
 */
@FreshenInternalApi
inline fun <reified R : Any> executeQueryAndResult(
    sql: String,
    params: Array<PrepareStatementParam>,
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    start: Long
): R? {
    val (connection, statement, resultSet) = executeQuery(sql, params)
    val entity = if (resultSet.next()) {
        resultSet.getObject<R>(typeMap)
    } else null
    connection.closeAndAudit(statement, resultSet, sql, params, start)
    return entity
}

/**
 * 执行查询，并返回处理好的结果
 *
 * @param sql SQL语句
 * @param params 参数列表
 * @param typeMap 属性（列）和JDBCType的映射
 * @param start 开始时间
 * @return List<R>
 */
@FreshenInternalApi
inline fun <reified R : Any> executeQueryAndResultList(
    sql: String,
    params: Array<PrepareStatementParam>,
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    start: Long
): List<R> {
    val (connection, statement, resultSet) = executeQuery(sql, params)
    val list = mutableListOf<R>()
    // 遍历结果集
    while (resultSet.next()) {
        resultSet.getObject<R>(typeMap).let { list.add(it) }
    }
    connection.closeAndAudit(statement, resultSet, sql, params, start)
    return list
}

/**
 * 执行查询，并返回处理好的结果
 *
 * @param sql SQL语句
 * @param params 参数列表
 * @param typeMap 属性（列）和JDBCType的映射
 * @param start 开始时间
 * @return List<R>
 */
@FreshenInternalApi
inline fun <reified R : Any> executeQueryAndResultFlow(
    sql: String,
    params: Array<PrepareStatementParam>,
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    start: Long
): Flow<R> {
    val (connection, statement, resultSet) = executeQuery(sql, params)
    return flow {
        // 遍历结果集
        while (resultSet.next()) {
            emit(resultSet.getObject<R>(typeMap))
        }
    }.onCompletion {
        connection.closeAndAudit(statement, resultSet, sql, params, start)
    }
}

/**
 * 执行查询
 *
 * @param sql SQL语句
 * @param params 参数列表
 * @return Triple<Connection, PreparedStatement, ResultSet>
 */
@FreshenInternalApi
@Suppress("SqlSourceToSinkFlow")
fun executeQuery(
    sql: String,
    params: Array<PrepareStatementParam>? = null
): Triple<Connection, PreparedStatement, ResultSet> {
    val connection = FreshenRuntimeConfig.dataSource.connection
    val statement = connection.prepareStatement(sql)
    statement.setParams(params)
    val resultSet = statement.executeQuery()
    return Triple(connection, statement, resultSet)
}