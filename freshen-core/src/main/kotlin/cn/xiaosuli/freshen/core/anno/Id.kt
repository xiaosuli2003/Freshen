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

package cn.xiaosuli.freshen.core.anno

import cn.xiaosuli.freshen.core.entity.KeyGenerator

/**
 * 主键注解
 *
 * @property value 数据库主键名，默认为属性名
 * @property keyGenerator 指定主键生成器，默认为NONE（用户手动设置，或数据库自增）
 */
@MustBeDocumented
@Target(AnnotationTarget.PROPERTY)
annotation class Id(
    val value: String = "",
    val keyGenerator: KeyGenerator = KeyGenerator.NONE
)
