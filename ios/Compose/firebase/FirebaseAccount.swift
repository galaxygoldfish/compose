import FirebaseAuth
import FirebaseFirestore
import FirebaseStorage
import SwiftUI

class FirebaseAccount {
    
    private let firebaseAuth: Auth = Auth.auth()
    private let firebaseStorage: Storage = Storage.storage()
    private let firebaseFirestore: Firestore = Firestore.firestore()

    /**
     * Authenticate an existing user with an e-mail and password.
     *
     * - Parameters:
     *  - email: User-inputted email address that is associated
     *    with their existing Compose account.
     *  - password: User-inputted password string that is also
     *    set to their Compose account. (case-sensitive)
     *  - onCompletion: A void-returning function that is to be invoked
     *    once a result has been determined, containing a LocalizedStringKey
     *    value describing the result.
     */
    public func authenticateWithEmail(
        email: String,
        password: String,
        onCompletion: @escaping (LocalizedStringKey) -> Void
    ) {
        if (email != "" && determineIfEmailIsValid(email: email)) {
            if (password != "" && password.count > 6) {
                firebaseAuth.signIn(withEmail: email, password: password, completion: { (result, error) in
                    if (error == nil) {
                        self.getExistingUserMetadata()
                        self.getExistingAvatarImage()
                        onCompletion(LocalizedStringKey("success_internal"))
                    } else {
                        onCompletion(LocalizedStringKey("log_in_failure_generic"))
                    }
                })
            } else {
                onCompletion(LocalizedStringKey("log_in_failure_password"))
            }
        } else {
            onCompletion(LocalizedStringKey("log_in_failure_email"))
        }
    }
    
    /**
     * Creates a new user account with e-mail and password and
     * associates metadata including avatar image and name with
     * the newly created account.
     *
     * - Parameters:
     *  - email: User-provided e-mail address that they wish to associate
     *    with their new account.
     *  - password: User-provided password that they wish to use to secure
     *    their account. This must be longer than 6 characters and can
     *    contain special symbols.
     *  - name: The user's first name
     *  - surname: The user's last name
     *  - avatar: A UIImage containing the selected (or default) profile
     *    picture that the user wishes to use as their avatar.
     *  - onCompletion: A function that is invoked as soon as the user is
     *    created, or if there was an error. A LocalizedStringKey containing
     *    a status message is included, to display to the user.
     */
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
                                self.syncMetadataPreferences(name: name, surname: surname)
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
    
    /**
     * Updates the user's metadata file located in Firebase,
     * which contains the user's first and last name (as of now).
     *
     * - Parameters:
     *  - name: The new first name to be updated.
     *  - surname: The new last name to be updated.
     */
    public func setNewUserMetadata(name: String, surname: String) {
        let metadataRemote = firebaseFirestore.collection("metadata").document(firebaseAuth.currentUser!.uid)
        let userdataRemote: [String : String] = ["firstName" : name, "lastName" : surname]
        metadataRemote.setData(userdataRemote)
        syncMetadataPreferences(name: name, surname: surname)
    }
    
    /**
     * Gets the user's metadata file and downloads it locally,
     * then syncs it to NSUserDefaults. This method should be
     * called regularly to make sure that the user's info
     * is always up to date, like their avatar and name.
     */
    public func getExistingUserMetadata() {
        let metadataRemote = firebaseFirestore.collection("metadata").document(firebaseAuth.currentUser!.uid)
        metadataRemote.getDocument { (document, error) in
            if let document = document, document.exists {
                self.syncMetadataPreferences(
                    name: document.value(forKey: "firstName")! as! String,
                    surname: document.value(forKey: "lastName")! as! String
                )
            }
        }
    }
    
    /**
     * Updates the user's name and last name in the app's
     * NSUserDictionary locally.
     *
     * - Parameters:
     *  - name: The new first name to be updated.
     *  - surname: The new last name to be updated.
     */
    private func syncMetadataPreferences(name: String, surname: String) {
        let preferences = getNsUserDefaults()
        preferences.set(name, forKey: "IDENTITY_USER_NAME_FIRST")
        preferences.set(surname, forKey: "IDENTITY_USER_NAME_LAST")
    }
    
    /**
     * Gets the user's existing avatar image from Firebase
     * and downloads it to the app-wide recognized avatar
     * destination, in the documents/avatar.png file, which
     * can be accessed from any screen.
     */
    public func getExistingAvatarImage() {
        let firebaseCurrentUser = firebaseAuth.currentUser!.uid
        let avatarPathRemote = firebaseStorage.reference().child("metadata/avatars/\(firebaseCurrentUser)")
        let avatarPathLocal = getDocumentsDirectory().appendingPathComponent("avatar.png")
        avatarPathRemote.write(toFile: avatarPathLocal)
    }
    
    /**
     * Writes the contents of the user's selected new
     * avatar to the profile image location, documents/avatar.png.
     *
     * - Parameters:
     *  - avatar: A UIImage containing the target image to be
     *    updated to.
     */
    private func storeNewAvatarImageToFile(avatar: UIImage) {
        if let avatarData = avatar.jpegData(compressionQuality: 40) {
            let avatarPath = getDocumentsDirectory().appendingPathComponent("avatar.png")
            try? avatarData.write(to: avatarPath)
        }
    }
    
    /**
     * Uploads a new avatar image to remote Firebase storage and
     * stores the file locally for access in other UI screens.
     *
     * - Parameters:
     *  - avatar: A UIImage containing the new avatar.
     */
    public func uploadNewAvatarImage(avatar: UIImage) {
        let firebaseCurrentUser = firebaseAuth.currentUser!.uid
        let avatarRemotePath = firebaseStorage.reference()
            .child("metadata/avatars/\(firebaseCurrentUser)")
        storeNewAvatarImageToFile(avatar: avatar)
        avatarRemotePath.putFile(
            from: getDocumentsDirectory().appendingPathComponent("avatar.png"),
            metadata: nil
        )
    }
    
    /**
     * Signs out the current user from their device and removes
     * related metadata including avatar and name.
     */
    public func signOutCurrentUser() {
        do {
            try Auth.auth().signOut()
            removeUserdataLocal()
        } catch {
            // TODO
        }
    }
    
    /**
     * Deletes user metadata that is stored locally on the
     * NSUserDictionary and also removes the avatar image
     * stored locally.
     */
    private func removeUserdataLocal() {
        let preferences = getNsUserDefaults()
        let fileManager = FileManager.default
        let avatarPathLocal = getDocumentsDirectory().appendingPathComponent("avatar.png")
        do {
            preferences.removeObject(forKey: "IDENTITY_USER_NAME_FIST")
            preferences.removeObject(forKey: "IDENTITY_USER_NAME_LAST")
            try fileManager.removeItem(at: avatarPathLocal)
        } catch {
            // TODO
        }
    }
}

