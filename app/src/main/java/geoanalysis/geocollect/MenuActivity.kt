package geoanalysis.geocollect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*
import android.content.Intent
/*Esta clase maneja el layout de menu donde esta el boton
* para ingresar una nueva muestra, al dar click dirige al nuevo activity*/
class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        //inica el Activity para una nueva muestra
        btnNewMuestra.setOnClickListener{
            val intent = Intent(this, MuestraActivity::class.java)
            startActivity(intent)
        }

        //cierra sesion, no se implemento a nivel de servidor
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
