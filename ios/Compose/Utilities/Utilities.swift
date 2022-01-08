import Foundation
import UIKit

func getDocumentsDirectory() -> URL {
    let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
    return paths[0]
}

func getAvatarImage() -> UIImage {
    let avatarImageData = try! Data(contentsOf: getDocumentsDirectory().appendingPathComponent("avatar.png"))
    return UIImage(data: avatarImageData)!
}

func determineIfEmailIsValid(email: String) -> Bool {
    let emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
    let emailResult = NSPredicate(format: "SELF MATCHES %@", emailRegex)
    return emailResult.evaluate(with: email)
}

func getNsUserDefaults() -> UserDefaults {
    return UserDefaults.standard
}

