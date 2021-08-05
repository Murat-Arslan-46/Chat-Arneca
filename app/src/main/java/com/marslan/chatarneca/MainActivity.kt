package com.marslan.chatarneca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.marslan.chatarneca.ui.main.*

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.container,LoginFragment())
            }.commit()
        }
    }

    override fun onBackPressed() {
        val myFragment = supportFragmentManager.findFragmentByTag("CHAT")
        if(myFragment  != null && myFragment.isVisible){
            supportFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                replace(R.id.container,MainFragment())
            }.commit()
            return
        }
        super.onBackPressed()
    }

}