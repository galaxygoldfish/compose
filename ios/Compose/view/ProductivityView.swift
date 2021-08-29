import SwiftUI
import SwiftUIPager
import UIKit

struct ProductivityView: View {
    
    @State private var searchQueryText: String = ""
    
    @State private var selectedTab: Int = 0
    @State private var noteTabColor: Color = Color.white
    @State private var taskTabColor: Color = Color("NeutralGrayDisabled")
    
    @StateObject private var currentPage: Page = .first()
    private var pagerItems: [() -> VStack<Content>] = [NoteListView, TaskListView]
    
    var body: some View {
        
        let preferences = getNsUserDefaults()
        let userFirstName = preferences.string(forKey: "IDENTITY_USER_NAME_FIRST")!
        
        ZStack(alignment: .topLeading) {
            FullscreenPlaceholder()
            VStack {
                HStack(alignment: .center) {
                    VStack(alignment: .leading) {
                        Text("Welcome, \(userFirstName)")
                            .font(.custom(InterBold, size: 26.0))
                            .padding(.leading, 18)
                            .padding(.top, 15)
                        Text("productivity_temp_message")
                            .font(.custom(InterRegular, size: 16.5))
                            .padding(.leading, 19)
                    }
                    Spacer()
                    Image(uiImage: getAvatarImage())
                        .resizable()
                        .clipShape(Circle())
                        .frame(width: 48, height: 48, alignment: .trailing)
                        .padding(.trailing, 17)
                        .padding(.top, 15)
                }
                ZStack(alignment: Alignment.leading) {
                    Color("NeutralGray")
                        .cornerRadius(10)
                    HStack {
                        Image("SearchMagnifier")
                            .padding(.leading, 15)
                        Spacer()
                        TextField(
                            LocalizedStringKey("productivity_search_hint"),
                            text: $searchQueryText
                        )
                        .font(.custom(InterRegular, size: 17.0))
                        .padding(.leading, 5)
                        Spacer()
                        Image("KeyboardVoice")
                            .padding(.trailing, 15)
                    }
                }
                .frame(width: .infinity, height: 55)
                .padding(.leading, 18)
                .padding(.trailing, 17)
                .padding(.top, 10)
                Pager(
                    page: self.currentPage,
                    data: self.pagerItems,
                    id: \.self
                ) { item in
                    item.
                }
                ZStack {
                    Color("NeutralGray")
                        .cornerRadius(10)
                    HStack(alignment: .center) {
                        Spacer()
                        Button(action: {
                            
                        }) {
                            Image("EditPen")
                                .resizable()
                                .colorMultiply(noteTabColor)
                                .frame(width: 30, height: 30)
                        }
                        .padding(.trailing, 25)
                        .frame(width: 42, height: 42, alignment: .center)
                        Spacer()
                        Button(action: {
                            
                        }) {
                            ZStack(alignment: .center) {
                                Color("DeepSea")
                                    .cornerRadius(8)
                                Image("NewItem")
                                    .colorMultiply(.black)
                            }
                        }
                        .frame(width: 45, height: 45, alignment: .center)
                        Spacer()
                        Button(action: {
                            
                        }) {
                            Image("TaskCheck")
                                .resizable()
                                .colorMultiply(taskTabColor)
                                .frame(width: 30, height: 30)
                        }
                        .padding(.leading, 25)
                        Spacer()
                    }
                }
                .frame(width: .infinity, height: 65)
                .padding(.leading, 18)
                .padding(.trailing, 17)
                .padding(.bottom, 10)
            }
        }
    }
}
