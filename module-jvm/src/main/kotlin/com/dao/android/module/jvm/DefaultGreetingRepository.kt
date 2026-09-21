package com.dao.android.module.jvm

import javax.inject.Inject

internal class DefaultGreetingRepository @Inject constructor() : GreetingRepository {
    override fun getGreeting(): String {
        return "Android"
    }
}
