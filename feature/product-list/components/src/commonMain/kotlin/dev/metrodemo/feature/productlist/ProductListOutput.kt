package dev.metrodemo.feature.productlist

/**
 * What the screen reports upwards. Features never know each other's navigation configs — Root
 * implements this and decides where to go.
 */
fun interface ProductListOutput {

    fun onProductSelected(productId: String)
}
