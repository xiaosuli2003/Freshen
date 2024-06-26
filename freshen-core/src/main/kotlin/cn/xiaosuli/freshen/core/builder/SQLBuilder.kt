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
import kotlin.reflect.KClass

/**
 * 最基本的SQL构造器
 */
@FreshenInternalApi
interface SQLBuilder<T : Any> {

    /**
     * 构建SQL，并返回SQL何占位参数
     *
     * @param table 表对应的实体类
     * @return SQL和占位参数
     */
    fun build(table: KClass<T>): Pair<String, Array<Any?>>
}