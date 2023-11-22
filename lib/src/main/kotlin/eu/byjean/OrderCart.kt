package eu.byjean

import eu.byjean.cart.Cart
import eu.byjean.cart.CartId
import eu.byjean.cart.CartRepository
import eu.byjean.order.Order
import eu.byjean.order.OrderId
import eu.byjean.order.OrderLine
import eu.byjean.order.OrderRepository
import eu.byjean.product.ProductRepository
import eu.byjean.user.User
import eu.byjean.user.UserId
import eu.byjean.user.UserRepository
import java.lang.IllegalStateException


class OrderCart(
        private val cartRepo: CartRepository,
        private val productRepo: ProductRepository,
        private val orderRepo: OrderRepository,
        private val userRepo: UserRepository,
) {
    fun apply(cartId: CartId, currentUserId: UserId): Order? {
        val user = userRepo.findById(userId = currentUserId)
                ?: return null
        val cart = cartRepo.findByIdAndUser(cartId, currentUserId)
                ?: return null

        val orderLines = cart.items.map {
            val product = productRepo.findById(it.productId)
                    ?: throw IllegalStateException("Trying to order a cart which references a product that doesn't exist anymore !")
            OrderLine(
                    productId = it.productId,
                    description = product.description,
                    quantity = it.quantity,
                    unitPrice = it.unitPrice
            )
        }.toSet()
        val order = Order(
                id = OrderId(),
                userId = currentUserId,
                cartId = cartId,
                lines = orderLines,
                discountAmout = user.discountAmount
        )
        return orderRepo.createOrder(order)
    }
}