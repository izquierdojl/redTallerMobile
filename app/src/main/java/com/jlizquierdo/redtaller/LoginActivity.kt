package com.jlizquierdo.redtaller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jlizquierdo.redtaller.datos.HttpUtils
import com.jlizquierdo.redtaller.datos.Preferencias
import com.jlizquierdo.redtaller.datos.HttpUtils as CheckLogin

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val preferencias = Preferencias()
        val sharedPreferences = preferencias.getEncryptedPreferences(this)
        val usuarioPreferencias = sharedPreferences.getString("username", null)
        val passwordPreferencias = sharedPreferences.getString("password", null)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        if (!usuarioPreferencias.isNullOrEmpty()) etUsername.setText(usuarioPreferencias)

        val etPassword = findViewById<EditText>(R.id.etPassword)
        if (!passwordPreferencias.isNullOrEmpty()) etPassword.setText(passwordPreferencias)

        btnLogin.setOnClickListener {
            doLogin( etUsername.text.toString(), etPassword.text.toString(), false )
        }

        if (!usuarioPreferencias.isNullOrEmpty() && !passwordPreferencias.isNullOrEmpty())
        {
            doLogin( usuarioPreferencias, passwordPreferencias , true )
        }

    }

    private fun doLogin(username: String, password: String, auto: Boolean)
    {

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            var checkPassword = password
            if ( !auto )
                checkPassword = HttpUtils.hashPassword(password)

            CheckLogin.check(username, checkPassword, this) { success ->
                if (success) {
                    if( !auto )
                        saveCredentials(username,HttpUtils.hashPassword(password))
                    navigateToHome()
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
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
