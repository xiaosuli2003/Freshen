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
package cn.xiaosuli.freshen.core

import cn.xiaosuli.freshen.core.anno.Column
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.anno.Id
import cn.xiaosuli.freshen.core.anno.Table
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
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

/**
 * 将驼峰命名转换为下划线分隔的字符串。
 *
 * * 此扩展函数为Freshen内部API，还请不要使用。
 * * 如果你也想实现类似的转换，请复制以下代码（即去掉与Freshen相关联的业务代码）
 * ```kotlin
 * fun String.toUnderscore(): String {
 *     val regex = Regex("([a-z])([A-Z])")
 *     return this.replace(regex, "$1_$2").lowercase(Locale.getDefault())
 * }
 * ```
 *
 * @return 转换后的下划线分隔字符串。
 */
@FreshenInternalApi
fun String.toUnderscore(): String {
    if (!FreshenRuntimeConfig.enabledUnderscoreToCamelCase) return this
    val regex = Regex("([a-z])([A-Z])")
    return this.replace(regex, "$1_$2").lowercase(Locale.getDefault())
}

/**
 * 将下划线分隔的字符串转换为驼峰命名。
 *
 * @return 转换后的驼峰命名字符串。
 */
@FreshenInternalApi
fun String.toCamelCase(): String {
    return split("_")
        .mapIndexed { index, part ->
            if (index == 0) part.lowercase()
            else part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
        .joinToString("")
}

/**
 * 给PreparedStatement设置参数
 *
 * @param params 参数列表
 */
@FreshenInternalApi
fun PreparedStatement.setParams(params: Array<Any?>) {
    params.forEachIndexed { index, param ->
        setObject(index + 1, param)
    }
}

/**
 * 获取ResultSet的列名（列标签）
 *
 * @return 列标签的集合
 */
fun ResultSet.getColumnLabels(): List<String> {
    val columnLabels = mutableListOf<String>()
    for (i in 1..metaData.columnCount) {
        columnLabels.add(metaData.getColumnLabel(i))
    }
    return columnLabels
}

/**
 * 反射构造对象
 *
 * @param columnLabels 查询的列
 * @param R 要反射构造的对象类型
 * @return R?
 */
fun <R : Any> ResultSet.getBean(columnLabels: List<String>, type: KClass<R>): R {
    val noArgsConstructor = type.constructors.firstOrNull { it.parameters.isEmpty() }
    return if (noArgsConstructor == null) {
        // 走有参构造对象
        getBeanForHasArgsConstructor(columnLabels, type)
    } else {
        // 走无参构造对象
        getObjForNoArgsConstructor(columnLabels, type)
    }
}

/**
 * 反射构造对象（适用于有参构造器）
 *
 * @param columnLabels 查询的列
 * @param R 要反射构造的对象类型
 * @return R
 */
fun <R : Any> ResultSet.getBeanForHasArgsConstructor(columnLabels: List<String>, type: KClass<R>): R {
    val hasArgsConstructor = type.constructors.firstOrNull()
    val size = hasArgsConstructor!!.parameters.size
    val params = Array<Any?>(size) {}
    type.members.filterIsInstance<KProperty1<R, *>>().forEach { kProperty1 ->
        hasArgsConstructor.parameters.forEachIndexed { index, parameter ->
            if (parameter.name == kProperty1.name) {
                val column = kProperty1.column
                params[index] = if (column in columnLabels) {
                    getObject(column)
                } else null
            }
        }
    }
    return hasArgsConstructor.call(*params)
}

/**
 * 反射构造对象（适用于无参构造器）
 *
 * @param columnLabels 查询的列
 * @return R
 */
fun <R : Any> ResultSet.getObjForNoArgsConstructor(columnLabels: List<String>, type: KClass<R>): R {
    val entity = type.constructors.firstOrNull { it.parameters.isEmpty() }!!.call()
    type.members.filterIsInstance<KProperty1<R, *>>().forEach { kProperty1 ->
        val column = kProperty1.column
        val kmp = kProperty1 as KMutableProperty1<R, *>
        if (column in columnLabels) {
            when (kProperty1.returnType.classifier) {
                Boolean::class -> kmp.setter.call(entity, getBoolean(column))
                Byte::class -> kmp.setter.call(entity, getByte(column))
                Short::class -> kmp.setter.call(entity, getShort(column))
                Int::class -> kmp.setter.call(entity, getInt(column))
                Long::class -> kmp.setter.call(entity, getLong(column))
                Float::class -> kmp.setter.call(entity, getFloat(column))
                Double::class -> kmp.setter.call(entity, getDouble(column))
                BigDecimal::class -> kmp.setter.call(entity, getBigDecimal(column))
                String::class -> kmp.setter.call(entity, getString(column))
                LocalDate::class -> kmp.setter.call(entity, getDate(column).toLocalDate())
                LocalTime::class -> kmp.setter.call(entity, getTime(column).toLocalTime())
                LocalDateTime::class -> kmp.setter.call(entity, getTimestamp(column).toLocalDateTime())
                Date::class -> kmp.setter.call(entity, getTimestamp(column))
                else -> kmp.setter.call(entity, getObject(column))
            }
        }
    }
    return entity
}