package dev.metrodemo.core.common.di

/**
 * Scope marker for per-screen graph extensions.
 *
 * The application-wide scope is Metro's own `dev.zacsweers.metro.AppScope`; everything that must
 * live and die with a single Decompose component is scoped here instead.
 */
abstract class ScreenScope private constructor()
