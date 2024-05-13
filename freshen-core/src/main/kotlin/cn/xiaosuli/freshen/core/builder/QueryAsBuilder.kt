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
import cn.xiaosuli.freshen.core.utils.column
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * 查询语句构造器：
 * 适用于 queryAs，queryOne,queryAsync,queryOneAsync
 *
 * @param T 表对应的实体类
 */
@FreshenInternalApi
class QueryAsBuilder<T : Any> : QueryBuilder<T>() {
    private var join = ""

    fun join() {
        select = ""
    }

    infix fun <T, V> KProperty1<T, *>.`as`(value: String): String = "as"

    /**
     * 设置要查询的列
     *
     * @param columns1 列名
     * @param columns2 列名
     */
    fun select(columns1: List<KProperty1<T, *>>, vararg columns2: KProperty1<T, *>) {
        select(columns1, columns2.toList())
    }

    /**
     * 设置要查询的列
     * * 建议直接硬编码列名，不要通过外面传入，否则可能会导致SQL注入问题
     * * 建议使用select(vararg columns: KProperty1<T,*>)这样的方法
     *
     * @param columns1 列名
     * @param columns2 列名
     */
    fun select(columns1: List<String>, vararg columns2: String) {
        select = buildString {
            append("select ")
            columns1.forEach { append("${it},") }
            columns2.forEach { append("${it},") }
        }.dropLast(1)
    }

    /**
     * 设置要查询的列
     *
     * @param columnsList 列名集合
     */
    fun select(vararg columnsList: List<KProperty1<T, *>>) {
        select = buildString {
            append("select ")
            columnsList.forEach { columns ->
                columns.forEach { append("${it.column},") }
            }
        }.dropLast(1)
    }

    /**
     * 查询全部字段
     *
     * @return 所有字段集合
     */
    val KClass<T>.allColumns: List<String>
        get() = memberProperties.map { it.column }
}