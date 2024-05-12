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

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import java.util.*

/**
 * 将驼峰命名转换为下划线分隔的字符串。
 *
 * * 此扩展函数为Freshen内部API，还请不要使用。
 * * 如果你也想实现类似的转换，请复制以下代码（即去掉与Freshen相关联的业务代码）
 * ```kotlin
 * fun String.toUnderscore(): String {
 *     val regex = Regex("([a-z])([A-Z])")
 *     return this.replace(regex, "$1_$2").lowercase(Locale.getDefault())
 * }
 * ```
 *
 * @return 转换后的下划线分隔字符串。
 */
@FreshenInternalApi
fun String.toUnderscore(): String {
    if (!FreshenRuntimeConfig.enabledUnderscoreToCamelCase) return this
    val regex = Regex("([a-z])([A-Z])")
    return this.replace(regex, "$1_$2").lowercase(Locale.getDefault())
}

/**
 * 将下划线分隔的字符串转换为驼峰命名。
 *
 * @return 转换后的驼峰命名字符串。
 */
/*
fun String.toCamelCase(): String {
    return split("_")
        .mapIndexed { index, part ->
            if (index == 0) part.lowercase()
            else part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
        .joinToString("")
}*/
