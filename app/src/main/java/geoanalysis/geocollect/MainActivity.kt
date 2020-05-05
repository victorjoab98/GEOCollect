package geoanalysis.geocollect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
/*Main class*/
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //para mantener abierta la sesion una vez se haya ingresado; No se implemento a nivel de servidor
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

    //Ir al activity de Menu
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
