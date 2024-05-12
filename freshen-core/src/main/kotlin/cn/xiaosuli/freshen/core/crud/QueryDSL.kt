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
import cn.xiaosuli.freshen.core.builder.QueryBuilder
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.utils.closeAndAudit
import cn.xiaosuli.freshen.core.utils.getObject
import cn.xiaosuli.freshen.core.utils.setParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.sql.Connection
import java.sql.JDBCType
import java.sql.PreparedStatement
import java.sql.ResultSet
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
): List<T> {
    val start = System.currentTimeMillis()
    val queryBuilder = QueryBuilder<T>()
    queryBuilder.from(T::class)
    init?.invoke(queryBuilder)
    val (sql, params) = queryBuilder.build()
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    return executeQueryAndResultList<T>(sql, params, typeMap, start)
}

/**
 * 查询多条记录
 *
 * @param typeMap kType和JDBCType的映射器
 * @param init 在这里构建查询语句
 * @return List<T>
 */
inline fun <reified T : Any> queryFlow(
    typeMap: Map<KProperty1<*, *>, JDBCType>? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Flow<T> {
    val start = System.currentTimeMillis()
    val queryBuilder = QueryBuilder<T>()
    queryBuilder.from<T>()
    init?.invoke(queryBuilder)
    val (sql, params) = queryBuilder.build()
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    return executeQueryAndResultFlow<T>(sql, params, typeMap, start)
}

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
 * 执行查询，并返回处理好的结果
 *
 * @param sql SQL语句
 * @param params 参数列表
 * @param typeMap 属性（列）和JDBCType的映射
 * @param start 开始时间
 * @return T
 */
inline fun <reified R : Any> executeQueryAndResult(
    sql: String,
    params: List<PrepareStatementParam>? = null,
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
inline fun <reified R : Any> executeQueryAndResultList(
    sql: String,
    params: List<PrepareStatementParam>? = null,
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
inline fun <reified R : Any> executeQueryAndResultFlow(
    sql: String,
    params: List<PrepareStatementParam>? = null,
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
@Suppress("SqlSourceToSinkFlow")
fun executeQuery(
    sql: String,
    params: List<PrepareStatementParam>? = null
): Triple<Connection, PreparedStatement, ResultSet> {
    val connection = FreshenRuntimeConfig.dataSource.connection
    val statement = connection.prepareStatement(sql)
    statement.setParams(params)
    val resultSet = statement.executeQuery()
    return Triple(connection, statement, resultSet)
}