package academy.epicprogramming.kynoanimalrescue.Intro

import academy.epicprogramming.kynoanimalrescue.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, IntroSlider::class.java)
            startActivity(intent)
            finish()
        }, 3300)
    }
}
