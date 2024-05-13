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

/**
 * 列注解
 *
 * @property value 列名，默认为属性名
 * @property ignore 是否忽略该列，默认为false
 * @property onInsertValue insert时该列的值，你设置的值会直接被拼接到sql，默认为空
 * @property onUpdateValue update时该列的值，你设置的值会直接被拼接到sql，默认为空
 */
@MustBeDocumented
@Target(AnnotationTarget.PROPERTY)
annotation class Column(
    val value: String = "",
    val ignore: Boolean = false,
    val onInsertValue: String = "",
    val onUpdateValue: String = ""
)
