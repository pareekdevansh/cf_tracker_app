package com.pareekdevansh.cftracker.ui.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.databinding.ActivityAuthBinding
import com.pareekdevansh.cftracker.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000L)
            binding.splashAnimation.apply {
                pauseAnimation()
                visibility = View.GONE
            }
            binding.outerCard.visibility = View.VISIBLE
        }

        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            getString(
                R.string.default_web_client_id
            )
        ).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.SignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {

                    val account = task.result
                    val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credentials).addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
            }

        }

}