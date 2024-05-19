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
import cn.xiaosuli.freshen.core.column
import kotlin.reflect.KProperty1

/**
 * 条件构造器
 */
@FreshenInternalApi
abstract class QueryCondition {
    /**
     * 条件lambda转SQL语句
     */
    abstract fun toSql(): String

    /**
     * 占位参数列表
     */
    abstract val queryParams: Array<Any?>

    /**
     * 用于表示逻辑操作
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
         * 是否添加括号
         */
        abstract var addParentheses: Boolean

        /**
         * 用and拼接两个条件
         *
         * @param current 当前条件
         * @param next 下一个条件
         * @param addParentheses 是否添加括号
         * @return `(current and next)`
         */
        class And(
            override val current: QueryCondition,
            override val next: QueryCondition,
            override var addParentheses: Boolean = false
        ) : LogicalExpression() {

            /**
             * 条件转sql
             *
             * @return `(current and next)`
             */
            override fun toSql(): String = if (addParentheses) {
                "(${current.toSql()} and ${next.toSql()})"
            } else {
                "${current.toSql()} and ${next.toSql()}"
            }

            /**
             * 占位参数列表
             */
            override val queryParams: Array<Any?> =
                arrayOf(*current.queryParams, *next.queryParams)
        }

        /**
         * 用or拼接两个条件
         *
         * @param current 当前条件
         * @param next 下一个条件
         * @param addParentheses 是否添加括号
         * @return `(current or next)`
         */
        class Or(
            override val current: QueryCondition,
            override val next: QueryCondition,
            override var addParentheses: Boolean = false
        ) : LogicalExpression() {
            /**
             * 条件转sql
             *
             * @return `(current or next)`
             */
            override fun toSql(): String = if (addParentheses) {
                "(${current.toSql()} or ${next.toSql()})"
            } else {
                "${current.toSql()} or ${next.toSql()}"
            }

            /**
             * 占位参数列表
             */
            override val queryParams: Array<Any?> =
                arrayOf(*current.queryParams, *next.queryParams)
        }
    }

    /**
     * 用于表示空条件
     */
    data object EmptyCondition : QueryCondition() {
        override fun toSql(): String = ""
        override val queryParams: Array<Any?> = emptyArray()
    }

    /**
     * 用于表示`exists/not exists`条件
     */
    sealed class ExistsCondition : QueryCondition() {
        /**
         * 子查询
         */
        abstract val subquery: String

        /**
         * 存在子查询
         *
         * @param subquery 子查询
         * @return `exists (subquery)`
         */
        class Exists(override val subquery: String) : ExistsCondition() {
            override fun toSql(): String = "exists ($subquery)"
            override val queryParams: Array<Any?> = emptyArray()
        }

        /**
         * 不存在子查询
         *
         * @param subquery 子查询
         * @return `exists (subquery)`
         */
        class NotExists(override val subquery: String) : ExistsCondition() {
            override fun toSql(): String = "not exists ($subquery)"
            override val queryParams: Array<Any?> = emptyArray()
        }
    }

    /**
     * 用于表示not条件
     */
    data class NotCondition(val notCondition: QueryCondition) : QueryCondition() {
        override fun toSql(): String = "not (${notCondition.toSql()})"
        override val queryParams: Array<Any?> = notCondition.queryParams
    }

    /**
     * 用于表示基础条件
     *
     * @param property 属性
     * @param operator 操作符
     * @param value 值
     */
    data class BaseCondition<T>(
        val property: KProperty1<T, *>,
        val operator: String,
        val value: Any
    ) : QueryCondition() {
        override fun toSql(): String = "${property.column} $operator ?"

        /**
         * 占位参数列表
         */
        override val queryParams: Array<Any?> = arrayOf(value)
    }

    /**
     * 用于表示 null / not null 条件
     *
     * @param property 属性
     * @param operator 操作符
     */
    data class NullCondition<T>(
        val property: KProperty1<T, *>,
        val operator: String,
    ) : QueryCondition() {
        override fun toSql(): String = "${property.column} $operator"

        /**
         * 占位参数列表
         */
        override val queryParams: Array<Any?> = emptyArray()
    }

    /**
     * 用于表示 between and / not between and条件
     *
     * @param property 属性
     * @param operator 操作符
     */
    data class BetweenCondition<T, V : Comparable<V>>(
        val property: KProperty1<T, V>,
        val operator: String,
        val range: ClosedRange<V>
    ) : QueryCondition() {
        override fun toSql(): String {
            return "${property.column} $operator ? and ?"
        }

        /**
         * 占位参数列表
         */
        override val queryParams: Array<Any?> = arrayOf(range.start, range.endInclusive)
    }

    /**
     * 用于表示 in /not in 条件
     *
     * @param property 属性
     * @param operator 操作符
     */
    class InCondition<T>(
        private val property: KProperty1<T, *>,
        private val operator: String,
        private val values: List<Any>,
    ) : QueryCondition() {
        override fun toSql(): String {
            val list = values.joinToString(",") { "?" }
            return "${property.column} $operator ($list)"
        }

        /**
         * 占位参数列表
         */
        override val queryParams: Array<Any?> = values.toTypedArray()
    }

    /**
     * 同sql中and条件
     */
    infix fun and(next: QueryCondition): QueryCondition = LogicalExpression.And(this, next)

    /**
     * 同sql中and条件
     */
    fun and(action: () -> QueryCondition): QueryCondition {
        val logicalExpression = action.invoke() as LogicalExpression
        logicalExpression.addParentheses = true
        return LogicalExpression.And(this, logicalExpression)
    }

    /**
     * 同sql中or条件
     */
    infix fun or(next: QueryCondition): QueryCondition = LogicalExpression.Or(this, next)

    /**
     * 同sql中or条件
     */
    fun or(action: () -> QueryCondition): QueryCondition {
        val logicalExpression = action.invoke() as LogicalExpression
        logicalExpression.addParentheses = true
        return LogicalExpression.Or(this, logicalExpression)
    }

    /**
     * 同sql中not条件
     */
    infix fun not(notCondition: QueryCondition): QueryCondition = NotCondition(notCondition)

    /**
     * 同sql中not条件
     */
    fun not(action: () -> QueryCondition): QueryCondition = NotCondition(action.invoke())

    /**
     * 同sql中exists条件
     * @param sql 子查询
     */
    fun exists(sql: String) = ExistsCondition.Exists(sql)

    /**
     * 同sql中not exists条件
     * @param sql 子查询
     */
    fun notExists(sql: String) = ExistsCondition.NotExists(sql)
}