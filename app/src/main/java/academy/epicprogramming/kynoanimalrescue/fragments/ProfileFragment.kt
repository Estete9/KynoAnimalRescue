package academy.epicprogramming.kynoanimalrescue.fragments


import academy.epicprogramming.kynoanimalrescue.AccountSettingsActivity
import academy.epicprogramming.kynoanimalrescue.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.edit_account_settings_button.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        return view
    }


}
