package cn.xiaosuli.freshen.core.anno

/**
 * 列注解
 *
 * @property value 列名
 */
@MustBeDocumented
@Target(AnnotationTarget.PROPERTY)
annotation class Column(
    val value: String
)
