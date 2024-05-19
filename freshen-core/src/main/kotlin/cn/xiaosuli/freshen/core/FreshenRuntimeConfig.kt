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

import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.entity.*
import java.sql.JDBCType
import javax.sql.DataSource
import kotlin.reflect.KClass

/**
 * Freshen 运行时配置类
 * 如果在执行CRUD前，调用了【runFreshen】方法，那么理论上就不会发生空指针异常
 */
@FreshenInternalApi
object FreshenRuntimeConfig {
    /**
     * 数据源，运行时赋值
     */
    lateinit var dataSource: DataSource
        private set

    /**
     * 主键生成策略
     */
    var keyGenerator: KeyGenerator = KeyGenerator.NONE
        private set

    /**
     * 逻辑删除配置
     */
    var logicDelete: LogicDelete = LogicDelete.Disable
        private set

    /**
     * 乐观锁配置
     */
    var optimisticLock: OptimisticLock = OptimisticLock.Disable
        private set

    /**
     * 表名前缀
     */
    var tablePrefix: String? = null
        private set

    /**
     * 是否开启驼峰转下划线
     */
    var enabledUnderscoreToCamelCase: Boolean = true
        private set

    /**
     * kType和JDBCType的映射器
     */
    var kTypeAndJDBCTypeMap: Map<KClass<*>, JDBCType> = defaultKTypeAndJDBCTypeMap
        private set

    /**
     * SQL审计1
     * 和sqlAudit2区别：执行时机不同，该方法是在执行SQL前执行，
     * 就算执行sql时报错，也会执行，方便确定sql拼接是否有误。
     * 缺点是拿不到执行耗时
     */
    var sqlAudit1: (sql: String, Array<Any?>) -> Unit = { _, _ -> }
        private set

    /**
     * SQL审计2
     * 和sqlAudit1区别：执行时机不同，该方法是在执行SQL后执行，
     * 能拿到执行耗时，缺点是当执行sql时报错，不会执行
     */
    var sqlAudit2: (sql: String, Array<Any?>, elapsedTime: Long) -> Unit = { _, _, _ -> }
        private set

    /**
     * 初始化运行时配置类所有属性
     */
    fun initializeConfig(freshenConfig: FreshenConfig) {
        dataSource = freshenConfig.dataSource
        keyGenerator = freshenConfig.keyGenerator
        logicDelete = freshenConfig.logicDelete
        optimisticLock = freshenConfig.optimisticLock
        tablePrefix = freshenConfig.tablePrefix
        enabledUnderscoreToCamelCase = freshenConfig.enabledUnderscoreToCamelCase
        kTypeAndJDBCTypeMap = freshenConfig.kTypeAndJDBCTypeMap
        sqlAudit1 = freshenConfig.sqlAudit1
        sqlAudit2 = freshenConfig.sqlAudit2
    }

    /**
     * 初始化datasource
     */
    fun initializeConfig(dataSource: DataSource) {
        this.dataSource = dataSource
    }

    /**
     * 检查运行时配置类中所有属性是否初始化
     *
     * @throws IllegalStateException 如果未初始化，抛出异常
     */
    fun checkDataSourceIsInitialized() {
        if (!::dataSource.isInitialized) {
            throw IllegalStateException("属性${::dataSource.name}未初始化，请检查是否调用了runFreshen方法且该方法在所有代码之前，例如：main函数的第一行。")
        }
    }
}