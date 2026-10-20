package dev.metrodemo.feature.productlist

/**
 * One scope marker per feature. A single shared marker for all screens also compiles, because Metro
 * only resolves bindings a graph actually exposes, but separate markers keep a contribution's target
 * explicit and nesting needs them anyway: the promo dialog graph hangs off the Summary graph.
 */
abstract class ProductListScope private constructor()
