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

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.entity.LogicDelete
import cn.xiaosuli.freshen.core.table
import kotlin.reflect.KClass

/**
 * 查询语句构造器（适用于单表查询）
 * 适用于query，queryOne,queryFlow
 *
 * @param T 表对应的实体类
 */
@FreshenInternalApi
open class DeleteBuilder<T : Any> : QueryConditionScope, QueryMethodScope, SQLBuilder<T> {

    /**
     * where condition
     */
    private var where = ""


    /**
     * where占位参数列表
     */
    private var whereParams: Array<Any?> = emptyArray()

    // where ==================================================

    /**
     * 构建where子句
     *
     * @param queryCondition 查询条件
     */
    fun where(queryCondition: QueryCondition) {
        where = "where ${queryCondition.toSql()}"
        whereParams = queryCondition.queryParams
    }

    /**
     * 构建where子句
     *
     * @param action 查询lambda
     */
    fun where(action: QueryCondition.() -> QueryCondition) {
        val emptyCondition = QueryCondition.EmptyCondition
        val queryCondition = emptyCondition.action()
        where = "where ${queryCondition.toSql()}"
        whereParams = queryCondition.queryParams
    }

    // sql build ==================================================

    /**
     * 构建SQL，并返回SQL何占位参数
     *
     * @param table 表对应的实体类
     * @return SQL和占位参数
     */
    override fun build(table: KClass<T>): Pair<String, Array<Any?>> {
        val sql = buildString {
            when (FreshenRuntimeConfig.logicDelete) {
                LogicDelete.Disable -> {
                    append("delete")
                    append(" ")
                    append(table.table)
                    append(" ")
                    append(where)
                }

                is LogicDelete.Enable -> {
                    val logicDelete = FreshenRuntimeConfig.logicDelete as LogicDelete.Enable
                    val (columnName, _, deletedValue) = logicDelete
                    append("update")
                    append(" ")
                    append(table.table)
                    append(" ")
                    append("set $columnName = $deletedValue")
                    append(" ")
                    append(where)
                }
            }
        }
        val params = arrayOf(*whereParams)
        return Pair(sql, params)
    }
}