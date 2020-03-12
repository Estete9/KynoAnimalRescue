package academy.epicprogramming.kynoanimalrescue

import academy.epicprogramming.kynoanimalrescue.Login.SignInActivity
import academy.epicprogramming.kynoanimalrescue.Model.User
import academy.epicprogramming.kynoanimalrescue.fragments.ProfileFragment
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, ProfileFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        close_profile_btn.setOnClickListener {

            val intent = Intent(this@AccountSettingsActivity, ProfileFragment::class.java)
            startActivity(intent)
            finish()
        }

        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {

            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun updateUserInfoOnly() {

        when {
            full_name_profile_frag.text.toString() == "" -> Toast.makeText(
                this,
                "Please enter your name",
                Toast.LENGTH_LONG
            ).show()

            username_profile_frag.text.toString() == "" -> Toast.makeText(
                this,
                "Please enter your username",
                Toast.LENGTH_LONG
            ).show()

            bio_profile_frag.text.toString() == "" -> {
                bio_profile_frag.setText("Estoy usando Kyno animal rescue")
                val usersRef =
                    FirebaseDatabase.getInstance().reference.child("Users")
                val userMap = HashMap<String, Any>()
                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString()
                userMap["bio"] = bio_profile_frag.text.toString()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account information successfully updated", Toast.LENGTH_LONG)
                    .show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            else -> {
                val usersRef =
                    FirebaseDatabase.getInstance().reference.child("Users")
                val userMap = HashMap<String, Any>()
                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account information successfully updated", Toast.LENGTH_LONG)
                    .show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun userInfo() {
        val usersRef =
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_view_profile_frag)
                    full_name_profile_frag.setText(user.getFullname())
                    username_profile_frag.setText(user.getUsername())
                    bio_profile_frag.setText(user.getBio())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}
