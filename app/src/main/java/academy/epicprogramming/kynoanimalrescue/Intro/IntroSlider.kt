package academy.epicprogramming.kynoanimalrescue.Intro

import academy.epicprogramming.kynoanimalrescue.Login.SignInActivity
import academy.epicprogramming.kynoanimalrescue.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSlider : AppCompatActivity() {
    private val TAG = "IntroSlider"

    private lateinit var preference: SharedPreferences
    private val prefShowIntro = "Intro"

    private val introSliderAdapter =
        IntroSliderAdapter(
            listOf(
                IntroSlide(
                    "Encontrados",
                    "Si se te perdio tu perrito, aqui podria estar",
                    R.drawable.found_resized
                ),
                IntroSlide(
                    "Perdidos",
                    "No desesperes, comparte aqui tu perrito perdido",
                    R.drawable.lost_resized
                ),
                IntroSlide(
                    "Adopcion",
                    "Si quieres adoptar, este es el lugar indicado",
                    R.drawable.adopt_resized
                )
            )
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)



        preference = getSharedPreferences("IntroSlider", Context.MODE_PRIVATE)
        if (!preference.getBoolean(
                prefShowIntro,
                true
            ) && FirebaseAuth.getInstance().currentUser != null
        ) {
            goToSignIn()
        }

        introSliderViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)
        introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        buttonNextIntro.setOnClickListener {

            Log.d(TAG, introSliderViewPager.currentItem.toString())
            Log.d(TAG, "introSliderAdapter.itemCount: ${introSliderAdapter.itemCount}--------------------------------")


            if (introSliderViewPager.currentItem + 1 == introSliderAdapter.itemCount -1 ) {
                buttonNextIntro.text = "Done"
                introSliderViewPager.currentItem += 1
            }
             else {
                if (introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount) {
                    introSliderViewPager.currentItem += 1

                    Log.d(TAG, introSliderViewPager.currentItem.toString())
                    Log.d(TAG, introSliderAdapter.itemCount.toString())
                } else {
                    goToSignIn()
                }

            }
        }

        textSkipIntro.setOnClickListener {
            goToSignIn()
        }


    }

    private fun goToSignIn() {
        Intent(applicationContext, SignInActivity::class.java).also {
            startActivity(it)
            finish()
            val editor = preference.edit()
            editor.putBoolean(prefShowIntro, false)
            editor.apply()
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active

                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )

            }
        }
    }
}
