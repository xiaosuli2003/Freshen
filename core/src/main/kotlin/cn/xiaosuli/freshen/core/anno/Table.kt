package cn.xiaosuli.freshen.core.anno

/**
 * 表注解
 *
 * @property value 表名
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class Table(
    val value: String,
)
