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

import cn.xiaosuli.freshen.core.utils.column
import kotlin.reflect.KProperty1

/**
 * mysql常用聚合函数
 */
interface QueryMethodScope<T> {

    /**
     * sql的count()函数
     *
     * @return count(*)
     */
    fun count(): String = "count(*)"

    /**
     * sql的count()函数
     *
     * @return count(column)
     */
    fun count(column: KProperty1<T, *>): String = "count(${column.column})"

    /**
     * sql的sum()函数
     *
     * @return sum(column)
     */
    fun sum(column: KProperty1<T, *>): String = "sum(${column.column})"

    /**
     * sql的avg()函数
     *
     * @return avg(column)
     */
    fun avg(column: KProperty1<T, *>): String = "avg(${column.column})"

    /**
     * sql的max()函数
     *
     * @return max(column)
     */
    fun max(column: KProperty1<T, *>): String = "max(${column.column})"

    /**
     * sql的min()函数
     *
     * @return min(column)
     */
    fun min(column: KProperty1<T, *>): String = "min(${column.column})"
}