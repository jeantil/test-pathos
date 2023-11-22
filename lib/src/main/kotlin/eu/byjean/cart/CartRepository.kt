package eu.byjean.cart

import eu.byjean.product.ProductId
import eu.byjean.user.UserId
import java.lang.IllegalArgumentException
import java.sql.Connection

interface CartRepository {
    fun createCart(cart: Cart): Cart
    fun updateCart(cart: Cart): Cart
    fun findByIdAndUser(cartId: CartId, currentUserId: UserId): Cart?
    fun findById(cartId: CartId): Cart?
    fun findAllByProductId(productId: ProductId): Sequence<Cart>
}

//abstract class SqlCartRepository(connection: Connection) : CartRepository {
//    Todo("implement SQL connector")
//}

class CartDoesNotExist(cartId: CartId) : NoSuchElementException("Cart with id ${cartId} does not exist and can't be updated, use createCart first")
class CartAlreadyExists(cartId: CartId) : IllegalArgumentException("Cart with id ${cartId} already exists")

class InMemoryCartRepository() : CartRepository {
    private var store: MutableMap<CartId, Cart> = mutableMapOf()
    override fun createCart(cart: Cart): Cart {
        if (store[cart.id] != null) {
            throw CartAlreadyExists(cartId = cart.id)
        }
        store.put(cart.id, cart)
        return cart
    }

    override fun updateCart(cart: Cart): Cart {
        if (store[cart.id] == null) {
            throw CartDoesNotExist(cartId = cart.id)
        }
        store.put(cart.id, cart)
        return cart
    }

    override fun findByIdAndUser(cartId: CartId, ownerId: UserId): Cart? {
        return store[cartId]?.takeIf { it.ownerId == ownerId }
    }

    override fun findById(cartId: CartId): Cart? {
        return store[cartId]
    }

    override fun findAllByProductId(productId: ProductId): Sequence<Cart> {
        return store.values.asSequence().filter { it.items.any { it.productId == productId } }
    }
}