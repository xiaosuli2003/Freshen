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

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import java.sql.Connection

fun transaction(
    init: (Connection) -> Unit
) {
    val connection = FreshenRuntimeConfig.dataSource.connection
    connection.autoCommit = false
    try {
        init(connection)
    } catch (e: Exception) {
        connection.rollback()
        throw e
    } finally {
        connection.commit()
        connection.close()
    }
}