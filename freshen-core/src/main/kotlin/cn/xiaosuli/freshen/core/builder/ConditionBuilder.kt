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
 * 基本构造器：
 * 适用于：修改语句、查询语句、删除语句
 *
 * @param T 表对应的实体类
 */
abstract class ConditionBuilder<T> : SQLBuilder {
    var where = ""
    var queryCondition: String = ""

    /**
     * 构建where子句
     *
     * @param condition 条件
     */
    override fun where(vararg condition: String) {
        // TODO：这是一个where条件集合，condition也要是一个对象包含更多信息
        where = "$condition "
    }

    /**
     * like模糊匹配，需要手拼%
     * 示例："%${someThing}%"
     *
     * @param value 模糊匹配的值
     * @return
     */
    infix fun KProperty1<T, *>.like(value: String): String =
        "${name.toUnderscore()} like \"$value\""

    /**
     * =等于
     *
     * @param V
     * @param value 等于的值
     * @return
     */
    infix fun <V> KProperty1<T, V>.eq(value: V): String =
        "${name.toUnderscore()} = \"$value\""

    /**
     * in
     *
     * @param V
     * @param value 等于的值
     * @return
     */
    infix fun <V> KProperty1<T, V>.`in`(range: IntRange): String =
        "${name.toUnderscore()} between ${range.first} and ${range.last} "

    /**
     * =等于
     *
     * @param V
     * @param value 等于的值
     * @return
     */
    infix fun <V> KProperty1<T, V>.notIn(value: V): String =
        "${name.toUnderscore()} = \"$value\""
}