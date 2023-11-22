package eu.byjean.cart

import eu.byjean.product.ProductId
import eu.byjean.user.UserId
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class CartTest {
    @Test
    fun `a new cart should be empty`() {
        val emptyCart = Cart(CartId(), UserId())

        expectThat(emptyCart.total).isEqualTo(0)
        expectThat(emptyCart.items).isEmpty()
        expectThat(emptyCart.status).isEqualTo(CartStatus.New)
    }

    @Test
    fun `adding the first item of a cart makes it active`() {
        val emptyCart = Cart(CartId(), UserId())

        val cart = emptyCart.addItem(Item(ProductId(), 1, 100))

        expectThat(cart.total).isEqualTo(100)
        expectThat(cart.items).hasSize(1)
        expectThat(cart.status).isEqualTo(CartStatus.Active)
    }

    @Test
    fun `removing the last item of a cart makes it empty but still active`() {
        val item = Item(ProductId(), 1, 100)
        val cart = Cart(CartId(), UserId())
                .addItem(item)

        val actualCart = cart.removeItem(item)

        expectThat(actualCart.total).isEqualTo(0)
        expectThat(actualCart.items).isEmpty()
        expectThat(actualCart.status).isEqualTo(CartStatus.Active)
    }

    @Test
    fun `ordering a cart makes it ordered`() {
        val cart = Cart(CartId(), UserId())
                .addItem(Item(ProductId(), 1, 100))
                .order()

        expectThat(cart.total).isEqualTo(100)
        expectThat(cart.items).hasSize(1)
        expectThat(cart.status).isEqualTo(CartStatus.Ordered)
    }


}