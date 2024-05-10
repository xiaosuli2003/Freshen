package cn.xiaosuli.freshen.core.builder

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.anno.Table
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.entity.SQLWithParams
import cn.xiaosuli.freshen.core.utils.toUnderscore
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * 查询语句构造器：
 * 适用于query，queryOne,queryAsync,queryOneAsync）
 *
 * @param T 表对应的实体类
 */
@FreshenInternalApi
open class QueryBuilder<T : Any> : ConditionBuilder<T>() {
    var select = "select *"
    var from = ""
    var groupBy = ""
    var having = ""
    var orderBy = ""
    var limit = ""

    override val params: List<PrepareStatementParam>
        get() = TODO("要实现这里的逻辑")

    /**
     * 设置要查询的字段
     *
     * @param properties 属性引用
     */
    fun select(vararg properties: KProperty<*>) {
        select = buildString {
            append("select ")
            properties.forEach { append("${it.name.toUnderscore()},") }
        }.dropLast(1)
    }

    /**
     * 设置要查询的字段
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
     * 设置要查询的表
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
     * @param T 表对应的实体类
     */
    inline fun <reified T> from() {
        // 获取表名，如果有注解，则使用注解的值，否则走统一前缀
        // 如果设置统一前缀，则标名为统一前缀+类名，否则直接使用类名
        val table = T::class.annotations.filterIsInstance<Table>().firstOrNull()?.value
            ?: FreshenRuntimeConfig.tablePrefix?.let {
                "$it${T::class.simpleName!!.toUnderscore()}"
            }
            ?: T::class.simpleName!!.toUnderscore()
        from = "from $table"
    }

    fun limit(pageSize: Int, pageNum: Int) {
        val offset = (pageNum - 1) * pageSize
        limit = "limit $offset, $pageSize"
    }

    /**
     * 拼接SQL
     * TODO: 这只是单表的SQL拼接，若支持多表查询，这个不行
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
        val params = mutableListOf<PrepareStatementParam>()
        // TODO: 设置具体的占位参数
        return SQLWithParams(sql, params)
    }
}