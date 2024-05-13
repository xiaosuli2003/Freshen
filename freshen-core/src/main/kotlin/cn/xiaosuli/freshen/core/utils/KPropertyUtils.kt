package cn.xiaosuli.freshen.core.utils

import cn.xiaosuli.freshen.core.anno.Column
import cn.xiaosuli.freshen.core.anno.Id
import kotlin.reflect.KProperty1

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