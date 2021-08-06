package com.compose.app.android.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.compose.app.android.account.FirebaseAccount

class ProductivityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!FirebaseAccount().determineIfUserExists()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        setContent {
            MainContent()
        }
    }

    @Composable
    fun MainContent() {

        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            content = @Composable {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight()
                    ) {

                    }
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProductivityPreview() {
    ProductivityActivity().MainContent()
}