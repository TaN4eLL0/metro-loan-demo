# metro-loan-demo

A deliberately small KMP app whose only purpose is to evaluate **[Metro DI](https://zacsweers.github.io/metro/)**
against the architecture we actually use in `android-mx`: multi-module features, shared Decompose
components, Compose on Android, native SwiftUI on iOS.

Versions mirror `android-mx` so conclusions transfer: Kotlin 2.3.21, Compose Multiplatform 1.9.3,
Decompose 3.2.2.

Metro is pinned to **1.1.1**, not the newest 1.4.0: from 1.2.0 on, Metro's Kotlin/Native runtime
klib is built with Kotlin 2.4.0, and a 2.3.21 Kotlin/Native compiler cannot read it. Android builds
fine on 1.4.0 — the limit is iOS only. The annotation surface of 1.1.1 is identical to 1.4.0 for
everything this experiment uses. Details in [PLAN.md](PLAN.md#7-журнал-находок).

See [PLAN.md](PLAN.md) for the full experiment plan and the P1–P5 stages.

## Where to look for Metro

Read these six files in order and you have seen the whole DI story.

| File | What it demonstrates |
| --- | --- |
| [AppGraph.kt](shared/src/commonMain/kotlin/dev/metrodemo/shared/di/AppGraph.kt) | The one app-wide graph. It lists no modules, no bindings and no screen graphs. |
| [FakeProductsRepository.kt](core/data/src/commonMain/kotlin/dev/metrodemo/core/data/FakeProductsRepository.kt) | `@ContributesBinding` — reaches `AppGraph` across a module boundary without being named anywhere. |
| [StorageBindings.kt](core/data/src/commonMain/kotlin/dev/metrodemo/core/data/storage/StorageBindings.kt) | `@BindingContainer` with two `@Named` implementations of one interface. |
| [ProductListGraph.kt](feature/product-list/components/src/commonMain/kotlin/dev/metrodemo/feature/productlist/ProductListGraph.kt) | The per-screen graph: `@GraphExtension` + `@ContributesTo(AppScope)` on its factory, with `@Provides` runtime inputs. |
| [SummaryGraph.kt](feature/summary/components/src/commonMain/kotlin/dev/metrodemo/feature/summary/SummaryGraph.kt) | A graph extension of a graph extension — the promo dialog hangs off the Summary graph. |
| [DefaultRootComponent.kt](shared/src/commonMain/kotlin/dev/metrodemo/shared/DefaultRootComponent.kt) | The only holder of the graph: `asContribution<X.Factory>()` per screen, plus all the navigation. |

Supporting pieces: [SubmitApplicationUseCase.kt](core/domain/src/commonMain/kotlin/dev/metrodemo/core/domain/SubmitApplicationUseCase.kt)
injects a `Set<AnalyticsSink>` whose two contributors live in different modules;
[AppGraphAccessors.kt](shared/src/commonMain/kotlin/dev/metrodemo/shared/di/AppGraphAccessors.kt)
is the typed entry point for Android and iOS, and
[IosEntry.kt](shared/src/iosMain/kotlin/dev/metrodemo/shared/di/IosEntry.kt) is the single function
Swift calls — there is no `get<T>()` on the Swift side to mistype.

Metro is applied per module via `alias(libs.plugins.metro)` rather than from the convention plugin —
see finding 2 in [PLAN.md](PLAN.md#7-журнал-находок) for why.

## Screens and parameters

```
ProductList ──productId──▶ Calculator ──LoanDraft──▶ Summary ──applicationId+draft──▶ Result
                              ▲                         │ childSlot: promo dialog
                              └── edited draft back ─────┘   (code + discount back via callback)

Result: [Nuevo préstamo] → replaceAll(ProductList)
        [Repetir]        → replaceAll(ProductList, Calculator(prefill = draft))
```

Features never depend on each other: each exposes an `Output` interface and Root decides where to go.

## Module layout

```
core/common          models, formatters, scope markers
core/data            repository interfaces + fakes (contributed bindings)
core/domain          use cases
feature/<name>/components   Decompose component contract + impl + its screen graph
feature/<name>/ui           Compose Multiplatform screen for that component
shared               RootComponent, AppGraph, iOS framework (no Compose)
androidApp           Compose host
```

Dependency rules, deliberately the same as `android-mx`:

- features never depend on each other; they expose callbacks and Root routes between them
- `feature/*/ui` depends only on its own `components`
- `shared` stays Compose-free so the iOS framework does not drag Compose in
- iOS uses SwiftUI, so it consumes `*/components` and never `*/ui`

## Running

Android:

```bash
./gradlew :androidApp:installDebug
```

iOS — open `iosApp/iosApp.xcodeproj` and run the `iosApp` scheme. A build phase invokes
`:shared:embedAndSignAppleFrameworkForXcode`, so the Kotlin framework is built automatically. Or
headlessly:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16 Pro' build
```

Both apps drive the *same* components; only the view layer differs — Compose on Android, SwiftUI on
iOS, exactly as in `android-mx`.

## Stage status

- **P1 — done.** Module grid, conventions, Metro on every module, `AppGraph` with a working
  cross-module binding chain. Android APK and both iOS targets compile.
- **P2 — done.** Repositories and use cases, two `@Named` storages via a binding container,
  `Set<AnalyticsSink>` contributed from `:core:data` and `:feature:summary`.
- **P3 — done.** Decompose Root with the full four-screen flow, every parameter direction,
  promo `childSlot` with its own nested graph, per-screen graph extensions with `@Provides` inputs.
- **P4 — done.** Xcode project plus a SwiftUI layer over the same components; verified end to end
  on an iPhone 16 Pro simulator. Swift reaches the graph through one function only.
- **P5 — done.** Test graph with `replaces`, incremental-build measurements, a reproducible Metro
  intrinsic bug, and a green Kotlin 2.4.10 + Metro 1.4.0 build on `experiment/kotlin-2.4`.
