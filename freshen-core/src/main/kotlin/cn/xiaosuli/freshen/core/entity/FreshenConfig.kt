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
package cn.xiaosuli.freshen.core.entity

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.JDBCType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.renderer.ClassifierNamePolicy.SHORT

/**
 * Freshen 配置类
 * * sqlAudit1和sqlAudit2区别:
 * * 执行时机不一样，sqlAudit1拿不到最终的执行耗时，但是优点是就算执行prepareStatement发生异常，也会打印SQL
 *
 * @property dataSource 数据源，必传
 * @property keyGenerator 指定主键生成器，默认为None（用户自行处理或数据库自增）
 * @property logicDelete 逻辑删除配置，默认为不启用
 * @property optimisticLock 乐观锁配置，默认为不启用
 * @property tablePrefix 设置表名的统一前缀，默认为空
 * @property enabledUnderscoreToCamelCase 是否开启下划线驼峰转换，默认启用，会同时作用于表名和字段名
 * @property kTypeAndJDBCTypeMap kType和JDBCType的映射器，默认使用defaultKTypeAndJDBCTypeMap
 * @property sqlAudit1 在lambda中传入println或日志工具，以打印SQL，默认为空实现，sql: SQL语句，params
 * @property sqlAudit2 在lambda中传入println或日志工具，以打印SQL和执行耗时，默认为空实现，sql: SQL语句，elapsedTime: 整个方法执行耗时,单位ms
 */
data class FreshenConfig(
    val dataSource: DataSource,
    val keyGenerator: KeyGenerator = KeyGenerator.NONE,
    val logicDelete: LogicDelete = LogicDelete.Disable,
    val optimisticLock: OptimisticLock = OptimisticLock.Disable,
    val tablePrefix: String? = null,
    val enabledUnderscoreToCamelCase: Boolean = true,
    val kTypeAndJDBCTypeMap: Map<KClass<*>, JDBCType> = defaultKTypeAndJDBCTypeMap,
    val sqlAudit1: (sql: String, params: Array<Any?>) -> Unit = { _, _ -> },
    val sqlAudit2: (sql: String, params: Array<Any?>, elapsedTime: Long) -> Unit = { _, _, _ -> }
)

/**
 * 默认的kType和JDBCType的映射器
 */
val defaultKTypeAndJDBCTypeMap: Map<KClass<*>, JDBCType> = mapOf(
    Int::class to JDBCType.INTEGER,
    Long::class to JDBCType.BIGINT,
    SHORT::class to JDBCType.SMALLINT,
    Byte::class to JDBCType.TINYINT,
    Float::class to JDBCType.FLOAT,
    Double::class to JDBCType.DOUBLE,
    Boolean::class to JDBCType.BOOLEAN,
    Char::class to JDBCType.CHAR,
    String::class to JDBCType.VARCHAR,
    LocalDate::class to JDBCType.DATE,
    LocalDateTime::class to JDBCType.TIMESTAMP,
    Date::class to JDBCType.TIMESTAMP,
    LocalTime::class to JDBCType.TIME,
    BigDecimal::class to JDBCType.DECIMAL,
    BigInteger::class to JDBCType.BIGINT,
)

/**
 * 配置乐观锁
 *
 * @property columnName 乐观锁列名
 */
sealed class OptimisticLock(
    open val columnName: String
) {
    /**
     * 不启用乐观锁
     * 设置此值是为了避免写出[String?]这样的类型
     * 本库不会用到这3个值
     */
    data object Disable : OptimisticLock("none")

    /**
     * 启用乐观锁
     *
     * @param columnName 乐观锁列名
     */
    class Enable(
        override val columnName: String,
    ) : OptimisticLock(columnName)
}

/**
 * 配置逻辑删除
 *
 * @property columnName 逻辑删除列名
 * @property normalValue 正常值
 * @property deletedValue 删除值
 */
sealed class LogicDelete(
    open val columnName: String,
    open val normalValue: Any,
    open val deletedValue: Any
) {
    /**
     * 不启用逻辑删除
     * 设置此值是为了避免写出[String?]这样的类型
     * 本库不会用到这3个值
     */
    data object Disable : LogicDelete("none", -1, -2)

    /**
     * 启用逻辑删除
     *
     * @param columnName 逻辑删除列名
     * @param normalValue 正常值
     * @param deletedValue 删除值
     */
    data class Enable(
        override val columnName: String,
        override val normalValue: Any,
        override val deletedValue: Any
    ) : LogicDelete(columnName, normalValue, deletedValue)
}

/**
 * 主键生成器
 * * 由于SnowflakeId和FlexId在批量插入时100%复现id重复
 * * 对于单个插入，我没试过，但理论上不会重复
 * * 批量插入id重复，是因为id是foreach遍历时生成的，时间间隔太短，所以id会重复
 * * 所以只能用UUID，但是UUID生成的是字符串，不是整型
 * * 所以约等于Freshen，不支持自动生成主键
 */
enum class KeyGenerator {
    /**
     * 默认行为，什么也不做，需要你显式地设置主键的值
     */
    NONE,

    /**
     * 设置数据库自增
     */
    AUTO,

    /**
     * 使用UUID生成主键
     */
    UUID,

    /**
     * 使用雪花算法生成主键
     */
    SNOWFLAKE_ID,

    /**
     * 使用FlexId生成主键
     */
    FLEX_ID
}
