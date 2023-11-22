package eu.byjean

import eu.byjean.cart.Cart
import eu.byjean.cart.CartId
import eu.byjean.cart.CartRepository
import eu.byjean.cart.Item
import eu.byjean.product.Product
import eu.byjean.product.ProductId
import eu.byjean.product.ProductRepository
import eu.byjean.user.User
import eu.byjean.user.UserId
import eu.byjean.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CartEventHandlerTest {
    val cartRepository: CartRepository = mockk(relaxed = true)
    val userRepository: UserRepository = mockk()
    val productRepository: ProductRepository = mockk()
    val eventHandler = CartEventHandler(
            cartRepository,
            userRepository,
            productRepository,
    )

    @Test
    fun `should remove the product from carts and notify the users`() {
        val event = ProductDeleted(ProductId())

        val aCart = givenACartWithanItemForProduct(event.productId)
        every { cartRepository.findAllByProductId(event.productId) } returns sequenceOf(aCart)
        every { productRepository.findById(event.productId) } returns aProduct()
        every { userRepository.findById(aCart.ownerId) } returns aUser()


        eventHandler.onProductDeleted(event)

        verify { cartRepository.updateCart(aCart.emtpy()) }
    }

    private fun givenACartWithanItemForProduct(productId: ProductId): Cart =
            Cart(CartId(), UserId(), setOf(Item(productId, 1, 100)))


    private fun aProduct() = Product(ProductId(), "This product is soon to be discontinued", 100)
    private fun aUser() = User(UserId(), "Giant Fumble", 0)
}