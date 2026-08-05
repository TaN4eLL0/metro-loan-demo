# metro-loan-demo — план реализации

Тестовое KMP-приложение «мини-калькулятор займа» для проверки **Metro DI** в действии
на архитектуре, повторяющей android-mx: **многомодульные фичи** (components/ui на
фичу), shared Decompose-компоненты, Compose UI на Android, **нативный SwiftUI на iOS**,
навигация с передачей параметров вперёд и назад.

Цель — не приложение, а чек-лист: выяснить, ложится ли Metro на наши паттерны
(component-scope, параметризованные фабрики, слот-диалоги, Swift-доступ,
**DI через границы многих Gradle-модулей**) и что происходит с билд-таймом и бампом Kotlin.

## 1. Стек и версии

| Что | Версия | Примечание |
|---|---|---|
| Kotlin | 2.3.21 | как в android-mx (Metro 1.4.0 поддерживает: 0.10.0+) |
| Metro | 1.4.0 | плагин `dev.zacsweers.metro`, применяется на КАЖДЫЙ модуль с DI |
| Compose Multiplatform | 1.9.3 | как в android-mx |
| Decompose / Essenty | 3.2.2 / 2.5.0 | как в android-mx |
| kotlinx-serialization | последняя | Config-классы навигации |
| kotlinx-coroutines | последняя | |
| Ktor | — **не берём** | data-слой на fake-репозиториях с delay(), чтобы не раздувать |

iOS-интеграция: **direct framework integration** (`embedAndSignAppleFrameworkForXcode`
build phase), без CocoaPods — текущая рекомендация JetBrains. Static framework,
`baseName = "Shared"`, export Decompose/Essenty + всех feature-components модулей
(нужно для `ComponentContext` и моделей компонентов в Swift).

## 2. Структура проекта — многомодульная, по образу android-mx

```
/Users/egor/AndroidStudioProjects/metro-loan-demo/
├── settings.gradle.kts, build.gradle.kts, gradle/libs.versions.toml
├── build-logic/convention/            # convention-плагины (best practice):
│     kmp-library.gradle.kts           #   KMP-таргеты + Metro plugin + общие deps
│     feature-components.gradle.kts    #   = kmp-library + Decompose + core api
│     feature-ui.gradle.kts            #   = kmp-library + Compose MP + свой components
│
├── core/
│   ├── common/          # scope-маркеры (AppScope, ScreenScope), format/ (Money/DateFormatter)
│   ├── data/            # репозитории: интерфейсы + Fake impl (@ContributesBinding в AppScope),
│   │                    #   2×KeyValueStorage (@Named), AnalyticsSink (@ContributesIntoSet)
│   └── domain/          # CalculateScheduleUseCase, SubmitApplicationUseCase, ValidatePromoCodeUseCase
│
├── feature/                           # 4 фичи × 2 модуля — как compose/<feature>/{components,ui}
│   ├── product-list/
│   │   ├── components/  # ProductListComponent (+Default), Model, ProductListGraph
│   │   └── ui/          # ProductListScreen.kt (Compose)
│   ├── calculator/
│   │   ├── components/  # CalculatorComponent, @Assisted params, CalculatorGraph
│   │   └── ui/
│   ├── summary/
│   │   ├── components/  # SummaryComponent + promo childSlot + PromoDialogComponent,
│   │   │                #   SummaryGraph → PromoDialogGraph (extension от extension)
│   │   └── ui/          # SummaryScreen + PromoDialog (Compose)
│   └── result/
│       ├── components/  # ResultComponent, ResultGraph
│       └── ui/
│
├── shared/              # umbrella: RootComponent (ChildStack + Config),
│   │                    #   AppGraph (@DependencyGraph, агрегирует контрибуции всех модулей),
│   │                    #   RootContent.kt (Children → feature ui),
│   │                    #   ios/IosGraphAccessors.kt — типизированные аксессоры для Swift,
│   │                    #   iOS framework (binaries.framework, export всех components)
│   └── depends on: все feature/*/{components,ui}, core/*
│
├── androidApp/          # MainActivity: retainedComponent { RootComponent } + Compose host
└── iosApp/              # Xcode-проект, SwiftUI
    └── iosApp/
        ├── App.swift, RootView.swift            # StackView(root.stack) → switch child
        ├── internal/ObservableValue.swift, StateValue.swift   # как в android-mx
        └── ui/ProductListView.swift, CalculatorView.swift,
              SummaryView.swift (+ .sheet по childSlot), ResultView.swift
```

Итого **12 Gradle-модулей** (3 core + 8 feature + shared) + androidApp + iosApp.
Это намеренно: главный вопрос для android-mx (~30 модулей) — как Metro агрегирует
DI-контрибуции через много модульных границ и во что это обходится билду.

Правила зависимостей (как у нас):
- `feature/*/components` → только `core/*`; **фичи друг про друга не знают**;
- `feature/*/ui` → свой `components` (+ Compose);
- навигацию между фичами знает только `shared` (Root) — межфичевые параметры
  проходят через Config'и Root'а, не через прямые зависимости фич;
- Metro-плагин и таргеты (android + iosArm64/iosSimulatorArm64/iosX64)
  вешаются convention-плагином, не копипастой.

## 3. Экраны, навигация, параметры

```
ProductList ──ProductId──▶ Calculator ──LoanDraft──▶ Summary ──ApplicationResult──▶ Result
                                            ▲              │ childSlot: PromoDialog
                                            │              ▼   (draft ⇄ promo назад через callback)
                                            └── назад с изменённым draft (onBack с параметром)
Result: [Новый займ] → popTo(ProductList) / [Повторить] → replaceAll(Calculator(prefill))
```

Передача параметров (то, что просили прожать):
1. **ProductList → Calculator**: `Config.Calculator(productId, defaultAmount)` — простой forward-параметр через sealed `Config` (@Serializable).
2. **Calculator → Summary**: `LoanDraft(productId, amount, termDays, promoCode?)` — data-класс целиком в Config.
3. **Summary → PromoDialog (childSlot)**: draft внутрь, `PromoResult` наружу через callback + обновление state родителя. Диалог получает **свой** child-граф от графа Summary — воспроизводим наш класс багов со слот-контекстами.
4. **Summary → назад в Calculator**: возврат отредактированного draft (`onEditRequested(draft)`) — backward-параметр через колбэк родителю, родитель делает `pop` + шлёт draft в живой Calculator-компонент (проверка retained state / `instanceKeeper`).
5. **Summary → Result**: `applicationId` из fake submit + сумма.
6. **Result → Calculator (prefill)**: replaceAll с параметрами — «повторить займ».

Межфичевая развязка: компоненты фич не знают Config'ов друг друга — наружу отдают
колбэки (`onProductSelected(ProductId)`, `onDraftReady(LoanDraft)`), маршрутизирует Root.

## 4. DI-архитектура (Metro) — что именно проверяем

### Графы
```
AppGraph  @DependencyGraph(AppScope::class)   — живёт в shared, НЕ знает фич поимённо
 ├─ @SingleIn(AppScope): репозитории, форматтеры, 2×KeyValueStorage(@Named)
 │     (все приезжают контрибуциями из core/data: @ContributesBinding)
 ├─ Set<AnalyticsSink> — @ContributesIntoSet из core/data И из feature/summary (2 модуля)
 └─ @ContributesGraphExtension(ScreenScope) — объявлены В МОДУЛЯХ ФИЧ,
     в AppGraph приезжают агрегацией:
       ProductListGraph / CalculatorGraph / SummaryGraph / ResultGraph
          │  creator: factory с @Provides args:
          │           ComponentContext + params экрана (аналог factory { (params) -> })
          └─ SummaryGraph → PromoDialogGraph (extension от extension — слот-диалог)
```

### Соответствие нашим Koin-паттернам

| android-mx (Koin) | metro-loan-demo (Metro) | риск |
|---|---|---|
| DI-модули фич, `includes(...)` | `@ContributesGraphExtension` из feature-модуля, агрегация без ручного списка | ★★ главный тест многомодульности |
| `componentScope()` из koin-decompose | graph extension на компонент, создаётся в `init`, живёт с lifecycle | ★ |
| `factory { (params: XParams) -> }` | `@Assisted` в конструкторе / `@Provides` args в extension-factory | ★ |
| childSlot dialog + свой context | PromoDialogGraph от SummaryGraph + `childContext` | ★ (наш MMMX-55850 класс) |
| `named(LocalStorageType.X)` | `@Named("default")` / `@Named("secure")` | низкий |
| `single` в root-модуле | `@SingleIn(AppScope)` + `@ContributesBinding` из core/data | низкий |
| iOS keyPaths + @LazyKoin | typed-аксессоры на AppGraph, читаются из Swift | ★ |
| Мокирование в тестах | тестовый `@DependencyGraph` в commonTest фичи с replace-биндами | средний |

### iOS-аксессоры
`IosGraphAccessors.kt` в shared: `fun AppGraph.createRoot(ctx: ComponentContext): RootComponent`
+ 1–2 «сервисных» аксессора — прообраз замены нашего keyPaths-allowlist. Swift держит
`let graph = AppGraphCompanion.create()` в App.

### Что НЕ делаем
Auth-сессионный субграф, Ktor, БД, реальная аналитика, detekt/CI. Приложение остаётся
≤ ~40 kotlin-файлов + ~8 swift; вся «масса» — в модульной структуре, не в коде.

## 5. UI (коротко)

- **Android**: Compose MP в `feature/*/ui`, `shared` собирает `Children(stack)` +
  `androidPredictiveBackAnimatable`. MainActivity — единственный Android-специфичный файл.
- **iOS**: SwiftUI, по вью на экран, `StackView`-хелпер по `ChildStack` (копируем
  проверенные `ObservableValue/StateValue` обёртки из android-mx iosApp), слот промо —
  `.sheet(item:)` от `childSlot`. Compose на iOS не используем — как в android-mx.
- Каждый компонент отдаёт display-ready state (`Model` с уже отформатированными
  суммами) — по нашим правилам.

## 6. Этапы

| # | Этап | Результат |
|---|---|---|
| P1 | Скаффолд: gradle, build-logic convention-плагины, вся модульная сетка (пустые модули), Metro на каждом, пустой AppGraph, Android app запускается с заглушкой | 12 модулей компилируются, Metro активен везде |
| P2 | `core/*`: репозитории (fake + delay), use cases, форматтеры, @ContributesBinding, @Named-стораджи, @ContributesIntoSet | агрегация core → shared работает |
| P3 | Фичи: 4×components + 4×ui, Root в shared, все 6 параметро-передач, promo childSlot, @ContributesGraphExtension из фич; Android e2e | главный этап, ★-риски сняты/подтверждены |
| P4 | iOS: framework export (все components), Xcode-проект, Swift-обёртки Value, 4 SwiftUI-вью + sheet, typed-аксессоры | e2e на симуляторе |
| P5 | Эксперименты: (a) намеренно сломать граф в одном feature-модуле — где и как всплывает ошибка (в модуле? в shared при агрегации? в IDE?); (b) commonTest с подменённым биндом; (c) замер clean/incremental build (правка одной фичи — что перекомпилируется); (d) бамп Kotlin на 2.4.x → Metro 1.4 forward-compat | отчёт-вывод по Metro |

P1–P3 — Android-only проверяемо; P4 отдельно; P5 — собственно то, ради чего проект.

## 7. Журнал находок

Всё, что вылезло по ходу — это и есть материал для итогового вывода по Metro.

### P1

1. **Gradle-плагин Metro опубликован под Java 21.** Модуль `build-logic` пришлось перевести на
   `jvmToolchain(21)`, иначе Gradle не резолвит вариант артефакта
   (`Incompatible because this component declares a component, compatible with Java 21`).
   Для android-mx это значит: сборка обязана идти на JDK 21+.
2. **Metro нельзя применить из precompiled script plugin на Gradle 8.13.** Его jar собран Kotlin
   2.2, а `kotlin-dsl` в Gradle 8.13 компилирует скрипты встроенным Kotlin 2.0 и падает на
   `.kotlin_module`: `binary version of its metadata is 2.2.0, expected version is 2.0.0`.
   Обход — применять `alias(libs.plugins.metro)` в каждом модуле (13 строк вместо одной в
   convention-плагине). Альтернатива — Gradle 9.x со встроенным Kotlin 2.2, но это тянет за собой
   совместимость с AGP. **Прямо касается android-mx**: у нас тоже Gradle 8.13 и convention-плагины
   на `Plugin<Project>`, так что централизованно включить Metro одним конвенционным плагином не
   получится без апгрейда Gradle.
3. **Агрегация через границы модулей работает.** `FakeProductsRepository` с
   `@ContributesBinding(AppScope::class)` живёт в `:core:data`; в `:shared` нет ни его импорта, ни
   списка модулей — и он всё равно попадает в `AppGraph`. Апк собирается, экран печатает данные из
   графа. Это ровно та фича, из-за которой у нас исчезает ручной `includes(...)`.
4. **Компайл-тайм диагностика — очень хорошая.** Добавленный в `AppGraph` аксессор без биндинга
   валит билд за **3 секунды** с точным `file:line:column` и ссылкой на документацию:
   ```
   e: .../shared/.../di/AppGraph.kt:29:9  ╭─ [Metro/MissingBinding] No binding found for Duration
      docs: https://zacsweers.github.io/metro/latest/diagnostics/#missingbinding
   ```
   Для сравнения: у нас сейчас такой же дефект — это `NoBeanDefFoundException` в рантайме на
   устройстве.
5. **★ Самая важная находка: Metro 1.2.0+ не работает на Kotlin 2.3.21 под iOS.**
   `compileKotlinIosSimulatorArm64` падает с обманчивым сообщением
   `KLIB resolver: Could not find .../runtime-iosSimulatorArm64Main-1.4.0.klib` — при том что файл
   лежит ровно по этому пути и является валидным zip. Настоящая причина в манифесте klib:

   | Metro | abi_version | compiler_version |
   |---|---|---|
   | 1.4.0 | 2.4.0 | 2.4.0 |
   | 1.3.2 | 2.4.0 | 2.4.0 |
   | 1.2.1 | 2.4.0 | 2.4.0 |
   | **1.1.1** | **2.3.0** | **2.3.21** |
   | 1.0.1 | 2.3.0 | 2.3.21 |

   Native klib ABI не forward-compatible: компилятор Kotlin/Native 2.3.21 не может прочитать klib,
   собранный 2.4.0. При этом **Android-таргет с 1.4.0 собирается нормально** — ограничение только
   на Kotlin/Native.

   Важно: таблица совместимости Metro (`Kotlin 2.3.21 → Metro 0.10.0 - current`) описывает
   **compiler plugin**, а не ABI рантайм-klib. Для KMP с iOS она вводит в заблуждение.

   **Что это значит для android-mx:** на нашем Kotlin 2.3.21 сегодня доступна максимум
   **Metro 1.1.1**. Набор аннотаций у 1.1.1 идентичен 1.4.0 (`GraphExtension`, `ContributesTo`,
   `ContributesBinding`, `ContributesIntoSet`, `Assisted`, `Named`, `SingleIn`, `Multibinds`,
   `Includes`, `binding`); 1.4.0 добавляет только экспериментальные suspend-провайдеры. То есть
   для эксперимента мы ничего не теряем, но «жить на последней версии Metro» = держать Kotlin
   в актуальном состоянии почти синхронно с релизами Metro. Демо запинено на 1.1.1; апгрейд
   Kotlin до 2.4.x и переход на 1.4.0 — отдельный пункт P5.

### P2 + P3

6. **Всё из таблицы соответствия легло на Metro без хаков, и собралось с первого раза.**
   Работают: `@GraphExtension` на экран + `@ContributesTo(AppScope)` на его фабрике (в `:shared`
   нет ни одного упоминания фич), `@Provides`-аргументы фабрики вместо
   `factory { (params) -> ... }`, `@BindingContainer` с двумя `@Named`-реализациями одного
   интерфейса, `Set<AnalyticsSink>` с контрибуторами из двух модулей, `asContribution<X.Factory>()`
   в Root, и **graph extension от graph extension** — граф промо-диалога висит на графе Summary,
   а его фабрика инжектится прямо в `DefaultSummaryComponent`.
7. **`@ContributesBinding` требует явного `binding<T>()` для Decompose-компонентов.**
   `class DefaultXComponent : XComponent, ComponentContext by componentContext` — делегирование
   делает `ComponentContext` вторым супертипом, и контрибуция становится неоднозначной. Лечится
   `@ContributesBinding(Scope::class, binding = binding<XComponent>())`. Это будет в каждом нашем
   компоненте, то есть строчка-ритуал на весь проект.
8. **Гипотеза про утечку биндингов между экранными графами опровергнута.** Ожидалось, что общий
   `ScreenScope` на все фичи сломает сборку (биндинг чужой фичи попадёт в граф и потянет
   неудовлетворимую зависимость). Проверено: **собирается**. Metro резолвит только то, что граф
   реально отдаёт наружу, поэтому недостижимая контрибуция просто игнорируется. Отдельные маркеры
   в демо оставлены как соглашение, а не как необходимость — но для вложенности они обязательны
   (диалогу нужен свой скоуп, отличный от родительского).
9. **У Metro-графов нет `close()` — в отличие от Koin-скоупов.** `componentScope()` из
   koin-decompose закрывает скоуп на `ON_DESTROY`; в Metro граф — обычный объект, ничего не
   диспозится. Всё, у чего есть время жизни, привязывается к Decompose-lifecycle руками
   (в демо — `Lifecycle.componentScope()` в `core/common`). Для нас это означает, что уход с
   koin-decompose придётся компенсировать своим хелпером.
10. **iOS собирается и линкуется.** `linkDebugFrameworkIosSimulatorArm64` проходит со всеми
    экранными графами — кодогенерация Metro под Kotlin/Native работает, не только под JVM.

## 8. Критерии успеха

Metro «прошёл», если: все ★-строки таблицы реализуются без хаков; фичи контрибутят
графы без ручного списка в shared; ошибка в графе фичи видна в IDE (FIR) до запуска
и указывает на модуль-виновник; инкрементальный билд при правке одной фичи не
перекомпилирует всё; слот-диалог не имеет аналога context-collision бага; Swift не
может запросить незадекларированную зависимость; тестовый граф подменяет бинды без
boilerplate ощутимо хуже Koin-модулей.
