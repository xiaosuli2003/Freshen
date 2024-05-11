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
import kotlin.reflect.KProperty1

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
}