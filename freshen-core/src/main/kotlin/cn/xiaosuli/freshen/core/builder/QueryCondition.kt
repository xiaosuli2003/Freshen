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

import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.utils.toUnderscore
import kotlin.reflect.KProperty1

/**
 * 条件构造器
 *
 * * 写这个代码把CPU差点干烧了，整个代码大纲是通义千问生成的，我修改了一下，
 * * 说实话，我还是看不懂怎么就把条件拼接好了。
 */
@FreshenInternalApi
sealed class QueryCondition {
    abstract fun toSql(): String

    /**
     * 用于内部表示逻辑操作
     */
    sealed class LogicalExpression : QueryCondition() {
        /**
         * 当前条件
         */
        abstract val current: QueryCondition

        /**
         * 下一个条件
         */
        abstract val next: QueryCondition

        /**
         * 用and拼接两个条件
         *
         * @param current 当前条件
         * @param next 下一个条件
         * @return `(current and next)`
         */
        class And(
            override val current: QueryCondition,
            override val next: QueryCondition
        ) : LogicalExpression() {
            override fun toSql(): String = "(${current.toSql()} and ${next.toSql()})"
        }

        /**
         * 用or拼接两个条件
         *
         * @param current 当前条件
         * @param next 下一个条件
         * @return `(current or next)`
         */
        class Or(
            override val current: QueryCondition,
            override val next: QueryCondition
        ) : LogicalExpression() {
            override fun toSql(): String = "(${current.toSql()} or ${next.toSql()})"
        }
    }

    /**
     * 用于表示空条件
     */
    data object EmptyCondition : QueryCondition() {
        override fun toSql(): String = "1 = 1"
    }

    /**
     * 用于内部表示基础条件
     */
    data class BaseCondition(val property: KProperty1<*, *>, val operation: String, val value: String) :
        QueryCondition() {
        override fun toSql(): String = "${property.name.toUnderscore()} $operation '$value'"
    }

    infix fun and(next: QueryCondition): QueryCondition = LogicalExpression.And(this, next)

    fun and(action: () -> QueryCondition): QueryCondition = action.invoke()
    fun or(action: () -> QueryCondition): QueryCondition = action.invoke()

    infix fun or(next: QueryCondition): QueryCondition = LogicalExpression.Or(this, next)

    fun not(expression: String) {

    }

    fun exists(sql: String) {

    }

    fun notExists(sql: String) {

    }
}