package geoanalysis.geocollect;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class conexionJava {
    private static RequestQueue requestQueue;
    private static Context ctx;}
    //String imagen = getStringImagen(bitmap);


/*
    static void buscarEtiqueta(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        System.out.println("Se encontro");
                        System.out.println("El id es: "+jsonObject.getString("id"));

                    } catch (JSONException e) {
                      System.err.println(e.getMessage());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               System.err.println("ERROR EN LA CONEXION");
            }
        });
        requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
}
*/