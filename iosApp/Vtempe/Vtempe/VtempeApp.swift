import SwiftUI
import UIKit
import AppIos

struct ComposeRootView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        IosEntryPointKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@main
struct VtempeApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeRootView()
        }
    }
}
