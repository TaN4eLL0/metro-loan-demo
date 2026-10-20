import SwiftUI
import Shared

@main
struct iOSApp: App {

    private let root: RootComponent = IosEntryKt.createIosRootComponent()

    var body: some Scene {
        WindowGroup {
            RootView(component: root)
        }
    }
}
