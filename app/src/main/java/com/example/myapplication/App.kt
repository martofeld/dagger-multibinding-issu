package com.example.myapplication

import android.app.Application
import javax.inject.Inject

class App: Application() {

    @Inject
    lateinit var requestedObject: RequestedObject

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.create()
            .inject(this)
    }
}