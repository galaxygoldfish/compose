package com.compose.app.android.model

data class FeedbackDocument(
    var title: String,
    var extraDetails: String,
    var feedbackType: String,
    var dateOfSubmission: Long,
    var userOfSubmissionID: String
)
