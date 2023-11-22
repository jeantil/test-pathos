package eu.byjean.user

import java.util.*


data class UserId(val value: String = UUID.randomUUID().toString())
data class User(val id: UserId, val name: String, val discountAmount: Int)