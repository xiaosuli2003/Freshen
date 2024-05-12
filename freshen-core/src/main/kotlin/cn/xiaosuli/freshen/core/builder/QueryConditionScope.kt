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

package cn.xiaosuli.freshen.core.builder

import cn.xiaosuli.freshen.core.utils.toUnderscore
import kotlin.reflect.KProperty1

/**
 * where和having的条件符
 */
interface QueryConditionScope<T> {

    /**
     * 等于(=)
     *
     * @param value 模糊匹配的值
     * @return `column = value`
     */
    infix fun <V> KProperty1<T, V>.eq(value: V): QueryCondition =
        QueryCondition.BaseCondition(this, "=", value.toString())

    /**
     * 不等于(!=)
     *
     * @param value 模糊匹配的值
     * @return `column != value`
     */
    infix fun <V> KProperty1<T, V>.ne(value: V): String =
        "${name.toUnderscore()} != \"$value\""

    /**
     * 大于(>)
     *
     * @param value 模糊匹配的值
     * @return `column > value`
     */
    infix fun <V> KProperty1<T, V>.gt(value: V): String =
        "${name.toUnderscore()} > \"$value\""

    /**
     * 小于(<)
     *
     * @param value 模糊匹配的值
     * @return `column < value`
     */
    infix fun <V> KProperty1<T, V>.lt(value: V): String =
        "${name.toUnderscore()} < \"$value\""

    /**
     * 小于等于(<=)
     *
     * @param value 模糊匹配的值
     * @return `column <= value`
     */
    infix fun <V> KProperty1<T, V>.le(value: V): String =
        "${name.toUnderscore()} <= \"$value\""

    /**
     * 大于等于(>=)
     *
     * @param value 模糊匹配的值
     * @return `column >= value`
     */
    infix fun <V> KProperty1<T, V>.ge(value: V): String =
        "${name.toUnderscore()} >= \"$value\""

    /**
     * in(`in(value1,value2,...)`)
     *
     * @param values 区间
     * @return `column in (value1, value2)`
     */
    fun <V> KProperty1<T, V>.`in`(vararg values: V): String {
        val list = values.joinToString(",") { "\"$it\"" }
        return "${name.toUnderscore()} in ($list)"
    }

    /**
     * not in(`not in(value1,value2,...)`)
     *
     * @param values 区间
     * @return `column not in (value1, value2)`
     */
    fun <V> KProperty1<T, V>.notIn(vararg values: V): String {
        val list = values.joinToString(",") { "\"$it\"" }
        return "${name.toUnderscore()} not in ($list)"
    }

    /**
     * between and(`between value1 and value2`)
     *
     * @param range 区间
     * @return `column between value1 and value2`
     */
    infix fun <V : Comparable<V>> KProperty1<T, V>.`in`(range: ClosedRange<V>) =
        "${name.toUnderscore()} between ${range.start} and ${range.endInclusive}"

    /**
     * not between and(`between value1 and value2`)
     *
     * @param range 区间
     * @return `column not between value1 and value2`
     */
    infix fun <V : Comparable<V>> KProperty1<T, V>.notIn(range: ClosedRange<V>) =
        "${name.toUnderscore()} not between ${range.start} and ${range.endInclusive}"

    /**
     * like模糊匹配，需要手拼%
     * * 示例：`"%${someThing}%"`
     *
     * @param value 模糊匹配的值
     * @return `column like "%value%"`
     */
    infix fun KProperty1<T, *>.like(value: String): String =
        "${name.toUnderscore()} like \"$value\""

    /**
     * not like模糊匹配，需要手拼%
     * * 示例：`"%${someThing}%"`
     *
     * @param value 模糊匹配的值
     * @return `column not like "%value%"`
     */
    infix fun KProperty1<T, *>.notLike(value: String): String =
        "${name.toUnderscore()} not like \"$value\""

    /**
     * is null(`is null`)
     *
     * @return `column is null`
     */
    fun KProperty1<T, *>.isNull() = "${name.toUnderscore()} is null"

    /**
     * is not null(`is not null`)
     *
     * @return `column is not null`
     */
    fun KProperty1<T, *>.isNotNull() = "${name.toUnderscore()} is not null"
}