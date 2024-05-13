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

import kotlin.reflect.KProperty1

/**
 * where和having的条件符
 */
interface QueryConditionScope<T> {

    /**
     * 等于(=)
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun <V : Any?> KProperty1<T, V>.eq(value: V): QueryCondition =
        QueryCondition.BaseCondition(this, "=", value as Any)

    /**
     * 不等于(!=)
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun <V : Any?> KProperty1<T, V>.ne(value: V): QueryCondition =
        QueryCondition.BaseCondition(this, "!=", value as Any)

    /**
     * 大于(>)
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun <V : Any?> KProperty1<T, V>.gt(value: V): QueryCondition =
        QueryCondition.BaseCondition(this, ">", value as Any)

    /**
     * 小于(<)
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun <V : Any?> KProperty1<T, V>.lt(value: V): QueryCondition =
        QueryCondition.BaseCondition(this, "<", value as Any)

    /**
     * 小于等于(<=)
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun <V : Any?> KProperty1<T, V>.le(value: V): QueryCondition =
        QueryCondition.BaseCondition(this, "<=", value as Any)

    /**
     * 大于等于(>=)
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun <V : Any?> KProperty1<T, V>.ge(value: V): QueryCondition =
        QueryCondition.BaseCondition(this, ">=", value as Any)

    /**
     * in(`in(value1,value2,...)`)
     *
     * @param values 区间
     * @return QueryCondition
     */
    fun <V : Any> KProperty1<T, V>.`in`(vararg values: V): QueryCondition =
        QueryCondition.InCondition(this, "in", values.toList())

    /**
     * not in(`not in(value1,value2,...)`)
     *
     * @param values 区间
     * @return QueryCondition
     */
    fun <V : Any> KProperty1<T, V>.notIn(vararg values: V): QueryCondition =
        QueryCondition.InCondition(this, "not in", values.toList())

    /**
     * between and(`between value1 and value2`)
     *
     * @param range 区间
     * @return QueryCondition
     */
    infix fun <V : Comparable<V>> KProperty1<T, V>.`in`(range: ClosedRange<V>): QueryCondition =
        QueryCondition.BetweenCondition(this, "between", range)

    /**
     * not between and(`between value1 and value2`)
     *
     * @param range 区间
     * @return QueryCondition
     */
    infix fun <V : Comparable<V>> KProperty1<T, V>.notIn(range: ClosedRange<V>): QueryCondition =
        QueryCondition.BaseCondition(this, "not between", range)

    /**
     * like模糊匹配，需要手拼%
     * * 示例：`"%${someThing}%"`
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun KProperty1<T, *>.like(value: String): QueryCondition =
        QueryCondition.BaseCondition(this, "like", value)

    /**
     * not like模糊匹配，需要手拼%
     * * 示例：`"%${someThing}%"`
     *
     * @param value 模糊匹配的值
     * @return QueryCondition
     */
    infix fun KProperty1<T, *>.notLike(value: String): QueryCondition =
        QueryCondition.BaseCondition(this, "not like", value)

    /**
     * is null(`is null`)
     *
     * @return QueryCondition
     */
    fun KProperty1<T, *>.isNull(): QueryCondition =
        QueryCondition.NullCondition(this, "is null")

    /**
     * is not null(`is not null`)
     *
     * @return QueryCondition
     */
    fun KProperty1<T, *>.isNotNull(): QueryCondition =
        QueryCondition.NullCondition(this, "is not null")
}