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

