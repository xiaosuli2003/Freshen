/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.xiaosuli.freshen.core.handler

import java.sql.ResultSet
import java.sql.SQLException

/**
 * 该接口的实现将ResultSet转换为其他对象。
 *
 * 修改说明：将原文件翻译为Kotlin版本。
 * @param <R> 把ResultSet转换后的目标类型。
</T> */
interface ResultSetHandler<R : Any> {
    /**
     * 将`ResultSet`转换为对象
     *
     * @param resultSet 要处理的`ResultSet`
     *
     * @return 用`ResultSet`转换后的对象
     *
     * @throws SQLException 数据库访问错误
     */
    @Throws(SQLException::class)
    fun handle(resultSet: ResultSet): R
}