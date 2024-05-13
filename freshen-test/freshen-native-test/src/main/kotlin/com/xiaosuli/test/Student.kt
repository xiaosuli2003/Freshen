package com.xiaosuli.test

import cn.xiaosuli.freshen.core.anno.Column
import cn.xiaosuli.freshen.core.anno.Id
import cn.xiaosuli.freshen.core.anno.Table
import java.time.LocalDateTime

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

@Table("tb_student")
data class Student2 (
    @Id("id22")
    val id: Long,
    @Column("name33")
    val name: String,
    val gender: String,
    val birthday: LocalDateTime,
    val phoneNumber: String,
    val address: String? = null
)