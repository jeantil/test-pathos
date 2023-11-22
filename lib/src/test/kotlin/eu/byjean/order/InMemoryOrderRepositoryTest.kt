package eu.byjean.order

import eu.byjean.cart.CartId
import eu.byjean.product.ProductId
import eu.byjean.user.UserId
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class InMemoryOrderRepositoryTest {
    val repository = InMemoryOrderRepository()
    private val faker = Faker()

    @Nested
    inner class FindOrderById {
        @Test
        fun `should return nothing if repository empty`() {
            val order = repository.findByCartId(CartId())

            expectThat(order).isNull()
        }

        @Test
        fun `should return order by id`() {
            val order = Order(
                    OrderId(),
                    UserId(),
                    CartId(),
                    setOf(
                            OrderLine(
                                    ProductId(),
                                    faker.food.descriptions(),
                                    faker.random.nextInt(),
                                    faker.random.nextInt(),
                            )
                    ),
                    faker.random.nextInt()
            )
            repository.createOrder(order)
            val actualOrder = repository.findByCartId(order.cartId)

            expectThat(actualOrder).isEqualTo(order)
        }
    }

    @Nested
    inner class CreateOrder {
        @Test
        fun `should throw when called more than once with the same id`() {
            val order = Order(
                    OrderId(),
                    UserId(),
                    CartId(),
                    setOf(
                            OrderLine(
                                    ProductId(),
                                    faker.food.descriptions(),
                                    faker.random.nextInt(),
                                    faker.random.nextInt(),
                            )
                    ),
                    faker.random.nextInt()
            )
            repository.createOrder(order)

            expectThrows<OrderAlreadyExists> {
                repository.createOrder(order)
            }
        }
    }
}