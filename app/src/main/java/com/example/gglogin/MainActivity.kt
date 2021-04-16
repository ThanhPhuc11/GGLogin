package com.example.gglogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var GG_SIGN_IN = 1004
    lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode("105667876514-diaqb5biscjuuf9bg75193lsm72h46q5.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun login(view: View) {
        mGoogleSignInClient.signOut()
        snsGoogleLogin(mGoogleSignInClient)
    }

    private fun snsGoogleLogin(mGoogleSignInClient: GoogleSignInClient) {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GG_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GG_SIGN_IN -> {
                val task =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                task?.let { handleGoogleSignInResult(it) }
            }
            else -> {
            }
//            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun handleGoogleSignInResult(result: GoogleSignInResult) {


        try {
            val acct: GoogleSignInAccount? = result.signInAccount
            val authCode = acct?.serverAuthCode
            Toast.makeText(this, acct?.displayName, Toast.LENGTH_SHORT).show()

            val client = OkHttpClient()
            val requestBody: RequestBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add(
                    "client_id",
                    "105667876514-diaqb5biscjuuf9bg75193lsm72h46q5.apps.googleusercontent.com"
                )
                .add(
                    "client_secret",
                    "hWIjGpxsp67fMQ4mgLy5kNO5"
                )//when register in https://developers.google.com/identity/sign-in/android/start-integrating
                .add("redirect_uri", "")
                .add("code", "$authCode")
                .add("id_token", "${acct?.idToken}")
                .build()
            val request: Request = Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.e("TAG", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonObject = JSONObject(response.body?.string())
                        val message: String = jsonObject.toString(5)
                        var accessToken = jsonObject.getString("access_token")

                        runOnUiThread {
//                            viewModel.loginWithGoogle(accessToken)
                        }

                        Log.i("TAG", message)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })

        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }
}