package cz.helmisek.mvvmi.entity

import kategory.lenses

@lenses
data class Person(
        val firstName: String = "",
        val lastName: String = "",
        val age: String = "0"
)
