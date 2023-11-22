package eu.byjean.order

import eu.byjean.cart.CartAlreadyExists
import eu.byjean.cart.CartId
import java.lang.IllegalArgumentException
import java.sql.Connection

interface OrderRepository {
    fun createOrder(order: Order): Order
    fun findByCartId(cartId: CartId): Order?
}

//class SqlOrderRepository(connection: Connection) : OrderRepository {
//        TODO("Not yet implemented")
//}
class OrderAlreadyExists(orderId: OrderId) : IllegalArgumentException("Order with id ${orderId} already exists")

class InMemoryOrderRepository() : OrderRepository {
    private var store: MutableMap<OrderId, Order> = mutableMapOf()

    override fun createOrder(order: Order): Order {
        if (store[order.id] != null) {
            throw OrderAlreadyExists(orderId = order.id)
        }
        store.put(order.id, order)
        return order
    }

    override fun findByCartId(cartId: CartId): Order? {
        return store.values.find { it.cartId==cartId }
    }
}