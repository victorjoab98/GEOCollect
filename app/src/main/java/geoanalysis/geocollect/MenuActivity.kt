package geoanalysis.geocollect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*
import android.content.Intent
class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)



        btnNewMuestra.setOnClickListener{
            val intent = Intent(this, MuestraActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener(){
            clearSesionPreference()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun clearSesionPreference(){
        val preference = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("sesion_activa", false)
        editor.apply()

    }
}
