import Foundation
import SwiftUI
import SwiftUIPager

class ProductivityViewModel: ObservableObject {
    
    @Published var searchQueryText: String = ""
    @Published var selectedTab: Int = 0
    @Published var currentPage: Page = .first()
    
    
}
