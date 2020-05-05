package geoanalysis.geocollect

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_newmuestra.*
import kotlinx.android.synthetic.main.activity_resume.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
/*Esta clase maneja el layout de newMuestra en el que se insertan los datos para una nueva muestra
* asi como tambien el layout de resume en el cual muesta los datos ingresados y tiene un boton para
* enviar la muestra*/

class MuestraActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    private var requestQueue: RequestQueue? = null
    private var bitmap: Bitmap? = null
    private var foto: Uri? = null

    private val REQUEST_GALERIA = 1001
    private val REQUEST_CAMERA = 1002
    private val REQUEST_GPS = 1003
    private var existEtiqueta: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muestra)

        //Se coloca la fecha y hora en etDate del layout newMuestra
        val sdf = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        etDate.setText(currentDate)
        imageView.setTag("imgDefault")//un tag para la imagen inicial del layout

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)//para el cliente proveedor del GPS

        //los metodos relacionados con los botones de los layouts
        btnCamera_CLICK()
        btnGaleria_CLICK()
        btnGPS_CLICK()
        btnEnviarMuestra_CLICK()

        //Metodo para el boton Hecho
        // Se revisa que los campos tengan contenido y se hace un resumen de la informacion dada
        btnHecho.setOnClickListener(){
            when{
                imageView.getTag().toString() == "imgDefault" -> {
                    Snackbar.make(newMuestraLinearLayout,
                        "Debes establecer una imagen para continuar", Snackbar.LENGTH_SHORT).show()
                }
                etDate.text.toString().isEmpty()->{
                    Snackbar.make(newMuestraLinearLayout,
                        "La fecha esta vacia", Snackbar.LENGTH_SHORT).show()
                }
                etGPS.text.toString().isEmpty()->{
                    Snackbar.make(newMuestraLinearLayout,
                        "Debes Ingresar una", Snackbar.LENGTH_SHORT).show()
                }
                else->{
                    var etiqueta : String = buscarEtiqueta()//recibe una etiqueta que no este repetida en la bd
                    mostrarResumen(etiqueta)//muestra el siguiente layout mostrando un resumen de los datos ingresados
                }
            }
        }
    }

    //metodo cuando se de click a ENVIAR
    private fun btnEnviarMuestra_CLICK(){
        btnEnviarMuestra.setOnClickListener(){
            insertMuestra()
        }
    }

    //Coloca datos del primer layout en el segundo para mostrar un resumen
    private fun mostrarResumen(etiqueta:String){
        tvConfirmarEtiqueta.text = etiqueta;
        tvConfirmarFechayHora.text = etDate.text
        tvConfirmarUbicacion.text = etGPS.text
        tvConfirmarObservacion.text = etObservacion.text
        //oculta el layout de ingreso para mostrar el de resumen
        newMuestraLinearLayout.visibility = View.GONE
        lyResume.visibility = View.VISIBLE
    }

    //Pregunta si de verdad desea salir del enviar muestra
    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Seguro que desea Salir. Si sale se perderan los datos ingresados")
        dialog.setPositiveButton("Salir"){_, _ ->
            finish()
        }
        dialog.setNegativeButton("No Salir"){dialog, _ ->
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    //al dar click en abrir galeria
    private fun btnGaleria_CLICK(){
        btnGalery.setOnClickListener(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//ver la version de android
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permisosArchivos = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisosArchivos,REQUEST_GALERIA)//si no tiene permiso para leer, pide
                }else
                    abreGaleria()
            }else
                abreGaleria() //En versiones anteriores tiene permisos por default
        }
    }

    //abre la galeria de fotos
    private fun abreGaleria(){
        val intentGaleria = Intent(Intent.ACTION_PICK)
        intentGaleria.type = "image/*"
        startActivityForResult(intentGaleria,REQUEST_GALERIA)
    }

    //al dar click en Usar Camara
    private fun btnCamera_CLICK(){
        btnCamera.setOnClickListener(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //Si no tiene permisos para la Camara y escribir datos los pide
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permisosCamara = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    requestPermissions(permisosCamara, REQUEST_CAMERA)
                }else
                    abreCamara()
            }else
                abreCamara()
        }
    }

    //abre la camara del celeular
    private fun abreCamara(){
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE, "Nueva Imagen")
        foto = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
        val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, foto)
        startActivityForResult(camaraIntent, REQUEST_CAMERA)
    }

    //al dar click en Usar GPS
    private fun btnGPS_CLICK(){
        btnGPS.setOnClickListener(){
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                alertaUbicacion()
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                        val permisoGPS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        requestPermissions(permisoGPS, REQUEST_GPS)
                }else
                    obtieneUbicacion()
            }else
                obtieneUbicacion()
        }
    }

    //Avisa que la "ubicacion" esta desactivada
    private fun alertaUbicacion(){
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("La ubicacion esta desactivada. Habilite la ubicación para usar la localizcion GPS")
        dialog.setPositiveButton("Configuracion"){_, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        dialog.setNegativeButton("Cancelar"){_, _ ->
            finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    //toma la Ubicacion GPS
    @SuppressLint("SetTextI18n")
    private fun obtieneUbicacion(){
        fusedLocationProviderClient.lastLocation.
                addOnSuccessListener {
                    location: Location? ->
                        if(location!= null){//solo si ya se conoce la ubicacion
                            etGPS.setText(location.latitude.toString()+"/"+location.longitude.toString())
                        }else{
                            buildLocationRequest()
                            buildLocationCallBack()
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                        }
                }
    }

    //es un metodo para que define el Request de ubicacion
    private fun buildLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    //obtiene la nueva ultima ubicacion conocida
    private fun buildLocationCallBack(){
        locationCallback = object :LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations.get(p0!!.locations.size - 1)
                etGPS.setText(location.latitude.toString()+"/"+location.longitude.toString())//la coloca en etGPS del layout
            }
        }
    }

    //callbacks de las operaciones para poner la foto en imageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //si se uso la galeria
        if(resultCode == Activity.RESULT_OK && requestCode==REQUEST_GALERIA){
            val filePath = data!!.data
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            imageView.setImageBitmap(bitmap)
            imageView.rotation
            imageView.tag = "anyName"
        }
        //si se uso la camara
        if(resultCode == Activity.RESULT_OK && requestCode==REQUEST_CAMERA){
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), foto);
            imageView.setImageBitmap(bitmap)
            imageView.tag = "anyName"
        }
    }

    //callbacks de los permisos para camara, galeria y GPS
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_GALERIA ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    abreGaleria();
                else
                    Toast.makeText(applicationContext, "No se puede acceder a tus imagenes", Toast.LENGTH_SHORT).show()
            }
            REQUEST_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    abreCamara()
                else
                    Toast.makeText(applicationContext, "No se puede acceder a la camara", Toast.LENGTH_SHORT).show()
            }
            REQUEST_GPS -> {
                if(grantResults.size > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "Se accedio al GPS ", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "No se puede acceder al GPS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Hace una consulta para ver si ya existe la etiqueta que devolvio getRandomString()
     private fun buscarEtiqueta(): String{
         var etiqueta : String = getRandomString(6)
        val stringRequest = object : StringRequest(Request.Method.GET, "http://192.168.1.109/WebServicePHP/consultarEtiqueta.php?etiqueta="+etiqueta,
            Response.Listener { response->
                try {
                    Toast.makeText(this, "Guarde la Etiqueta de la Muestra ", Toast.LENGTH_SHORT).show()
                    etiqueta = getRandomString(6)
                }catch (e: Exception) {
                    e.printStackTrace()
                }
        }, Response.ErrorListener {existEtiqueta=0}){}

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this)
        }
        requestQueue?.add(stringRequest)

         return etiqueta
    }

    //genera una cadena alfanumerica para la etiqueta de la muestra
    private fun getRandomString(length: Int) : String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZ123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    //Sube la muestra a la base de datos
    private fun insertMuestra() {
        progress_circularResume.visibility = View.VISIBLE //una recurso que se muestra mientras este metodo esta operando
        //se define un StrinRequest con el tipo de Metodo, la ruta y las acciones correspondientes
        val stringRequest = object : StringRequest(Request.Method.POST,
            "http://192.168.1.109/WebServicePHP/insertarMuestra.php",//ruta del web service insertarMuestra
            Response.Listener { response ->
                try {
                    //si se logra enviar muestraun mensaje de exito y oculta el recurso progress
                    Toast.makeText(this, "Se ha ingresado una nueva muestra", Toast.LENGTH_SHORT)
                        .show()
                    progress_circularResume.visibility = View.INVISIBLE
                } catch (e: Exception) {
                    Toast.makeText(this, "Ha ocurrido un error al insertar la muestra", Toast.LENGTH_SHORT)
                        .show()
                }
            },Response.ErrorListener{Toast.makeText(this, "Error al ingresar nueva muestra", Toast.LENGTH_SHORT).show()}){

            //se encapsulan los parametros que se enviaran al web service
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                val img = getStringImagen(bitmap)//se recibe la imagen como una cadena
                params["etiqueta"] = tvConfirmarEtiqueta.text.toString()
                params["fecha"] = tvConfirmarFechayHora.text.toString()
                params["ubicacion"] = tvConfirmarUbicacion.text.toString()
                params["imagen"] = img
                params["observacion"] = tvConfirmarObservacion.text.toString()
                return params
            }
        }
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this)//se crea una nueva request
        }
        requestQueue?.add(stringRequest)//se le asigna el StringRequest

    }

    //convierte la imagen Bitmap en un string que se pueda enviar al servidor
    fun getStringImagen(bmp: Bitmap?): String {
        val baos = ByteArrayOutputStream()
        bmp?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

}



