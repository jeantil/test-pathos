package eu.byjean.user

import java.sql.Connection

interface UserRepository {
    fun findById(userId: UserId): User?
}
//
//class SqlUserRepository(connection: Connection) : UserRepository {
//}
class InMemoryUserRepository(): UserRepository {
    private val store : MutableMap<UserId, User> = mutableMapOf()
    override fun findById(userId: UserId): User? {
        return store[userId]
    }
    fun save(user: User){
        store[user.id]=user
    }
}