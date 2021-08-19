import FirebaseAuth
import FirebaseStorage
import SwiftUI

class FirebaseAccount {
    
    private var firebaseAuth: Auth = Auth.auth()
    private var firebaseStorage: Storage = Storage.storage()

    public func authenticateWithEmail(email: String, password: String) -> Bool {
        firebaseAuth.signIn(withEmail: email, password: password)
        return true
    }
    
    public func createUserWithEmail(
        email: String,
        password: String,
        name: String,
        surname: String,
        avatar: UIImage,
        onCompletion: @escaping (LocalizedStringKey) -> Void
    ) {
        if (email != "" && determineIfEmailIsValid(email: email)) {
            if (password != "" && password.count > 6) {
                if (name.count > 1 && surname.count > 1) {
                    self.firebaseAuth.createUser(withEmail: email, password: password) { authResult, error in
                        if (error == nil) {
                            print("created !")
                            DispatchQueue.global().async {
                                self.uploadNewAvatarImage(avatar: avatar)
                                self.uploadNewUserMetadata(name: name, surname: surname)
                                onCompletion(LocalizedStringKey("success_internal"))
                            }
                        } else {
                            print(error.debugDescription)
                            onCompletion(LocalizedStringKey("create_account_failure_generic"))
                        }
                    }
                } else {
                    onCompletion(LocalizedStringKey("create_account_failure_name"))
                }
            } else {
                onCompletion(LocalizedStringKey("create_account_failure_password"))
            }
        } else {
            onCompletion(LocalizedStringKey("create_account_failure_email"))
        }
    }
    
    public func uploadNewUserMetadata(name: String, surname: String) {
        let preferences = getNsUserDefaults()
        preferences.set(name, forKey: "IDENTITY_USER_NAME_FIRST")
        preferences.set(surname, forKey: "IDENTITY_USER_NAME_LAST")
    }
    
    private func storeAvatarImageToFile(avatar: UIImage) {
        if let avatarData = avatar.jpegData(compressionQuality: 40) {
            let avatarPath = getDocumentsDirectory().appendingPathComponent("avatar.png")
            try? avatarData.write(to: avatarPath)
        }
    }
    
    public func uploadNewAvatarImage(avatar: UIImage) {
        print("uploading avatar !")
        let firebaseCurrentUser = firebaseAuth.currentUser!.uid
        let avatarRemotePath = firebaseStorage.reference()
            .child("metadata/avatars/\(String(describing: firebaseCurrentUser))")
        storeAvatarImageToFile(avatar: avatar)
        avatarRemotePath.putFile(
            from: getDocumentsDirectory().appendingPathComponent("avatar.png"),
            metadata: nil
        )
    }
    
    public func signOutCurrentUser() {
        do {
            try Auth.auth().signOut()
        } catch {
            // TODO
        }
    }
}

