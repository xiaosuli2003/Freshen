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
import javax.sql.DataSource

/**
 * 启动 Freshen
 *
 * @param freshenConfig Freshen配置信息
 */
fun runFreshen(freshenConfig: FreshenConfig) {
    // 将获取的配置信息赋值给【运行时配置类】对应的属性
    FreshenRuntimeConfig.initializeConfig(freshenConfig)
    // 全部初始化完了，再检查一下dataSource否已初始化
    FreshenRuntimeConfig.checkDataSourceIsInitialized()
}

/**
 * 启动 Freshen
 *
 * @param dataSource 数据源
 */
fun runFreshen(dataSource: DataSource) {
    // 将获取的datasource赋值给【运行时配置类】的datasource
    FreshenRuntimeConfig.initializeConfig(dataSource)
    // 全部初始化完了，再检查一下dataSource否已初始化
    FreshenRuntimeConfig.checkDataSourceIsInitialized()
}