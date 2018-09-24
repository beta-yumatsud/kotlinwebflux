package com.darmaso.spring.kotlinwebflux.repository

import com.darmaso.spring.kotlinwebflux.domain.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserRepository: ReactiveCrudRepository<User, String>