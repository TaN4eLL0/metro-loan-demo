import SwiftUI
import Shared

struct RootView: View {

    let component: RootComponent

    @StateValue
    private var stack: ChildStack<AnyObject, RootComponentChild>

    init(component: RootComponent) {
        self.component = component
        _stack = StateValue(component.stack)
    }

    var body: some View {
        switch stack.active.instance {
        case let child as RootComponentChildProductList:
            ProductListView(component: child.component)
        case let child as RootComponentChildCalculator:
            CalculatorView(component: child.component)
        case let child as RootComponentChildSummary:
            SummaryView(component: child.component)
        case let child as RootComponentChildResult:
            ResultView(component: child.component)
        default:
            EmptyView()
        }
    }
}
