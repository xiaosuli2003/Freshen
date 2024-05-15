package cn.xiaosuli.freshen.core.utils

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.Column
import cn.xiaosuli.freshen.core.anno.Id
import cn.xiaosuli.freshen.core.anno.Table
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

/**
 * kt属性转字段（列）
 */
val KProperty1<*, *>.column: String
    get() {
        var columnName = name.toUnderscore()
        annotations.forEach {
            columnName = when (it) {
                Id::class -> (it as Id).value.ifEmpty { name.toUnderscore() }
                Column::class -> (it as Column).value.ifEmpty { name.toUnderscore() }
                else -> name.toUnderscore()
            }
        }
        return columnName
    }

/**
 * kt类转表名
 */
val KClass<*>.table: String
    get() {
        val table = findAnnotation<Table>()?.value
            ?: FreshenRuntimeConfig.tablePrefix?.let {
                "$it${simpleName!!.toUnderscore()}"
            }
            ?: simpleName!!.toUnderscore()
        return table
    }