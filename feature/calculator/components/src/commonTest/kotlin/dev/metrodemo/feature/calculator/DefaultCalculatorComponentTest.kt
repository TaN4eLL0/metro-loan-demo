package dev.metrodemo.feature.calculator

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.metrodemo.core.common.LoanDraft
import dev.metrodemo.core.common.Platform
import dev.zacsweers.metro.asContribution
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultCalculatorComponentTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsProductBoundsFromTheGraph() = runTest(testDispatcher) {
        val component = createComponent {}
        advanceUntilIdle()

        val model = component.model.value
        assertEquals("Stub product", model.productTitle)
        assertEquals(1_000, model.amount)
        assertEquals(10, model.termDays)
    }

    @Test
    fun continueReportsTheEditedDraft() = runTest(testDispatcher) {
        var emitted: LoanDraft? = null
        val component = createComponent { emitted = it }
        advanceUntilIdle()

        component.onAmountChange(7_000)
        component.onTermChange(15)
        component.onContinueClick()

        assertEquals(LoanDraft(productId = "stub", amount = 7_000, termDays = 15), emitted)
    }

    private fun createComponent(onDraftReady: (LoanDraft) -> Unit): CalculatorComponent {
        val graphFactory = createGraphFactory<TestGraph.Factory>()
        val testGraph = graphFactory.create(platform = Platform(name = "test"))
        val calculatorGraphFactory = testGraph.asContribution<CalculatorGraph.Factory>()
        return calculatorGraphFactory
            .createCalculatorGraph(
                componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
                args = CalculatorArgs(productId = "stub"),
                output = object : CalculatorOutput {
                    override fun onDraftReady(draft: LoanDraft) = onDraftReady(draft)
                },
            )
            .component
    }
}
