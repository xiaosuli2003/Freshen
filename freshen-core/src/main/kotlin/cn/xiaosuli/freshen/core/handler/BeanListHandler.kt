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
package cn.xiaosuli.freshen.core.handler

import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.getBean
import cn.xiaosuli.freshen.core.getColumnLabels
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.reflect.KClass

/**
 * 将`ResultSet`转换为对象列表的`ResultSetHandler`实现。
 *
 * @param R 要转换的Bean类型
 * @param type 要转换的Bean类型
 * @see cn.xiaosuli.freshen.core.handler.ResultSetHandler
 */
@FreshenInternalApi
class BeanListHandler<R : Any>(private val type: KClass<R>) : ResultSetHandler<List<R>> {
    /**
     * 将`ResultSet`转换为对象
     *
     * @param resultSet 要处理的`ResultSet`
     *
     * @return 用`ResultSet`转换后的对象
     *
     * @throws SQLException 数据库访问错误
     */
    override fun handle(resultSet: ResultSet): List<R> {
        val columnLabels = resultSet.getColumnLabels()
        val results = mutableListOf<R>()
        // 遍历结果集
        while (resultSet.next()) {
            resultSet.getBean(columnLabels, type).let { results.add(it) }
        }
        return results
    }
}