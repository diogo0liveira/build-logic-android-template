package com.dao.android.module.core

import com.dao.android.module.jvm.GreetingRepository
import javax.inject.Inject

public class GetGreeting @Inject constructor(private val repository: GreetingRepository) {
    public operator fun invoke(): String {
        return repository.getGreeting()
    }
}
