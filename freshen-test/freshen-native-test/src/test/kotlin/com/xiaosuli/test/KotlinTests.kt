package com.xiaosuli.test

import cn.xiaosuli.freshen.core.utils.column
import org.junit.jupiter.api.Test

class KotlinTests {
    @Test
    fun test01() {
        log.info(Student2::id.column)
        log.info(Student2::name.column)
        log.info(Student2::gender.column)
    }
}