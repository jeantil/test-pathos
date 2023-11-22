package eu.byjean.cart

import eu.byjean.product.ProductId
import eu.byjean.user.UserId
import java.util.*

enum class CartStatus { New, Active, Ordered }

data class CartId(val value: String = UUID.randomUUID().toString())

data class Item(val productId: ProductId, val quantity: Int, val unitPrice: Int) {
    val itemTotal = quantity * unitPrice
}

data class Cart internal constructor(
        val id: CartId, val ownerId: UserId,
        val items: Set<Item> = setOf(), val status: CartStatus = CartStatus.New,
) {
    val total = items.fold(0) { acc, item -> acc + item.itemTotal }
    fun addItem(item: Item) = copy(items = items.plus(item), status = CartStatus.Active)
    fun removeItem(item: Item) = copy(items = items.minus(item))
    fun order() = copy(status = CartStatus.Ordered)
    fun removeItemWithProductId(productId: ProductId): Pair<Cart, Item> {
        val item = items.find { it.productId == productId }!!
        return removeItem(item) to item
    }

    fun emtpy(): Cart = copy(items= setOf())
}