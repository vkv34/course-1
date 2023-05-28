package com.example.products

import android.app.Application
import io.paperdb.Paper

class App: Application(){

    override fun onCreate() {
        super.onCreate()

        Paper.init(this)
    }

}