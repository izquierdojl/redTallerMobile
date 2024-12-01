package com.jlizquierdo.redtaller.datos

import com.jlizquierdo.redtaller.modelo.Taller
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.io.encoding.Base64

class TalleresHttpClient {

    class TalleresHttpClient {

        private val apiUrl = "https://redtaller.jlizquierdo.com/api/talleres.php"

        fun getTalleres( bearer: String, user: String, password: String ): List<Taller> {

            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection

            return try {

                val basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString("$user:$password".toByteArray())

                connection.requestMethod = "POST"
                connection.setRequestProperty( "Authorization" , basicAuth )
                connection.setRequestProperty( "Token" , "Bearer $bearer" )
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = reader.readText()
                    reader.close()
                    parseTalleresJson(response)
                } else {
                    emptyList()
                }
            } finally {
                connection.disconnect()
            }
        }

        private fun parseTalleresJson(jsonString: String): List<Taller> {

            val talleresList = mutableListOf<Taller>()
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

            return talleresList
        }
    }

}