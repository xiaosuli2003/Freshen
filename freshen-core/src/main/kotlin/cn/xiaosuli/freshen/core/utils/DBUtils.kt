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

package cn.xiaosuli.freshen.core.utils

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

object DBUtils {
    /**
     * 关闭连接，释放资源
     *
     * @param connection Connection对象
     * @param stmt Statement对象
     * @param rs ResultSet对象
     */
    fun close(connection: Connection?, stmt: Statement?, rs: ResultSet?) {
        try {
            rs?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            stmt?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        try {
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}