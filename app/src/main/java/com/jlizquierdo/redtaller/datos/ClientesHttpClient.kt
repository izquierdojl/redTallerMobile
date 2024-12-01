package com.jlizquierdo.redtaller.datos

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.jlizquierdo.redtaller.modelo.Cliente
import org.json.JSONArray

class ClientesHttpClient(private val context: Context) {

    private val queue: RequestQueue = Volley.newRequestQueue(context)

    fun obtenerClientes(nif: String? = null, onResult: (List<Cliente>?, String?) -> Unit) {
        // URL según si se pasa o no el NIF
        val url = if (nif != null) {
            "https://redtaller.jlizquierdo.com/api/clientes.php?nif=$nif"
        } else {
            "https://redtaller.jlizquierdo.com/api/clientes.php"
        }


        HttpUtils.getBearer(context) { bearer ->
            if (bearer.isNullOrEmpty()) {
                onResult(null, "Token Bearer no disponible")
                return@getBearer
            }

            val preferencias = Preferencias()
            val sharedPreferences = preferencias.getEncryptedPreferences(context)
            var username = sharedPreferences.getString("username","")
            var password = sharedPreferences.getString("password","")
            val basicAuth = "Basic ${android.util.Base64.encodeToString("$username:$password".toByteArray(), android.util.Base64.NO_WRAP)}"
            val jsonArrayRequest = object : JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    val clientes = parseClientes(response)
                    onResult(clientes, null)
                },
                Response.ErrorListener { error ->
                    Log.e("VolleyError", error.toString())
                    Log.d("ClientesHttpClient", "Basic Auth: $basicAuth")
                    Log.d("ClientesHttpClient", "Bearer: $bearer")
                    onResult(null, "Error al obtener los clientes: ${error.message}")
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = mutableMapOf<String, String>()
                    headers["Token"] = "Bearer $bearer"
                    headers["Authorization"] = basicAuth
                    return headers
                }
            }

            // Añadir la solicitud a la cola de Volley
            queue.add(jsonArrayRequest)
        }
    }

    private fun parseClientes(response: JSONArray): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        for (i in 0 until response.length()) {
            val jsonObject = response.getJSONObject(i)
            val cliente = Cliente(
                nif = jsonObject.getString("nif"),
                nombre = jsonObject.getString("nombre"),
                domicilio = jsonObject.getString("domicilio"),
                cp = jsonObject.getString("cp"),
                pob = jsonObject.getString("pob"),
                pro = jsonObject.getString("pro"),
                tel = jsonObject.getString("tel"),
                email = jsonObject.getString("email"),
                movil = jsonObject.getString("movil"),
                id = jsonObject.getInt("id")
            )
            clientes.add(cliente)
        }
        return clientes
    }
}
