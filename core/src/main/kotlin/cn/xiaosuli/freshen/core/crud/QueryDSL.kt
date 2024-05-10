package cn.xiaosuli.freshen.core.crud

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.builder.QueryBuilder
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.utils.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * 查询多条记录
 *
 * @param init 在这里构建查询语句
 * @return List<T>
 */
inline fun <reified T : Any> query(noinline init: (QueryBuilder<T>.() -> Unit)? = null): List<T> {
    val start = System.currentTimeMillis()
    val queryBuilder = QueryBuilder<T>()
    queryBuilder.from<T>()
    init?.invoke(queryBuilder)
    val (sql, params) = queryBuilder.build()
    FreshenRuntimeConfig.sqlAudit1(sql,params)
    return executeQueryAndProcessResult<T>(sql, params, start)
}

/**
 * 查询多条记录
 *
 * @param init 在这里构建查询语句
 * @return List<R>
 */
/*inline fun <reified T : FreshenEntity, reified R : Any> queryAs(noinline init: (QueryAsScope<T>.() -> Unit)? = null): List<R> {

    val sql = buildQuerySQLAndAudit()

    val (connection, statement, resultSet) = executeSQL(sql)
    val list = mutableListOf<R>()
    // 遍历结果集
    while (resultSet.next()) {
        // 获取泛型实体主构造器的参数个数，以便确定数组大小
        val size = T::class.countProperties()
        val params = Array<Any>(size) {}
        // 遍历泛型实体的属性
        T::class.getProperties().forEach {
            // 根据属性名获取对应列的值
            // 并根据该属性在构造器中的位置，放入相同索引的数组中
            params[it.index] = resultSet.getObject(it.name.toUnderscore())
        }
        // 根据参数数组，创建实体
        val entity = R::class.newInstance<R>(*params)
        list.add(entity)
    }
    // 关闭连接，释放资源
    ConnUtils.close(connection, statement, resultSet)
    // 执行SQL审计2的lambda
    FreshenRuntimeConfig.sqlAudit2(sql, System.currentTimeMillis() - start)
    return list
}*/


/**
 * 查询一条记录
 *
 * @param init 在这里构建查询语句
 * @return T
 */
/*inline fun <reified T> queryOne(noinline init: (QueryScope<T>.() -> Unit)? = null): T {
    // 记录开始执行此操作的时间
    val start = System.currentTimeMillis()
    // 检查泛型是否为【data class】类型
    T::class.requireDataClass()
    // 清空上次构造SQL的缓存
    QueryBuilder.clear()
    // 构建默认的查询表名，如果在lambda中调用from()方法，这里设置的默认值会被替换
    QueryScope<T>().from<T>()
    // 调用构建查询语句的lambda
    init?.invoke(QueryScope())
    // 获取构建好的SQL
    val sql = QueryBuilder.toSql()
    // 执行SQL审计1的lambda
    FreshenRuntimeConfig.sqlAudit1(sql)
    // 获取数据库连接
    val connection = ConnUtils.getConnection()
    // 创建Statement对象
    val statement = connection.prepareStatement(sql)
    // 执行查询，获取结果集
    val resultSet = statement.executeQuery()
    val size = T::class.countProperties()
    val params = Array<Any>(size) {}
    // 移动一下指针，以获取第一条记录
    resultSet.next()
    T::class.getProperties().forEach {
        params[it.index] = resultSet.getObject(it.name)
    }
    val entity = T::class.newInstance<T>(*params)
    // 关闭连接，释放资源
    ConnUtils.close(connection, statement, resultSet)
    // 执行SQL审计2的lambda
    FreshenRuntimeConfig.sqlAudit2(sql, System.currentTimeMillis() - start)
    return entity
}*/

/**
 * 执行查询，并返回处理好的结果
 *
 * @param sql SQL语句
 * @param params 参数列表
 * @param start 开始时间
 * @return List<T>
 */
inline fun <reified T : Any> executeQueryAndProcessResult(
    sql: String,
    params: List<PrepareStatementParam>? = null,
    start: Long
): List<T> {
    val (connection, statement, resultSet) = executeQuery(sql, params)
    val list = mutableListOf<T>()
    // 遍历结果集
    while (resultSet.next()) {
        resultSet.getObject<T>()?.let { list.add(it) }
    }
    connection.closeAndAudit(statement, resultSet, sql, params,start)
    return list
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