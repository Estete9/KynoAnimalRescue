package academy.epicprogramming.kynoanimalrescue.fragments


import academy.epicprogramming.kynoanimalrescue.AccountSettingsActivity
import academy.epicprogramming.kynoanimalrescue.Model.User
import academy.epicprogramming.kynoanimalrescue.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid) {
            view?.edit_account_settings_button?.text = "Edit Profile"

        } else if (profileId != firebaseUser.uid) {

            checkFollowAndFollowingButtonStatus()
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.edit_account_settings_button.setOnClickListener {
            val getButtonText = view.edit_account_settings_button.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(
                    Intent(
                        context,
                        AccountSettingsActivity::class.java
                    )
                )
                getButtonText == "Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it1.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(profileId)
                            .child("Following").child(it1.toString()).setValue(true)
                    }

                }

                getButtonText == "Following" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it1.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(profileId)
                            .child("Following").child(it1.toString()).removeValue()
                    }

                }

            }

        }

        getFollowers()
        getFollowings()
        userInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef =
            FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                .child("Following")



        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(profileId).exists()) {
                        view?.edit_account_settings_button?.text = "Following"
                    } else {
                        view?.edit_account_settings_button?.text = "Follow"
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun getFollowers() {
        val followersRef =
            FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    view?.total_followers?.text = dataSnapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun getFollowings() {
        val followingsRef = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                .child("Following")
        }
        followingsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    view?.total_following?.text = dataSnapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(view?.profile_image_fragment)
                    view?.profile_fragment_username?.text = user.getUsername()
                    view?.full_name_profile_frag?.text = user.getFullname()
                    view?.bio_profile_frag?.text = user.getBio()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}
