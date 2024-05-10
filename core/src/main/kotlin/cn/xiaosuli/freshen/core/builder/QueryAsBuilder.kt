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