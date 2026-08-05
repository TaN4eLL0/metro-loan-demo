package dev.metrodemo.feature.productlist

/**
 * Every feature declares its own scope marker.
 *
 * This is convention, not a hard requirement — a single shared `ScreenScope` for all four screens
 * was tried and it compiles: Metro only resolves bindings a graph actually exposes, so a
 * `@ContributesBinding(ScreenScope::class)` from another feature just sits there unreachable.
 *
 * Separate markers are kept anyway because they are what makes a contribution's target explicit,
 * and because nesting needs them regardless: the promo dialog's graph extension hangs off the
 * Summary graph and must have a scope of its own.
 */
abstract class ProductListScope private constructor()
