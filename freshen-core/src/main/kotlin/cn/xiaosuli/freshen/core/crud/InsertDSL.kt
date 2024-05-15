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

package cn.xiaosuli.freshen.core.crud

import cn.xiaosuli.freshen.core.FreshenRuntimeConfig
import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.anno.Id
import cn.xiaosuli.freshen.core.entity.KeyGenerator
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import cn.xiaosuli.freshen.core.keygen.FlexIDKeyGenerator
import cn.xiaosuli.freshen.core.keygen.SnowFlakeIDKeyGenerator
import cn.xiaosuli.freshen.core.keygen.UUIDKeyGenerator
import cn.xiaosuli.freshen.core.utils.closeAndAudit
import cn.xiaosuli.freshen.core.utils.column
import cn.xiaosuli.freshen.core.utils.setParams
import cn.xiaosuli.freshen.core.utils.table
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * 插入一条记录
 *
 * @param entity 要插入的实体
 * @param connection  数据库连接，默认为空，即不启用事务，如果传入，则使用事务
 * @return 受影响的行数
 */
inline fun <reified T : Any> insert(
    entity: T,
    connection: Connection? = null,
): Int {
    // 记录方法开始执行的时间
    val start = System.currentTimeMillis()
    // 获取实体中的主键属性集合
    val idProperty = T::class.findIdProperty()

    // 如果主键生成策略为Auto，则忽略该属性
    var finallyProperties = T::class.memberProperties.filter {
        it.findAnnotation<Id>()?.keyGenerator != KeyGenerator.AUTO
    }
    // 再继续排查没有@Id注解，但是有属性名为id的属性，如果主键生成策略为AUTO，则忽略属性
    finallyProperties = if (FreshenRuntimeConfig.keyGenerator == KeyGenerator.AUTO) {
        finallyProperties.filter {
            it.name != "id"
        }
    } else finallyProperties
    // 获取要插入的列名
    val columns = finallyProperties.joinToString(prefix = "(", postfix = ")") { it.column }
    // 设置单个 (?, ?, ?)
    val values = finallyProperties.joinToString(prefix = "(", postfix = ")") { "?" }
    // 存放预编译参数
    val params: MutableList<PrepareStatementParam> = mutableListOf()
    // 遍历实体类中的属性，将属性的type和值添加到params中
    entity::class.memberProperties.forEach { property ->
        // 将属性的类型转为KClass类型
        val type = property.returnType.classifier as KClass<*>
        // 如果属性是主键，则根据主键生成策略生成值
        if (idProperty.contains(property)) {
            when (property.findKeyGenerator()) {
                KeyGenerator.NONE -> {
                    val value = property.getter.call(entity)
                    params.add(PrepareStatementParam(type, value))
                }

                KeyGenerator.UUID -> {
                    val generate = UUIDKeyGenerator().generate()
                    params.add(PrepareStatementParam(String::class, generate))
                    Thread.sleep(50)
                }

                KeyGenerator.SNOWFLAKE_ID -> {
                    val generate = SnowFlakeIDKeyGenerator().generate()
                    params.add(PrepareStatementParam(Long::class, generate))
                }

                KeyGenerator.FLEX_ID -> {
                    val generate = FlexIDKeyGenerator().generate()
                    params.add(PrepareStatementParam(Long::class, generate))
                }

                KeyGenerator.AUTO -> {}
            }
        } else {
            // 如果属性不是主键，则直接获取属性的值
            val value = property.getter.call(entity)
            params.add(PrepareStatementParam(type, value))
        }
    }
    // 拿到最终拼接好的sql语句
    val sql = "insert into ${T::class.table} $columns values $values"
    // 执行insert语句，返回受影响的行数
    return executeUpdate(sql, params.toTypedArray(), start, connection)
}

/**
 * 插入多条记录
 *
 * @param entities 要插入的实体集合
 * @return 受影响的行数
 */
inline fun <reified T : Any> insertBatch(vararg entities: T): Int = insertBatch(entities.toList())

/**
 * 插入多条记录
 * * TODO: 这里的批量插入是伪批量，即在同一个事务中循环执行插入方法。
 *
 * @param entities 要插入的实体集合
 * @return 受影响的行数
 */
inline fun <reified T : Any> insertBatch(entities: List<T>, connection: Connection? = null): Int {
    // 如果集合为空，抛出异常
    if (entities.isEmpty()) throw IllegalArgumentException("批量插入的实体集合不能为空！")
    val enabledTransaction = connection != null
    val conn = connection ?: FreshenRuntimeConfig.dataSource.connection
    return if (enabledTransaction) {
        var rows = 0
        entities.forEach {
            rows += insert(it, conn)
        }
        rows
    } else {
        conn.autoCommit = false
        var rows = 0
        try {
            entities.forEach {
                rows += insert(it, conn)
            }
        } catch (e: Exception) {
            conn.rollback()
            throw e
        } finally {
            conn.commit()
            conn.close()
        }
        rows
    }
}

/**
 * 获取实体类中的主键属性
 *
 * @return 主键属性
 */
fun <T : Any> KClass<T>.findIdProperty(): List<KProperty1<T, *>> {
    val idProperties = mutableListOf<KProperty1<T, *>>()
    memberProperties.forEach { property ->
        if (property.findAnnotation<Id>() != null) {
            idProperties.add(property)
        }
    }
    idProperties.add(memberProperties.firstOrNull { it.name == "id" } as KProperty1<T, *>)
    return idProperties
}

/**
 * 获取实体类主键对应的keyGenerator
 *
 * @return 主键属性
 */
fun <T : Any> KProperty1<T, *>.findKeyGenerator(): KeyGenerator {
    val idAnno = findAnnotation<Id>()
    if (idAnno != null) {
        return idAnno.keyGenerator
    }
    return FreshenRuntimeConfig.keyGenerator
}

/**
 * 执行增删改方法
 *
 * @param sql SQL语句
 * @param params 参数列表
 * @param start 方法开始执行的时间
 * @param connection 数据库连接，默认为空，即不启用事务，如果传入，则使用事务
 * @return 受影响的行数
 */
@FreshenInternalApi
@Suppress("SqlSourceToSinkFlow")
fun executeUpdate(
    sql: String,
    params: Array<PrepareStatementParam>,
    start: Long,
    connection: Connection? = null
): Int {
    val enableTransaction = connection != null
    FreshenRuntimeConfig.sqlAudit1(sql, params)
    val conn = connection ?: FreshenRuntimeConfig.dataSource.connection
    val statement = conn.prepareStatement(sql)
    statement.setParams(params)
    val rows = statement.executeUpdate()
    if (!enableTransaction) {
        conn.closeAndAudit(statement, sql = sql, params = params, start = start)
    } else {
        FreshenRuntimeConfig.sqlAudit2(sql, params, System.currentTimeMillis() - start)
    }
    return rows
}