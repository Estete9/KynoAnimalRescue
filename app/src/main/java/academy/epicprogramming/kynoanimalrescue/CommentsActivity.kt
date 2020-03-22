package academy.epicprogramming.kynoanimalrescue

import academy.epicprogramming.kynoanimalrescue.Model.User
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private var postId = ""
    private var publisherId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        userInfo()
        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comment.text.toString() == "") {
                Toast.makeText(this@CommentsActivity, "enter a comment first", Toast.LENGTH_LONG)
                    .show()
            } else {
                addComment()
            }
        })
    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        val commentMap = HashMap<String, Any>()
        commentMap["comment"] = add_comment!!.text.toString()
        commentMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentMap)

        add_comment!!.text.clear()

    }

    private fun userInfo() {
        val usersRef =
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_comment)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}
