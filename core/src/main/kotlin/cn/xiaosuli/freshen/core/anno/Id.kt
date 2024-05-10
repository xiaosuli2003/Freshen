package cn.xiaosuli.freshen.core.anno

/**
 * 主键注解
 *
 * @property value 数据库主键名
 */
@MustBeDocumented
@Target(AnnotationTarget.PROPERTY)
annotation class Id(
    val value: String = "id"
)
