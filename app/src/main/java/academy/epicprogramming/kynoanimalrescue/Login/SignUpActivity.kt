package academy.epicprogramming.kynoanimalrescue.Login

import academy.epicprogramming.kynoanimalrescue.MainActivity
import academy.epicprogramming.kynoanimalrescue.R
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonSignInSignIn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignInActivity::class.java
                )
            )
        }
        buttonRegister.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val TAG = "CreateAccount"

        val fullname = fullNameSignUp.text.toString()
        val username = userNameSignUp.text.toString()
        val email = emailSignInSignIn.text.toString()
        val password = passwordSignInSignIn.text.toString()

        when {
            TextUtils.isEmpty(fullname) -> Toast.makeText(
                this,
                "Full name is required",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(username) -> Toast.makeText(
                this,
                "User name name is required",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "Email is required",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "Password is required",
                Toast.LENGTH_LONG
            ).show()

            else -> {

                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SIgnUp")
                progressDialog.setMessage("This might take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "auth successful")
                            saveUserInfo(fullname, username, email, progressDialog)
                            progressDialog.dismiss()

                        } else {
                            progressDialog.dismiss()
                            val message = task.exception!!.toString()
                            mAuth.signOut()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()

                        }
                    }
            }
        }
    }

    private fun saveUserInfo(
        fullName: String,
        username: String,
        email: String,
        progressDialog: ProgressDialog
    ) {

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = username.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Estoy usando Kyno animal rescue"
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/kyno-animal-rescue.appspot.com/o/default%20Images%2Fprofile-default-image.png?alt=media&token=8df0fae5-d6e2-400a-91cd-57cde52fd48a"

        usersRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                } else {
                    val message = task.exception!!.toString()
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }
    }
}
