package eu.byjean

import eu.byjean.cart.Cart
import eu.byjean.cart.CartId
import eu.byjean.cart.CartRepository
import eu.byjean.cart.Item
import eu.byjean.order.Order
import eu.byjean.order.OrderRepository
import eu.byjean.product.Product
import eu.byjean.product.ProductId
import eu.byjean.product.ProductRepository
import eu.byjean.user.User
import eu.byjean.user.UserId
import eu.byjean.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class OrderCartTest {

    val cartRepository = mockk<CartRepository>()
    val productRepository = mockk<ProductRepository>()
    val orderRepository = mockk<OrderRepository>()
    val userRepository = mockk<UserRepository>()

    val orderCart = OrderCart(
            cartRepo = cartRepository,
            productRepo = productRepository,
            orderRepo = orderRepository,
            userRepo = userRepository
    )

    @Test
    fun `should not convert a cart into an order when user doesn't exist`() {
        val userId = UserId()
        val cartId = CartId()

        every { userRepository.findById(userId) } returns null

        val order = orderCart.apply(cartId, userId)

        expectThat(order).isNull()

        verify(exactly = 0) { orderRepository.createOrder(any()) }

    }

    @Test
    fun `should not convert a cart into an order when cart doesn't exist`() {
        val userId = UserId()
        val cartId = CartId()

        every { userRepository.findById(userId) } returns aUser()
        every { cartRepository.findByIdAndUser(cartId, userId) } returns null

        val order = orderCart.apply(cartId, userId)

        expectThat(order).isNull()

        verify(exactly = 0) { orderRepository.createOrder(any()) }
    }


    @Test
    fun `should convert a cart into an order`() {
        val userId = UserId();
        val cartId = CartId();
        val slot = slot<Order>()
        val discountAmount = 10

        every { userRepository.findById(userId) } returns aUser(discountAmount = discountAmount)
        val cart = aCart()
        every { cartRepository.findByIdAndUser(cartId, userId) } returns cart
        cart.items.forEach {
            every { productRepository.findById(it.productId) } returns aProduct(userId = userId)
        }
        every { orderRepository.createOrder(capture(slot)) } answers { slot.captured }

        val order = orderCart.apply(cartId, userId)

        expectThat(order)
                .isNotNull()
                .and {
                    get(Order::cartId) isEqualTo (cartId)
                    get(Order::userId) isEqualTo (userId)
                    get(Order::discountAmout) isEqualTo (discountAmount)
                }
    }


    private fun aUser(discountAmount: Int = 10): User =
            User(UserId(), "name", discountAmount)

    private fun aProduct(userId: UserId): Product =
            Product(
                    ProductId(),
                    "The mechanical keyboard",
                    500
            )


    private fun aCart(): Cart =
            Cart(
                    CartId(),
                    UserId(),
                    setOf(
                            Item(
                                    ProductId(),
                                    1,
                                    400
                            )
                    )
            )

}