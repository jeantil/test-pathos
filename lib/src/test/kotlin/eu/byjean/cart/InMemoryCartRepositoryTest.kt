package eu.byjean.cart

import eu.byjean.product.ProductId
import eu.byjean.user.UserId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*

class InMemoryCartRepositoryTest {
    val repository = InMemoryCartRepository()


    @Nested
    inner class CreateCart {
        @Test
        fun `should throw when called more than once with the same id`() {
            val cart = Cart(CartId(), UserId())
            repository.createCart(cart)

            expectThrows<CartAlreadyExists> {
                repository.createCart(cart)
            }
        }
    }

    @Nested
    inner class UpdateCart {
        @Test
        fun `should throw when called for a missing id`() {
            val cart = Cart(CartId(), UserId())

            expectThrows<CartDoesNotExist> {
                repository.updateCart(cart)
            }
        }

        @Test
        fun `should update cart when it exists`() {
            val cart = Cart(CartId(), UserId())
            repository.createCart(cart)

            repository.updateCart(cart.addItem(Item(ProductId(), 1, 100)))

            val actualCart = repository.findById(cart.id)!!
            expectThat(actualCart) {
                get { items } hasSize 1
                get { total } isNotEqualTo 0
            }
        }
    }

    @Nested
    inner class FindById {
        @Test
        fun `should return nothing if repository empty`() {
            val cart = repository.findById(CartId())

            expectThat(cart).isNull()
        }

        fun `should return nothing if no cart exists with id`() {
            repository.createCart(Cart(CartId(), UserId()))

            val cart = repository.findById(CartId())

            expectThat(cart).isNull()
        }

        @Test
        fun `should return Cart if cart exists with id for user `() {
            val id = CartId()
            val userId = UserId()
            val cart = Cart(id, userId)
            repository.createCart(cart)

            val actualCart = repository.findById(id)

            expectThat(actualCart).isEqualTo(cart)
        }
    }

    @Nested
    inner class findByIdAndUser {
        @Test
        fun `should return nothing if repository empty`() {
            val cart = repository.findByIdAndUser(CartId(), UserId())

            expectThat(cart).isNull()
        }

        fun `should return nothing if no cart exists with id`() {
            repository.createCart(Cart(CartId(), UserId()))

            val cart = repository.findByIdAndUser(CartId(), UserId())

            expectThat(cart).isNull()
        }

        @Test
        fun `should return nothing if cart exists with id for another user`() {
            val id = CartId()
            repository.createCart(Cart(id, UserId()))

            val cart = repository.findByIdAndUser(id, UserId())

            expectThat(cart).isNull()
        }

        @Test
        fun `should return Cart if cart exists with id for user `() {
            val id = CartId()
            val userId = UserId()
            val cart = Cart(id, userId)
            repository.createCart(cart)

            val actualCart = repository.findByIdAndUser(id, userId)

            expectThat(actualCart).isEqualTo(cart)
        }
    }

    @Nested
    inner class FindAllByProductId {
        @Test
        fun `should return Carts containing an item for that productId`() {
            val productId = ProductId()
            val otherProductId = ProductId()
            val cart1 = givenCartFor(productId, quantity = 1)
            val cart2 = givenCartFor(otherProductId, quantity = 2)
            val cart3 = givenCartFor(productId, quantity = 3)

            val found = repository.findAllByProductId(productId)

            expectThat(found.toList())
                    .hasSize(2)
                    .containsExactly(cart1, cart3)

        }

        private fun givenCartFor(productId: ProductId, quantity: Int = 1): Cart {
            val cart = Cart(CartId(), UserId())
                    .addItem(Item(productId, 1, 100))
                    .order()
            return repository.createCart(cart)
        }
    }
}