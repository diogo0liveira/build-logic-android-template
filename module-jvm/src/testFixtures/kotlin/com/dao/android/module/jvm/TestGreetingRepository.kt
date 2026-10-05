package com.dao.android.module.jvm

class TestGreetingRepository : GreetingRepository {
    override fun getGreeting(): String {
        return "Android"
    }
}
