package com.xiaosuli.test

import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty1

class KotlinTests {

    data class Demo(val id: Int)

    fun <T, V> KProperty1<T, V>.test(value: V) = "$value"

    @Test
    fun test01() {
        Demo::id.test("")
    }

    @Test
    fun test02() {
        val list = mutableListOf<Int>()
        repeat(1000) {
            list.add(it)
        }
        list.forEach {
            log.info(System.currentTimeMillis().toString())
        }
    }
}