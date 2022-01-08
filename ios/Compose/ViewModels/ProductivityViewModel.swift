import Foundation
import SwiftUI
import SwiftUIPager
import FirebaseAuth

class ProductivityViewModel: ObservableObject {
    
    @Published var searchQueryText: String = ""
    @Published var selectedTab: Int = 0
    @Published var currentPage: Page = .first()
    
    @Published var noteList1: Array<NoteDocument> = []
    @Published var noteList2: Array<NoteDocument> = []
    @Published var doneLoading = false
    
    init() {
        if (Auth.auth().currentUser != nil) {
            updateNoteList()
        }
    }
    
    func updateNoteList() {
        FirebaseDocument.getAllNotes { documents in
            self.noteList1.removeAll()
            self.noteList2.removeAll()
            let totalSize = documents.count - 1
            let halfSize = totalSize / 2
            (0...halfSize).forEach { index in
                self.noteList1.append(documents[index])
            }
            ((halfSize + 1)...totalSize).forEach { index in
                self.noteList2.append(documents[index])
            }
        }
        doneLoading = true
    }
    
}
