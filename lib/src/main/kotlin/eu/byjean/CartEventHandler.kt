package eu.byjean

import eu.byjean.cart.Cart
import eu.byjean.cart.CartRepository
import eu.byjean.cart.Item
import eu.byjean.product.ProductId
import eu.byjean.product.ProductRepository
import eu.byjean.user.UserId
import eu.byjean.user.UserRepository


class CartEventHandler(
        val cartRepository: CartRepository,
        val userRepository: UserRepository,
        val productRepository: ProductRepository,
) {
    // RGPD stuff or something
    fun onUserDeleted(event: UserDeleted) {

    }

    // Planned obsolescence makes products disappear all the time
    fun onProductDeleted(event: ProductDeleted) {
        cartRepository.findAllByProductId(event.productId)
                .map {
                    it.removeItemWithProductId(event.productId)
                }.forEach { (cart, removedItem) ->
                    cartRepository.updateCart(cart)
                    notifyUserOfProductRemovedFromCart(cart, removedItem)
                }
    }

    private fun notifyUserOfProductRemovedFromCart(it: Cart, removedItem: Item) {
        val user = userRepository.findById(it.ownerId) ?: return
        val product = productRepository.findById(removedItem.productId) ?: return
        println(
                """
            Dear ${user.name},
            we have regret to inform you that we had to remove 
            ${product.description} 
            from your cart as the f**ng manufacture won't make any more
        """.trimIndent()
        )
    }
}

data class UserDeleted(val userId: UserId)
data class ProductDeleted(val productId: ProductId)