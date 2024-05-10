package cn.xiaosuli.freshen.core.utils

// fun KClass<*>.requireDataClass(): KClass<*> {
//     if (!this.isData) {
//         throw NoDataClassException("实体类必须是data class！")
//     }
//     return this
// }

// inline fun <reified T : Any> KClass<T>.newInstance(vararg args: Any?): T =
//     constructors.first().call(*args)

// inline fun <reified T : Any> KClass<T>.countProperties(): Int =
//     constructors.first().parameters.count()
//
// inline fun <reified T : Any> KClass<T>.getProperties(): List<ConstructorParam> =
//     constructors.first().parameters.map {
//         ConstructorParam(it.index, it.name!!, it.type)
//     }
