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
 * @param R 要反射构造的对象类型
 * @return R?
 */
inline fun <reified R> ResultSet.getObject(): R {
    val noArgsConstructor = R::class.constructors.firstOrNull { it.parameters.isEmpty() }
    return if (noArgsConstructor == null) {
        // 走有参构造对象
        getObjForHasArgsConstructor<R>()
    } else {
        // 走无参构造对象
        getObjForNoArgsConstructor<R>()
    }
}

/**
 * 反射构造对象（适用于有参构造器）
 *
 * @param R 要反射构造的对象类型
 * @return R?
 */
inline fun <reified R> ResultSet.getObjForHasArgsConstructor(): R {
    val hasArgsConstructor = R::class.constructors.firstOrNull()
    val size = hasArgsConstructor!!.parameters.size
    val params = Array<Any>(size) {}
    hasArgsConstructor.parameters.forEachIndexed { index, parameter ->
        val name = parameter.name!!
        // TODO: 这里的映射还需要修改，或添加
        when (parameter.type.classifier) {
            Int::class -> params[index] = getInt(name.toUnderscore())
            Long::class -> params[index] = getLong(name.toUnderscore())
            String::class -> params[index] = getString(name.toUnderscore())
            Boolean::class -> params[index] = getBoolean(name.toUnderscore())
            Byte::class -> params[index] = getByte(name.toUnderscore())
            Float::class -> params[index] = getFloat(name.toUnderscore())
            Double::class -> params[index] = getDouble(name.toUnderscore())
            BigDecimal::class -> params[index] = getBigDecimal(name.toUnderscore())
            Short::class -> params[index] = getShort(name.toUnderscore())
            else -> params[index] = getObject(name.toUnderscore())
        }
    }
    return hasArgsConstructor.call(*params)
}

/**
 * 反射构造对象（适用于无参构造器）
 *
 * @param R 要反射构造的对象类型
 * @return R?
 */
inline fun <reified R> ResultSet.getObjForNoArgsConstructor(): R {
    val entity = R::class.constructors.firstOrNull { it.parameters.isEmpty() }!!.call()
    R::class.members.filterIsInstance<KProperty1<R, *>>().forEach { kProperty1 ->
        val kmp = kProperty1 as KMutableProperty1<R, *>
        // TODO: 这里的映射还需要修改，或添加
        when (kProperty1.returnType.classifier) {
            Int::class -> kmp.setter.call(entity, getInt(kProperty1.name.toUnderscore()))
            Long::class -> kmp.setter.call(entity, getLong(kProperty1.name.toUnderscore()))
            String::class -> kmp.setter.call(entity, getString(kProperty1.name.toUnderscore()))
            Boolean::class -> kmp.setter.call(entity, getBoolean(kProperty1.name.toUnderscore()))
            Byte::class -> kmp.setter.call(entity, getByte(kProperty1.name.toUnderscore()))
            Float::class -> kmp.setter.call(entity, getFloat(kProperty1.name.toUnderscore()))
            Double::class -> kmp.setter.call(entity, getDouble(kProperty1.name.toUnderscore()))
            BigDecimal::class -> kmp.setter.call(entity, getBigDecimal(kProperty1.name.toUnderscore()))
            Short::class -> kmp.setter.call(entity, getShort(kProperty1.name.toUnderscore()))
            else -> kmp.setter.call(entity, getObject(kProperty1.name.toUnderscore()))
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