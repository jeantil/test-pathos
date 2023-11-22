package eu.byjean.product

import java.util.*

data class ProductId(val value: String = UUID.randomUUID().toString())
data class Product(val id: ProductId, val description: String, val unitPrice: Int)