package geoanalysis.geocollect

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

    }
}
