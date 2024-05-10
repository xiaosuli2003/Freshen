package com.xiaosuli.test

class Demo : Base() {
    var id: Long? = null
    var userName: String? = null
    var password: String? = null
}

open class Base {
    var createBy = ""
    fun test2() = listOf("test2")
    fun test() = "test"
}

data class Test(var id: Long? = null, var name: String? = null){
    constructor(a:String):this(1,a)
}

fun main() {

}

inline fun <reified T> test() {
    //val list = T::class.members.filterIsInstance<KProperty1<T, *>>()?.
    /*Demo::class.memberProperties.forEach {
        println("name = ${it.name}; kType1 = ${it.returnType}; kType2 = ${it.returnType.classifier}")
    }*/
    //println("------------------------------")
    /*T::class.memberProperties.filterIsInstance<KProperty1<T, *>>().forEach {
        println("name = ${it.name}; kType1 = ${it.returnType}; kType2 = ${it.returnType.classifier}")
    }*/
}
