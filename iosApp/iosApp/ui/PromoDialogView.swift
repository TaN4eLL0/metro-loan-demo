import SwiftUI
import Shared

struct PromoDialogView: View {

    private let component: PromoDialogComponent

    @StateValue
    private var model: PromoDialogComponentModel

    init(component: PromoDialogComponent) {
        self.component = component
        _model = StateValue(component.model)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Código promocional").font(.title3).bold()

            TextField(
                "Código",
                text: Binding(
                    get: { model.code },
                    set: { component.onCodeChange(code: $0) }
                )
            )
            .textFieldStyle(.roundedBorder)
            .autocorrectionDisabled()
            .textInputAutocapitalization(.characters)
            .disabled(model.isChecking)

            if let error = model.errorText {
                Text(error).foregroundStyle(.red).font(.footnote)
            }

            HStack {
                Button("Cancelar") { component.onDismissClick() }
                Spacer()
                Button("Aplicar") { component.onApplyClick() }
                    .buttonStyle(.borderedProminent)
                    .disabled(!model.isApplyEnabled)
            }

            Spacer()
        }
        .padding()
        .presentationDetents([.medium])
    }
}
