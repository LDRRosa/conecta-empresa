package com.ifgoiano.conectaempresa

import android.app.Application
import org.osmdroid.config.Configuration

class ConectaEmpresaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configuração do OSMDroid
        Configuration.getInstance().userAgentValue = packageName
    }
}