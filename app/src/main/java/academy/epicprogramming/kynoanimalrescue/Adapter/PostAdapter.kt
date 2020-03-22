package academy.epicprogramming.kynoanimalrescue.Adapter

import academy.epicprogramming.kynoanimalrescue.CommentsActivity
import academy.epicprogramming.kynoanimalrescue.MainActivity
import academy.epicprogramming.kynoanimalrescue.Model.Post
import academy.epicprogramming.kynoanimalrescue.Model.User
import academy.epicprogramming.kynoanimalrescue.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context, private val mPost: List<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostImage()).into(holder.postImage)
        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())

        isLikes(post.getPostId(), holder.likebtn)
        numberOfLikes(holder.likes, post.getPostId())

        numberOfComments(holder.comments, post.getPostId())

        holder.likebtn.setOnClickListener {
            if (holder.likebtn.tag == "Like") {

                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            } else {

                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .removeValue()
                holder.likes.text = ""

//                val intent = Intent(mContext, MainActivity::class.java)
//                mContext.startActivity(intent)
            }
        }

        if (post.getDescription() == "") {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.getDescription()
        }

        holder.commentbtn.setOnClickListener {

            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostId())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener {

            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostId())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)

        }

    }

    private fun numberOfLikes(likes: TextView, postId: String) {
        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    likes.text = "${dataSnapshot.childrenCount} likes"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun numberOfComments(comments: TextView, postId: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    comments.text = "view all ${dataSnapshot.childrenCount} comments"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun isLikes(postId: String, likebtn: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(firebaseUser!!.uid).exists()) {
                    likebtn.setImageResource(R.drawable.heart_clicked)
                    likebtn.tag = "Liked"
                } else {
                    likebtn.setImageResource(R.drawable.heart_not_clicked)
                    likebtn.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likebtn: ImageView
        var commentbtn: ImageView
        var savebtn: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likebtn = itemView.findViewById(R.id.post_image_like_btn)
            commentbtn = itemView.findViewById(R.id.post_image_comment_btn)
            savebtn = itemView.findViewById(R.id.post_save_comment_btn)
            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)

        }
    }


    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        publisherID: String
    ) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profileImage)
                    userName.text = user.getUsername()
                    publisher.text = user.getFullname()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}