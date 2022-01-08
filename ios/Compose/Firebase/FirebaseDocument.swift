import Foundation
import SwiftUI
import FirebaseFirestore
import FirebaseAuth

class FirebaseDocument {
    
    private static let userdataBasePath = Firestore.firestore().collection("USERDATA")
        .document(Auth.auth().currentUser!.uid)
    
    static func getAllNotes(completion: @escaping (Array<NoteDocument>) -> Void) {
        userdataBasePath.collection("NOTE-DATA").getDocuments { documents, error in
            var list: Array<NoteDocument> = []
            documents?.documents.forEach { item in
                let document = item.data()
                list.append(
                    NoteDocument(
                        id: document["ID"] as! String,
                        color: document["COLOR"] as! Int,
                        content: document["CONTENT"] as! String,
                        title: document["TITLE"] as! String,
                        date: document["DATE"] as! String,
                        time: document["TIME"] as! String
                    )
                )
            }
            completion(list)
        }
    }
    
}
