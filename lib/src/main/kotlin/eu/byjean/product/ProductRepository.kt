package eu.byjean.product

import java.sql.Connection

interface ProductRepository {
    fun createProduct(product: Product): Product
    fun updateProduct(product: Product): Product
    fun findById(productId: ProductId): Product?
}
//
//class SqlProductRepository(connection: Connection) : ProductRepository {
//}

class InMemoryProductRepository() : ProductRepository {
    private var store: MutableMap<ProductId, Product> = mutableMapOf()
    override fun createProduct(product: Product): Product {
        store.put(product.id, product)
        return product
    }

    override fun updateProduct(product: Product): Product {
        store.put(product.id, product)
        return product
    }

    override fun findById(productId: ProductId): Product? {
        return store[productId]
    }
}