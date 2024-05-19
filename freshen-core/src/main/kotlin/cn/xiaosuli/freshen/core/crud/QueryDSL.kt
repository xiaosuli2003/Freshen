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
import cn.xiaosuli.freshen.core.handler.BeanListHandler
import cn.xiaosuli.freshen.core.setParams
import cn.xiaosuli.freshen.core.utils.DBUtils
import kotlinx.coroutines.flow.Flow
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * 查询多条记录
 *
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return List<T>
 */
inline fun <reified T : Any> query(
    connection: Connection? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): List<T> {
    // 记录函数开始执行时间
    val start = System.currentTimeMillis()
    // 生成查询语句和填充占位的参数数组
    val (sql, params) = buildQuerySQL<T>(init)
    // 在SQL构建完毕，执行statement之前调用sqlAudit1
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    // 执行查询语句
    val (conn, statement, resultSet) = executeQuery(connection, sql, params)
    // 使用传入的结果集处理器处理`ResultSet`
    val result = BeanListHandler(T::class).handle(resultSet)
    // 如果传入的connection为空，则关闭连接
    if (connection == null) {
        DBUtils.close(conn, statement, resultSet)
    }
    // 在结果集处理完毕，返回数据之前调用sqlAudit2
    FreshenRuntimeConfig.sqlAudit2(sql, params, System.currentTimeMillis() - start)
    return result
}

/**
 * 生成查询语句和填充占位的参数数组
 *
 * @param T 要查询的表对应的实体
 * @param init 构建查询语句的lambda
 * @return 查询语句和填充占位的参数数组
 */
inline fun <reified T : Any> buildQuerySQL(
    noinline init: (QueryBuilder<T>.() -> Unit)?
): Pair<String, Array<Any?>> {
    // 创建QueryBuilder对象
    val queryBuilder = QueryBuilder<T>()
    // 执行构建SQL的lambda
    init?.invoke(queryBuilder)
    // 返回SQL和占位参数
    return queryBuilder.build(T::class)
}

/**
 * 查询多条记录
 *
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return Flow<T>
 */
inline fun <reified T : Any> queryFlow(
    connection: Connection? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Flow<T> = queryFlowAs<T, T>(connection, init)

/**
 * 查询一条记录
 *
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return List<T>
 */
/*
inline fun <reified T : Any> queryOne(
    connection: Connection? = null,
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
*/

/**
 * 查询多条记录
 *
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any, reified R : Any> queryAs(
    connection: Connection? = null,
    noinline init: (QueryAsBuilder<T>.() -> Unit)? = null
): List<R> {
    TODO("TODO: queryAs")
    // val (sql, params, start) = buildQueryAsSQL<T>(init)
    // return executeQueryAndResultList<R>(sql, params, typeMap, start)
}

/**
 * 查询多条记录
 *
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return Flow<R>
 */
inline fun <reified T : Any, reified R : Any> queryFlowAs(
    connection: Connection? = null,
    noinline init: (QueryAsBuilder<T>.() -> Unit)? = null
): Flow<R> {
    TODO("TODO: queryFlowAs")
    // val (sql, params, start) = buildQueryAsSQL<T>(init)
    // return executeQueryAndResultFlow<R>(sql, params, typeMap, start)
}

/**
 * 查询一个值
 * * 该方法只适用于查询一列，且值不为null，否则JDBC会抛异常
 * * 例如：`select count(*) from table`
 *
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any> queryValue(
    connection: Connection? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Any {
    TODO("TODO: queryValue")
    // val (sql, params, start) = buildQuerySQL<T>(init)
    // val (connection, statement, resultSet) = executeQuery(sql, params)
    // // TODO: 未检查next()的返回值，这里可能会抛异常
    // resultSet.next()
    // val value = resultSet.getBean(column)
    // connection.closeAndAudit(statement, resultSet, sql, params, start)
    // return value
}

/**
 * 查询一个值
 *
 * @param column 列名
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any> queryValueOrNull(
    column: String, noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Any? {
    TODO("TODO: queryValue")
    // val (sql, params, start) = buildQuerySQL<T>(init)
    // val (connection, statement, resultSet) = executeQuery(sql, params)
    // val value = if (resultSet.next()) {
    //     resultSet.getBean(column)
    // } else null
    // connection.closeAndAudit(statement, resultSet, sql, params, start)
    // return value
}

/**
 * 分页
 *
 * @param pageNumber 页码
 * @param pageSize 每页大小
 * @param totalRow 总记录数
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any> paginate(
    pageNumber: Long,
    pageSize: Long,
    totalRow: Long = -1L,
    connection: Connection? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Page<T> = paginateAs<T, T>(pageNumber, pageSize, totalRow, connection, init)

/**
 * 分页
 *
 * @param pageNumber 页码
 * @param pageSize 每页大小
 * @param totalRow 总记录数
 * @param connection 数据库连接
 * @param init 在这里构建查询语句
 * @return List<R>
 */
inline fun <reified T : Any, reified R : Any> paginateAs(
    pageNumber: Long,
    pageSize: Long,
    totalRow: Long = -1L,
    connection: Connection? = null,
    noinline init: (QueryBuilder<T>.() -> Unit)? = null
): Page<R> {
    TODO("TODO: paginateAs")
    // val start = System.currentTimeMillis()
    // val queryBuilder = QueryBuilder<T>()
    // queryBuilder.from(T::class)
    // init?.invoke(queryBuilder)
    // // 设置分页
    // queryBuilder.limit(pageSize, pageNumber)
    // val (sql, params) = queryBuilder.build()
    // FreshenRuntimeConfig.sqlAudit1(sql, params)
    // val resultList = executeQueryAndResultList<R>(sql, params, typeMap, start)
    // val total = if (totalRow == -1L) {
    //     queryValue<T>("count(*)") { select(count()) } as Long
    // } else totalRow
    // val totalPage = ceil(totalRow / pageSize.toDouble()).toLong()
    // return Page(pageNumber, pageSize, totalPage, total, resultList)
}

/**
 * 执行查询
 *
 * @param connection 数据库连接
 * @param sql SQL语句
 * @param params 参数列表
 * @return Triple<Connection, PreparedStatement, ResultSet>
 */
@FreshenInternalApi
@Suppress("SqlSourceToSinkFlow")
fun executeQuery(
    connection: Connection?,
    sql: String,
    params: Array<Any?>
): Triple<Connection, PreparedStatement, ResultSet> {
    // 获取连接，如果传入的connection为空，则创建一个新连接
    val conn = connection ?: FreshenRuntimeConfig.dataSource.connection
    val statement = conn.prepareStatement(sql)
    statement.setParams(params)
    val resultSet = statement.executeQuery()
    return Triple(conn, statement, resultSet)
}