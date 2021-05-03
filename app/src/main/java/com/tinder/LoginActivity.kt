package com.tinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth // auth = FirebaseAuth.getInstance() 랑 같음 근데 이건 좀 자바스럽
        callbackManager = CallbackManager.Factory.create()

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }

    private fun initLoginButton() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            handleSuccessLogin()
                        }else{
                            Toast.makeText(this, "로그인에 실패하였습니다. 이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }

        }
    }

    private fun initSignUpButton() {
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful){
                            Toast.makeText(this, "회원가입에 성공하였습니다. 로그인 버튼을 눌러 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun initEmailAndPasswordEditText() { // 값 비어있을 때 파이어베이스에서 오류나지 않도록 예외처리
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
    }

    private fun initFacebookLoginButton() {
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        facebookLoginButton.setPermissions("email", "public_profile") // 계정에서 가져올 정보
        facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                // 로그인 성공
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)// 로그인 엑세스 토큰 파이어베이스로 넘기기
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if(task.isSuccessful){
                                handleSuccessLogin()
                            }else {
                                Toast.makeText(this@LoginActivity, "페이스북 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) { // 콜백안에있기 때문에 this@LoginActiviy로 해주어야함
                Toast.makeText(this@LoginActivity, "페이스북 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSuccessLogin() {
        if(auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String, Any>()
        user["UserId"] = userId
        currentUserDB.updateChildren(user)

        finish()
    }
}