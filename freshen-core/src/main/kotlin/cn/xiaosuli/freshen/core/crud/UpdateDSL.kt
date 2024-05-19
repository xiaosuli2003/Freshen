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

package cn.xiaosuli.freshen.core.crud

import cn.xiaosuli.freshen.core.builder.UpdateBuilder
import java.sql.Connection

/**
 * 修改
 *
 * @param entity 要修改的实体
 * @param ignoreNulls  是否忽略空值
 * @param connection  数据库连接，默认为空，即不启用事务，如果传入，则使用事务
 * @return 受影响的行数
 */
inline fun <reified T : Any> update(
    entity: T,
    ignoreNulls: Boolean = true,
    connection: Connection? = null,
    noinline init: (UpdateBuilder<T>.() -> Unit)? = null
): Int {
    // 记录方法开始执行的时间
    val start = System.currentTimeMillis()
    val updateBuilder = UpdateBuilder<T>()
    init?.invoke(updateBuilder)
    updateBuilder.set(entity, ignoreNulls)
    val (sql, params) = updateBuilder.build(T::class)
    return executeUpdate(sql, params, start, connection)
}