package cn.xiaosuli.freshen.core.entity

import javax.sql.DataSource

/**
 * Freshen 配置类
 * sqlAudit1和sqlAudit2区别是前者执行时机不一样，
 * sqlAudit1拿不到最终的执行耗时，但是优点是就算执行prepareStatement发生异常，也会打印SQL
 *
 * @property dataSource 数据源，必传
 * @property keyStrategy 主键生成策略，默认为None（用户自行处理）
 * @property logicDelete 逻辑删除配置，默认为不启用
 * @property optimisticLock 乐观锁配置，默认为不启用
 * @property tablePrefix 设置表名的统一前缀，默认为空
 * @property enabledUnderscoreToCamelCase 是否开启下划线驼峰转换，默认启用，会同时作用于表名和字段名
 * @property sqlAudit1 在lambda中传入println或日志工具，以打印SQL，默认为空实现，sql: SQL语句
 * @property sqlAudit2 在lambda中传入println或日志工具，以打印SQL和执行耗时，默认为空实现，sql: SQL语句，elapsedTime: 整个方法执行耗时,单位ms
 */
data class FreshenConfig(
    val dataSource: DataSource,
    val keyStrategy: KeyStrategy = KeyStrategy.None,
    val logicDelete: LogicDelete = LogicDelete.Disable,
    val optimisticLock: OptimisticLock = OptimisticLock.Disable,
    val tablePrefix: String? = null,
    val enabledUnderscoreToCamelCase: Boolean = true,
    val sqlAudit1: (sql: String,List<PrepareStatementParam>) -> Unit = {_,_->},
    val sqlAudit2: (sql: String, List<PrepareStatementParam>?,elapsedTime: Long) -> Unit = { _,_, _ -> }
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
    class Enable(
        override val columnName: String,
        override val normalValue: Any,
        override val deletedValue: Any
    ) : LogicDelete(columnName, normalValue, deletedValue)
}

/**
 * 配置主键生成策略
 *
 * @property keyGenerator 指定主键生成器
 */
sealed class KeyStrategy(
    open val keyGenerator: KeyGenerator
) {
    /**
     * 手动设置id
     */
    data object None : KeyStrategy(KeyGenerator.NONE)

    /**
     * 数据库自增
     */
    data object Anto : KeyStrategy(KeyGenerator.NONE)

    /**
     * 通过 KeyGenerator 生成器生成。
     *
     * @param keyGenerator 主键生成器
     */
    class Generator(
        override val keyGenerator: KeyGenerator
    ) : KeyStrategy(keyGenerator)
}

/**
 * 主键生成器
 *
 * @property value 值
 */
enum class KeyGenerator(
    private val value: String
) {
    /**
     * 默认行为，不使用生成器
     * 设置此值是为了避免写出[KeyGenerator?]这样的类型
     * 本库不会用到这3个值
     */
    NONE("none"),

    /**
     * 使用UUID生成主键
     */
    UUID("uuid"),

    /**
     * 使用雪花算法生成主键
     */
    SNOWFLAKE_ID("snowFlakeId")
}