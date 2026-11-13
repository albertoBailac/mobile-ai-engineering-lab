import SwiftUI

@MainActor
struct FocusRoute: View {
    @Environment(\.scenePhase) private var scenePhase
    @SceneStorage("focusSessionSnapshot") private var encodedSnapshot = ""
    @State private var viewModel: FocusViewModel
    @State private var didRestoreScene = false

    private let isUITesting: Bool

    init() {
        _viewModel = State(initialValue: FocusViewModel())
        isUITesting = ProcessInfo.processInfo.arguments.contains("-uiTesting")
    }

    var body: some View {
        FocusScreen(state: viewModel.state, onAction: viewModel.send)
            .task {
                restoreSceneIfNeeded()
            }
            .onChange(of: viewModel.state) { _, state in
                persist(state)
            }
            .onChange(of: scenePhase, initial: true) { _, phase in
                switch phase {
                case .active:
                    viewModel.send(.appBecameActive)
                case .background:
                    viewModel.send(.appEnteredBackground)
                case .inactive:
                    break
                @unknown default:
                    break
                }
            }
    }

    private func restoreSceneIfNeeded() {
        guard !didRestoreScene else { return }
        didRestoreScene = true

        if isUITesting {
            encodedSnapshot = ""
            viewModel.send(.reset)
        } else if let snapshot = FocusSessionSnapshotCodec.decode(encodedSnapshot) {
            viewModel.send(.restore(snapshot))
        }
        persist(viewModel.state)
    }

    private func persist(_ state: FocusState) {
        encodedSnapshot = FocusSessionSnapshotCodec.encode(
            FocusSessionSnapshot(state: state)
        ) ?? ""
    }
}
