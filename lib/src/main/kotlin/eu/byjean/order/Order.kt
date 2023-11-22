package eu.byjean.order

import eu.byjean.cart.CartId
import eu.byjean.product.ProductId
import eu.byjean.user.User
import eu.byjean.user.UserId
import java.util.*


data class OrderId(val value: String = UUID.randomUUID().toString())


data class OrderLine(
        val productId: ProductId,
        val description: String,
        val quantity: Int,
        val unitPrice: Int,
) {
    val lineTotal = quantity * unitPrice
}

data class Order(
        val id: OrderId,
        val userId: UserId,
        val cartId: CartId,
        val lines: Set<OrderLine>,
        val discountAmout: Int
)

