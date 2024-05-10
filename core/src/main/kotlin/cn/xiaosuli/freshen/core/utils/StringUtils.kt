package cn.xiaosuli.freshen.core.utils

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import java.util.*

/**
 * 将驼峰命名转换为下划线分隔的字符串。
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
