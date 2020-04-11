package geoanalysis.geocollect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE )
        val session = preferences.getBoolean("sesion_activa", false)
        if(session){
            goToMenu()
        }
        btnLogin.setOnClickListener{
            createSesionReference()
            goToMenu()
        }
    }

    private fun goToMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent);
        finish()
    }

    private fun createSesionReference(){
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE )
        val editor = preferences.edit()
        editor.putBoolean("sesion_activa", true)
        editor.apply()
    }


}
