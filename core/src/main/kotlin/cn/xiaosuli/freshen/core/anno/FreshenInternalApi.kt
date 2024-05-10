package cn.xiaosuli.freshen.core.anno

/**
 * 被此注解标注的类或函数为Freshen内部API，还请不要使用。
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class FreshenInternalApi
