package cn.xiaosuli.freshen.core.entity

import kotlin.reflect.KProperty1

/**
 * SQL与参数
 *
 * @param sql SQL语句
 * @param params 参数列表
 */
data class SQLWithParams(
    val sql: String,
    val params: List<PrepareStatementParam>
)

/**
 * PrepareStatement参数实体
 *
 * @param index 占位参数位置
 * @param type 参数类型
 * @param value 参数值
 */
data class PrepareStatementParam(
    val index: Int,
    val type: KProperty1<*, *>,
    val value: Any
)