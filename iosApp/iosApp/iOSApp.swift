import SwiftUI
import Shared

@main
struct iOSApp: App {

    /// The graph is built once, on the Kotlin side, by `createIosRootComponent()`.
    private let root: RootComponent = IosEntryKt.createIosRootComponent()

    var body: some Scene {
        WindowGroup {
            RootView(component: root)
        }
    }
}
