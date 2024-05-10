package com.xiaosuli.test

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