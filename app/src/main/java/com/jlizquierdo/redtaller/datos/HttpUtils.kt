package com.jlizquierdo.redtaller.datos

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.content.Context
import android.net.Uri
import com.android.volley.toolbox.JsonArrayRequest
import com.jlizquierdo.redtaller.modelo.Taller
import org.json.JSONArray
import java.net.URLEncoder
import java.net.URLEncoder.encode
import java.security.MessageDigest

object HttpUtils {

    private const val API_URL = "https://redtaller.jlizquierdo.com/api/login.php"
    private const val API_URL_TALLERES = "https://redtaller.jlizquierdo.com/api/talleres.php"
    private const val BEARER_URL = "https://redtaller.jlizquierdo.com/security/jwt.php"

    fun check(user: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        getBearer(context) { bearer ->
            if (bearer.isNullOrEmpty()) {
                callback(false)
                return@getBearer
            }

            val basicAuth = "Basic ${android.util.Base64.encodeToString("$user:$password".toByteArray(), android.util.Base64.NO_WRAP)}"
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, API_URL, null,
                Response.Listener { response ->
                    try {
                        val result = response.getString("result")
                        if ("OK" == result) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    } catch (e: Exception) {
                        Log.e("Login Error", "Error al procesar la respuesta", e)
                        callback(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Login Error", "Error de conexión", error)
                    callback(false)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = mutableMapOf<String, String>()
                    headers["Authorization"] = basicAuth
                    headers["Token"] = "Bearer $bearer"
                    return headers
                }
            }

            // Obtener la instancia de la cola de solicitudes
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }
    }

    // Método para obtener talleres
    fun getTalleres(context: Context, callback: (List<Taller>?) -> Unit, user: String, password: String, filter: String) {
        getBearer(context) { bearer ->
            if (bearer.isNullOrEmpty()) {
                callback(null) // Llamada al callback si el bearer es nulo
                return@getBearer
            }

            val basicAuth = "Basic ${android.util.Base64.encodeToString("$user:$password".toByteArray(), android.util.Base64.NO_WRAP)}"

            val uriBuilder = Uri.parse(API_URL_TALLERES).buildUpon()
            if (!filter.isNullOrEmpty()) {
                uriBuilder.appendQueryParameter("search", filter )
            }
            val jsonArrayRequest = object : JsonArrayRequest(Request.Method.GET, uriBuilder.toString(), null,
                Response.Listener { response ->
                    try {
                        val talleres = parseTalleresJson(response.toString()) // Parseo de los talleres
                        callback(talleres) // Llamada al callback con la lista de talleres
                    } catch (e: Exception) {
                        Log.e("Talleres Error", "Error al procesar la respuesta", e)
                        callback(null) // En caso de error, callback con null
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Talleres Error", "Error de conexión", error)
                    callback(null) // En caso de error en la conexión, callback con null
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = mutableMapOf<String, String>()
                    headers["Authorization"] = basicAuth
                    headers["Token"] = "Bearer $bearer"
                    return headers
                }
            }

            // Obtener la instancia de la cola de solicitudes
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonArrayRequest)
        }
    }

    private fun parseTalleresJson(jsonString: String): List<Taller> {
        val talleresList = mutableListOf<Taller>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val taller = Taller(
                    id = jsonObject.getInt("id"),
                    nif = jsonObject.getString("nif"),
                    nombre = jsonObject.getString("nombre"),
                    domicilio = jsonObject.getString("domicilio"),
                    cp = jsonObject.getString("cp"),
                    pob = jsonObject.getString("pob"),
                    pro = jsonObject.getString("pro"),
                    tel = jsonObject.getString("tel"),
                    email = jsonObject.getString("email"),
                    movil = jsonObject.getString("movil")
                )
                talleresList.add(taller)
            }
        } catch (e: Exception) {
            Log.e("Talleres Error", "Error al procesar el JSON", e)
        }
        return talleresList
    }

    fun getBearer(context: Context, callback: (String?) -> Unit) {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, BEARER_URL, null,
            { response ->
                try {
                    val bearer = response.getString("jwt")
                    callback(bearer)
                } catch (e: Exception) {
                    Log.e("Bearer Error", "Error al procesar el token", e)
                    callback(null)
                }
            },
            { error ->
                Log.e("Bearer Error", "Error de conexión al obtener el token", error)
                callback(null)
            })

        // Obtener la instancia de la cola de solicitudes
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        requestQueue.add(jsonObjectRequest)
    }

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

}
