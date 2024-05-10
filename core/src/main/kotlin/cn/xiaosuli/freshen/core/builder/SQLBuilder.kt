package cn.xiaosuli.freshen.core.builder

import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.entity.SQLWithParams

/**
 * 最基本的SQL构造器
 */
interface SQLBuilder {
    /**
     * SQL占位参数列表
     */
    val params: List<PrepareStatementParam>

    /**
     * 构建where子句
     *
     * @param condition 条件
     */
    fun where(vararg condition: String)

    /**
     * 构建SQL
     */
    fun build(): SQLWithParams
}