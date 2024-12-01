package com.jlizquierdo.redtaller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jlizquierdo.redtaller.datos.Preferencias
import com.jlizquierdo.redtaller.datos.HttpUtils as CheckLogin

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val preferencias = Preferencias()
        val sharedPreferences = preferencias.getEncryptedPreferences(this)
        val usuario = sharedPreferences.getString("username", null)
        if (!usuario.isNullOrEmpty()) etUsername.setText(usuario)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                CheckLogin.check(username, password, this) { success ->
                    if (success) {
                        saveCredentials(username,password)
                        navigateToHome()
                    } else {
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveCredentials(username: String, password: String) {
        //val sharedPreferences = getEncryptedPreferences(this)
        val preferencias = Preferencias();
        val sharedPreferences = preferencias.getEncryptedPreferences(this)
        sharedPreferences.edit().apply {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onCancelClicked(view: View) {
        finish() // Cierra la actividad (si deseas salir de la aplicación)
    }

}
