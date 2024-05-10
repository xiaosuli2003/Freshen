/**
 * 启动 Freshen
 * @author xiaosuli
 * @date 2024-05-07
 */
package cn.xiaosuli.freshen.core

import cn.xiaosuli.freshen.core.entity.FreshenConfig

/**
 * 启动 Freshen
 *
 * @param init Freshen初始化块，返回用户定义的Freshen配置信息
 */
fun runFreshen(init: () -> FreshenConfig) {
    // 获取用户定义的配置信息
    val freshenSLConfig = init()
    // 将获取的配置信息赋值给【运行时配置类】对应的属性
    FreshenRuntimeConfig.dataSource = freshenSLConfig.dataSource
    FreshenRuntimeConfig.keyStrategy = freshenSLConfig.keyStrategy
    FreshenRuntimeConfig.logicDelete = freshenSLConfig.logicDelete
    FreshenRuntimeConfig.optimisticLock = freshenSLConfig.optimisticLock
    FreshenRuntimeConfig.tablePrefix = freshenSLConfig.tablePrefix
    FreshenRuntimeConfig.enabledUnderscoreToCamelCase = freshenSLConfig.enabledUnderscoreToCamelCase
    FreshenRuntimeConfig.sqlAudit1 = freshenSLConfig.sqlAudit1
    FreshenRuntimeConfig.sqlAudit2 = freshenSLConfig.sqlAudit2
    // 全部初始化完了，再检查一下dataSource否已初始化
    FreshenRuntimeConfig.checkDataSourceIsInitialized()
}

