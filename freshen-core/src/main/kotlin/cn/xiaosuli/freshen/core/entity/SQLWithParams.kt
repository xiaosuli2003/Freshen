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

package cn.xiaosuli.freshen.core.entity

import kotlin.reflect.KClass

/**
 * SQL与参数
 *
 * @param sql SQL语句
 * @param params 参数列表
 */
data class SQLWithParams(
    val sql: String,
    val params: Array<PrepareStatementParam>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SQLWithParams

        if (sql != other.sql) return false
        if (!params.contentEquals(other.params)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sql.hashCode()
        result = 31 * result + params.contentHashCode()
        return result
    }
}

/**
 * PrepareStatement参数实体
 *
 * @param type 参数类型
 * @param value 参数值
 */
data class PrepareStatementParam(
    val type: KClass<*>,
    val value: Any?
)