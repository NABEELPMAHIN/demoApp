package com.nabeel.udhaarkhata

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nabeel.udhaarkhata.login.LoginActivity
import com.nabeel.udhaarkhata.utils.SpAndroidUtils

class SplashScreen : AppCompatActivity() {

    val TAG = "SplashScreen"

    private val SPLASH_TIME_OUT: Long = 3000
    private var animation: Animation? = null
    private var logo: TextView? = null
    private var title_txt: TextView? = null
   // var mWaitDialog: SpWaitDialog

    internal var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        logo=findViewById(R.id.logo_img);
        refreshDataInBackGround()
    }

    fun refreshDataInBackGround() {
        val startTime = System.currentTimeMillis()

        val refreshData: AsyncTask<Void, Void, Boolean>
        refreshData = object : AsyncTask<Void, Void, Boolean>() {
            override fun onPreExecute() {
                flyIn()
            }

            override fun doInBackground(vararg args: Void): Boolean? {


                return true
            }

            override fun onPostExecute(refreshedData: Boolean?) {
                if (isFinishing)
                    return
                val endTime = System.currentTimeMillis()
                val timeElapsed = endTime - startTime
                if (SPLASH_TIME_OUT - timeElapsed > 0) {
                    Handler().postDelayed({ endSplash() }, SPLASH_TIME_OUT - timeElapsed)
                } else
                    endSplash()
            }
        }
        refreshData.execute(null, null, null)
    }

    private fun flyIn() {
        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        logo?.startAnimation(animation)

        // animation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation);
        // title_txt.startAnimation(animation);
    }

    private fun endSplash() {
        if (animation == null)
            return

        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation_back)
        logo?.startAnimation(animation)

        //animation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation_back);
        //title_txt.startAnimation(animation);

        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(arg0: Animation) {
                    val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                    SpAndroidUtils.startFwdAnimation(this@SplashScreen)
                    startActivity(intent)
                finish()


            }

            override fun onAnimationRepeat(arg0: Animation) {}

            override fun onAnimationStart(arg0: Animation) {}
        })
    }
}
