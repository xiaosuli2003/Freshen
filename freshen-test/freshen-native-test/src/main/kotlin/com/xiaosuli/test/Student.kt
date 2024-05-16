package com.xiaosuli.test

import cn.xiaosuli.freshen.core.anno.Table
import java.time.LocalDateTime

@Table("tb_student")
class Student {
    var id: Long? = null
    var name: String? = null
    var gender: String? = null
    var birthday: LocalDateTime? = null
    var phoneNumber: String? = null
    var address: String? = null
    override fun toString(): String {
        return "Student(id=$id, name=$name, gender=$gender, birthday=$birthday, phoneNumber=$phoneNumber, address=$address)"
    }
}

data class Student2(
    val id: Long,
    val name: String,
    val gender: String,
    val birthday: LocalDateTime,
    val phoneNumber: String,
    val address: String? = null
)