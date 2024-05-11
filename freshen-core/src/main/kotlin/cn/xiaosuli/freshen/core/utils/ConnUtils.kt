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
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import java.math.BigDecimal
import java.sql.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

/**
 *
 *  反射构造对象
 *
 * @param typeMap 属性（列）和JDBCType的映射
 * @param R 要反射构造的对象类型
 * @return R?
 */
inline fun <reified R> ResultSet.getObject(
    typeMap:Map<KProperty1<*, *>,JDBCType>?=null
): R {
    val noArgsConstructor = R::class.constructors.firstOrNull { it.parameters.isEmpty() }
    return if (noArgsConstructor == null) {
        // 走有参构造对象
        getObjForHasArgsConstructor<R>(typeMap)
    } else {
        // 走无参构造对象
        getObjForNoArgsConstructor<R>(typeMap)
    }
}

/**
 * 反射构造对象（适用于有参构造器）
 *
 * @param typeMap 属性（列）和JDBCType的映射
 * @param R 要反射构造的对象类型
 * @return R?
 */
inline fun <reified R> ResultSet.getObjForHasArgsConstructor(
    typeMap:Map<KProperty1<*, *>,JDBCType>?=null
): R {
    val hasArgsConstructor = R::class.constructors.firstOrNull()
    val size = hasArgsConstructor!!.parameters.size
    val params = Array<Any>(size) {}
    R::class.members.filterIsInstance<KProperty1<R, *>>().forEach { kProperty1 ->
        hasArgsConstructor.parameters.forEachIndexed { index, parameter ->
            if (parameter.name == kProperty1.name) {
                val name = parameter.name!!.toUnderscore()
                val jdbcType = typeMap?.get(kProperty1)?:FreshenRuntimeConfig.kTypeAndJDBCTypeMap[parameter.type.classifier]
                when(jdbcType){
                    JDBCType.BIT,JDBCType.BOOLEAN -> params[index] = getBoolean(name)
                    JDBCType.TINYINT-> params[index] = getByte(name)
                    JDBCType.SMALLINT-> params[index] = getShort(name)
                    JDBCType.INTEGER-> params[index] = getInt(name)
                    JDBCType.BIGINT-> params[index] = getLong(name)
                    JDBCType.FLOAT -> params[index] = getFloat(name)
                    JDBCType.DOUBLE -> params[index] = getDouble(name)
                    JDBCType.DECIMAL -> params[index] = getBigDecimal(name)
                    JDBCType.CHAR,JDBCType.VARCHAR,JDBCType.LONGVARCHAR ,JDBCType.NCHAR,JDBCType.NVARCHAR,JDBCType.LONGNVARCHAR-> params[index] = getString(name)
                    JDBCType.DATE -> params[index] = getDate(name).toLocalDate()
                    JDBCType.TIME -> params[index] = getTime(name).toLocalTime()
                    JDBCType.TIMESTAMP -> params[index] = getTimestamp(name).toLocalDateTime()
                    else-> params[index] = getObject(name)
                }
            }
        }
    }
    return hasArgsConstructor.call(*params)
}

/**
 * 反射构造对象（适用于无参构造器）
 *
 * @param typeMap 属性（列）和JDBCType的映射
 * @return R?
 */data class A(val a:String)
inline fun <reified R> ResultSet.getObjForNoArgsConstructor(
    typeMap:Map<KProperty1<*, *>,JDBCType>?=null
): R {
    val entity = R::class.constructors.firstOrNull { it.parameters.isEmpty() }!!.call()
    R::class.members.filterIsInstance<KProperty1<R, *>>().forEach { kProperty1 ->
        val name = kProperty1.name.toUnderscore()
        // 如果有传入typeMap，则使用typeMap中的值，否则使用默认的
        val jdbcType = typeMap?.get(kProperty1) ?:FreshenRuntimeConfig.kTypeAndJDBCTypeMap[kProperty1.returnType.classifier]
        val kmp = kProperty1 as KMutableProperty1<R, *>
        when (jdbcType) {
            JDBCType.BIT,JDBCType.BOOLEAN -> kmp.setter.call(entity, getBoolean(name))
            JDBCType.TINYINT-> kmp.setter.call(entity, getByte(name))
            JDBCType.SMALLINT-> kmp.setter.call(entity, getShort(name))
            JDBCType.INTEGER-> kmp.setter.call(entity, getInt(name))
            JDBCType.BIGINT-> kmp.setter.call(entity, getLong(name))
            JDBCType.FLOAT -> kmp.setter.call(entity, getFloat(name))
            JDBCType.DOUBLE -> kmp.setter.call(entity, getDouble(name))
            JDBCType.DECIMAL -> kmp.setter.call(entity, getBigDecimal(name))
            JDBCType.CHAR,JDBCType.VARCHAR,JDBCType.LONGVARCHAR ,JDBCType.NCHAR,JDBCType.NVARCHAR,JDBCType.LONGNVARCHAR-> kmp.setter.call(entity, getString(name))
            JDBCType.DATE -> kmp.setter.call(entity, getDate(name).toLocalDate())
            JDBCType.TIME -> kmp.setter.call(entity, getTime(name).toLocalTime())
            JDBCType.TIMESTAMP -> kmp.setter.call(entity, getTimestamp(name).toLocalDateTime())
            else -> kmp.setter.call(entity, getObject(name))
        }
    }
    return entity
}

/**
 * 给PreparedStatement设置参数
 *
 * @param params 参数列表
 */
fun PreparedStatement.setParams(params: List<PrepareStatementParam>?) {
    params?.forEach {
        when (it.type.returnType.classifier) {
            Int::class -> setInt(it.index, it.value as Int)
            Long::class -> setLong(it.index, it.value as Long)
            String::class -> setString(it.index, it.value as String)
            Boolean::class -> setBoolean(it.index, it.value as Boolean)
            Byte::class -> setByte(it.index, it.value as Byte)
            Float::class -> setFloat(it.index, it.value as Float)
            Double::class -> setDouble(it.index, it.value as Double)
            BigDecimal::class -> setBigDecimal(it.index, it.value as BigDecimal)
            Short::class -> setShort(it.index, it.value as Short)
            else -> setObject(it.index, it.value)
        }
    }
}

/**
 * 关闭连接，释放资源，执行SQL审计2
 *
 * @param statement Statement对象
 * @param resultSet ResultSet对象
 * @param sql SQL语句
 * @param start 方法开始时间
 */
@FreshenInternalApi
fun Connection.closeAndAudit(
    statement: PreparedStatement,
    resultSet: ResultSet,
    sql: String,
    params:List<PrepareStatementParam>?,
    start: Long
) {
    this.close(statement, resultSet)
    val end = System.currentTimeMillis()
    FreshenRuntimeConfig.sqlAudit2(sql, params,end - start)
}

/**
 * 关闭数据库连接
 *
 * @param stmt Statement对象
 * @param rs ResultSet对象
 */
fun Connection.close(stmt: Statement?, rs: ResultSet?) {
    try {
        rs?.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    try {
        stmt?.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    try {
        this.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}