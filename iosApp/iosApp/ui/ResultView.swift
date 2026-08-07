import SwiftUI
import Shared

struct ResultView: View {

    private let component: ResultComponent

    @StateValue
    private var model: ResultComponentModel

    init(component: ResultComponent) {
        self.component = component
        _model = StateValue(component.model)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("¡Solicitud enviada!").font(.title2).bold()
            Text(model.productTitle).font(.headline)
            Text(model.amountText).font(.largeTitle)
            Text("ID: \(model.applicationId)").font(.subheadline)

            Button("Repetir con los mismos datos") { component.onRepeatClick() }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)

            Button("Nuevo préstamo") { component.onNewLoanClick() }
                .frame(maxWidth: .infinity)

            Spacer()
        }
        .padding()
    }
}
