import SwiftUI
import Shared

struct ProductListView: View {

    private let component: ProductListComponent

    @StateValue
    private var model: ProductListComponentModel

    init(component: ProductListComponent) {
        self.component = component
        _model = StateValue(component.model)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Elige un producto").font(.title2).bold()

            if model.isLoading {
                ProgressView()
            } else {
                ForEach(model.products, id: \.id) { product in
                    Button {
                        component.onProductClick(productId: product.id)
                    } label: {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(product.title).font(.headline)
                            Text(product.amountRange).font(.subheadline)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding()
                        .background(Color(.secondarySystemBackground))
                        .cornerRadius(12)
                    }
                    .buttonStyle(.plain)
                }
            }

            Spacer()
        }
        .padding()
    }
}
