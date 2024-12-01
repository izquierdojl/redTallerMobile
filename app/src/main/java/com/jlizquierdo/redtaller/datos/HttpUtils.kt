package com.jlizquierdo.redtaller.datos

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.content.Context

object HttpUtils {

    private const val API_URL = "https://redtaller.jlizquierdo.com/api/login.php"
    private const val BEARER_URL = "https://redtaller.jlizquierdo.com/security/jwt.php"

    fun check(user: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        getBearer(context) { bearer ->
            if (bearer.isNullOrEmpty()) {
                callback(false)
                return@getBearer
            }

            // Crear la solicitud POST con Volley
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
}
