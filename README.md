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

| File | What it demonstrates |
| --- | --- |
| [AppGraph.kt](shared/src/commonMain/kotlin/dev/metrodemo/shared/di/AppGraph.kt) | The one app-wide graph. Note it lists no modules and no bindings. |
| [FakeProductsRepository.kt](core/data/src/commonMain/kotlin/dev/metrodemo/core/data/FakeProductsRepository.kt) | `@ContributesBinding` — reaches `AppGraph` across a module boundary without being named anywhere. |
| [MoneyFormatter.kt](core/common/src/commonMain/kotlin/dev/metrodemo/core/common/format/MoneyFormatter.kt) | `@SingleIn(AppScope::class)` scoped binding. |
| [CalculateTotalUseCase.kt](core/domain/src/commonMain/kotlin/dev/metrodemo/core/domain/CalculateTotalUseCase.kt) | Plain unscoped `@Inject` binding. |
| [MainActivity.kt](androidApp/src/main/kotlin/dev/metrodemo/android/MainActivity.kt) | `createGraphFactory` + `@Provides` runtime input. |
| [ScreenScope.kt](core/common/src/commonMain/kotlin/dev/metrodemo/core/common/di/ScreenScope.kt) | Scope marker for the per-screen graph extensions added in P3. |

The Metro plugin is applied once, in [demo.kmp.gradle.kts](build-logic/convention/src/main/kotlin/demo.kmp.gradle.kts),
so every module gets it.

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

```bash
./gradlew :androidApp:installDebug
```

## Stage status

- **P1 — done.** Module grid, conventions, Metro on every module, `AppGraph` with a working
  cross-module binding chain, Android app renders it. Feature modules hold their component
  contracts and screens; implementations land in P3.
- P2 — repositories, use cases, `@Named` qualifiers, `Set<AnalyticsSink>` multibinding.
- P3 — Decompose Root, 4 screens, parameter passing, promo `childSlot`, per-screen graph extensions.
- P4 — iOS: framework export, SwiftUI views, typed graph accessors.
- P5 — the actual measurements: broken-graph diagnostics, test graphs, build times, Kotlin bump.
