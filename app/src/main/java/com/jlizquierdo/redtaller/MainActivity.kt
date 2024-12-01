package com.jlizquierdo.redtaller

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.jlizquierdo.redtaller.databinding.ActivityMainBinding
import com.jlizquierdo.redtaller.datos.ClientesHttpClient
import com.jlizquierdo.redtaller.datos.Preferencias as Preferencias

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val preferencias = Preferencias()
        val sharedPreferences = preferencias.getEncryptedPreferences(this)
        var username = sharedPreferences.getString("username","")
        val clientesHttpClient = ClientesHttpClient(this)
        clientesHttpClient.obtenerClientes(nif = username) { clientes, error ->
            if (clientes != null && clientes.isNotEmpty()) {
                val nombreCompleto = "$username\n${clientes[0].nombre}"
                val headerView = navView.getHeaderView(0)
                val usernameTextView = headerView.findViewById<TextView>(R.id.usuarioConectado)
                usernameTextView.text = nombreCompleto
            } else {
                Log.e("HTTP", "Error al obtener cliente o lista vacía")
                Log.e("HTTP", error.toString())
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}