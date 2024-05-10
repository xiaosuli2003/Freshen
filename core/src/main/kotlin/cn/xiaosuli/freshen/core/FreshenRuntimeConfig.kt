package cn.xiaosuli.freshen.core

import cn.xiaosuli.freshen.core.anno.FreshenInternalApi
import cn.xiaosuli.freshen.core.entity.KeyStrategy
import cn.xiaosuli.freshen.core.entity.LogicDelete
import cn.xiaosuli.freshen.core.entity.OptimisticLock
import cn.xiaosuli.freshen.core.entity.PrepareStatementParam
import javax.sql.DataSource

/**
 * Freshen 运行时配置类
 * 使用internal关键字，避免外部调用
 * 如果在执行CRUD前，调用了【runFreshen】方法，那么理论上就不会发生空指针异常
 */
@FreshenInternalApi
object FreshenRuntimeConfig {
    /**
     * 数据源，运行时赋值
     */
    lateinit var dataSource: DataSource

    /**
     * 主键生成策略
     */
    var keyStrategy: KeyStrategy = KeyStrategy.None

    /**
     * 逻辑删除配置
     */
    var logicDelete: LogicDelete = LogicDelete.Disable

    /**
     * 乐观锁配置
     */
    var optimisticLock: OptimisticLock = OptimisticLock.Disable

    /**
     * 表名前缀
     */
    var tablePrefix: String? = null

    /**
     * 是否开启驼峰转下划线
     */
    var enabledUnderscoreToCamelCase: Boolean = true

    /**
     * SQL审计1
     * 和sqlAudit2区别：执行时机不同，该方法是在执行SQL前执行，
     * 就算执行sql时报错，也会执行，方便确定sql拼接是否有误。
     * 缺点是拿不到执行耗时
     */
    var sqlAudit1: (sql: String,List<PrepareStatementParam>) -> Unit = {_,_->}

    /**
     * SQL审计2
     * 和sqlAudit1区别：执行时机不同，该方法是在执行SQL后执行，
     * 能拿到执行耗时，缺点是当执行sql时报错，不会执行
     */
    var sqlAudit2: (sql:String,List<PrepareStatementParam>?,elapsedTime: Long) -> Unit = { _, _,_ -> }

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