import SwiftUI
import Shared

struct CalculatorView: View {

    private let component: CalculatorComponent

    @StateValue
    private var model: CalculatorComponentModel

    init(component: CalculatorComponent) {
        self.component = component
        _model = StateValue(component.model)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(model.productTitle).font(.title2).bold()
            Text(model.amountText).font(.largeTitle)

            if model.maxAmount > model.minAmount {
                Slider(
                    value: Binding(
                        get: { Double(model.amount) },
                        set: { component.onAmountChange(amount: Int32($0)) }
                    ),
                    in: Double(model.minAmount)...Double(model.maxAmount)
                )
            }

            Text("\(model.termDays) días").font(.headline)

            if model.maxTermDays > model.minTermDays {
                Slider(
                    value: Binding(
                        get: { Double(model.termDays) },
                        set: { component.onTermChange(termDays: Int32($0)) }
                    ),
                    in: Double(model.minTermDays)...Double(model.maxTermDays)
                )
            }

            Text(model.totalToRepayText)

            Button("Continuar") { component.onContinueClick() }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
                .disabled(!model.isContinueEnabled)

            Spacer()
        }
        .padding()
    }
}
