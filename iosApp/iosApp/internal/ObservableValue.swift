import Combine
import SwiftUI
import Shared

/// Bridges a Decompose `Value<T>` into a SwiftUI `ObservableObject`.
final class ObservableValue<T: AnyObject>: ObservableObject {

    @Published
    var value: T

    private var cancellation: Cancellation?

    init(_ value: Value<T>) {
        self.value = value.value
        self.cancellation = value.subscribe { [weak self] newValue in
            self?.value = newValue
        }
    }

    deinit {
        cancellation?.cancel()
    }
}

/// Lets each view subscribe to its own slice of state, the way `subscribeAsState()` does in Compose.
@propertyWrapper
struct StateValue<T: AnyObject>: DynamicProperty {

    @ObservedObject
    private var observable: ObservableValue<T>

    var wrappedValue: T { observable.value }

    init(_ value: Value<T>) {
        observable = ObservableValue(value)
    }
}
