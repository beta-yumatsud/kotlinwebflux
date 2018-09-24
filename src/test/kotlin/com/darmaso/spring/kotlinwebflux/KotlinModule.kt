package com.darmaso.spring.kotlinwebflux

import org.mockito.Mockito
import kotlin.reflect.KClass

/**
 * 参考
 * https://qiita.com/ko2ic/items/9a0b76f54e9e5e203d77
 */
class KotlinModule {
    companion object {
        fun <T> any(): T {
            return Mockito.any()
                    ?: null as T
        }

        fun <T : Any>  any(type: KClass<T>): T {
            return Mockito.any(type.java)
        }

        fun <T> eq(value: T): T {
            return if (value != null)
                Mockito.eq(value)
            else
                null
                        ?: null as T
        }
    }
}