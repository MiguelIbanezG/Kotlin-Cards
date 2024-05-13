package es.uam.eps.dadm.cards

import android.app.Activity
import android.widget.ImageView
import androidx.compose.ui.text.input.PasswordVisualTransformation
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.map
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import es.uam.eps.dadm.cards.database.CardDao
import java.time.LocalDateTime


@Composable
fun EmailPassword(navController: NavController, viewModel: CardViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    val context = LocalContext.current as Activity

    val onSignInWithEmailAndPassword: () -> Unit = {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && email.isNotEmpty() && password.isNotEmpty()) {
                        SettingsActivity.setLoggedIn(context, true)
                        val user = auth.currentUser
                        viewModel.refreshData()
                        navController.navigate(NavRoutes.Decks.route)
                    } else {
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else{
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        }

    }

    val createInWithEmailAndPassword: () -> Unit = {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context)  { task ->
                    if (task.isSuccessful ) {
                        val user = auth.currentUser
                        viewModel.refreshData()
                        navController.navigate(NavRoutes.Decks.route)
                    } else {
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(110.dp))

        Text(stringResource(id = R.string.Login),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            color = Color.Black)

        Spacer(modifier = Modifier.height(60.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        var passwordVisibility by remember { mutableStateOf(true) }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            visualTransformation = if (passwordVisibility) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility }
                ) {
                    Icon(
                        if (passwordVisibility) {
                            painterResource(id = R.drawable.baseline_visibility_off_24)
                        } else {
                            painterResource(id = R.drawable.baseline_remove_red_eye_24)
                        },
                        contentDescription = if (passwordVisibility) {
                            stringResource(id = R.string.updateDeck)
                        } else {
                            stringResource(id = R.string.email)
                        }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Button(onClick = onSignInWithEmailAndPassword,
                 colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text(context.getString(R.string.sign))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = createInWithEmailAndPassword,
                colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text(context.getString(R.string.create))
            }
        }

    }
}




