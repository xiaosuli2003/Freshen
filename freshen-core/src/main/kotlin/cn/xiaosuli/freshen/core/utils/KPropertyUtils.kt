package cn.xiaosuli.freshen.core.utils

import cn.xiaosuli.freshen.core.anno.Column
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.anno.Id
import kotlin.reflect.KProperty1

/**
 * kt属性转字段（列）
 */
val KProperty1<*, *>.column: String
    get() = {
        var columnName = ""
        annotations.forEach {
            when (it) {
                Id::class -> {
                    columnName = (it as Id).value.ifEmpty { name.toUnderscore() }
                }

                Column::class -> {
                    columnName = (it as Column).value.ifEmpty { name.toUnderscore() }
                }

                else -> this.name.toUnderscore()
            }
        }
        columnName
    }.toString()