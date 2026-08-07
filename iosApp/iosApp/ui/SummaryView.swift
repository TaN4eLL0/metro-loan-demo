import SwiftUI
import Shared

struct SummaryView: View {

    private let component: SummaryComponent

    @StateValue
    private var model: SummaryComponentModel

    @StateValue
    private var promoSlot: ChildSlot<AnyObject, PromoDialogComponent>

    init(component: SummaryComponent) {
        self.component = component
        _model = StateValue(component.model)
        _promoSlot = StateValue(component.promoDialog)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Resumen").font(.title2).bold()
            Text(model.productTitle).font(.headline)
            Text(model.amountText).font(.largeTitle)
            Text("\(model.termDays) días")
            Text(model.totalToRepayText)

            Button(model.promoCode.map { "Promo: \($0)" } ?? "Agregar código promocional") {
                component.onPromoClick()
            }

            Button("Confirmar") { component.onConfirmClick() }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
                .disabled(model.isSubmitting)

            Button("Editar monto") { component.onEditClick() }
                .frame(maxWidth: .infinity)

            Spacer()
        }
        .padding()
        .sheet(isPresented: .constant(promoSlot.child?.instance != nil)) {
            if let dialog = promoSlot.child?.instance {
                PromoDialogView(component: dialog)
                    .interactiveDismissDisabled()
            }
        }
    }
}
