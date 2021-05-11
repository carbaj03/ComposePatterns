package com.acv.mvp.ui.legacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.acv.mvp.databinding.MainBinding
import com.acv.mvp.presentation.TodosStore

class MainActivityLegacy : ComponentActivity() {

    private lateinit var binding: MainBinding

    private val adapter: CustomAdapter = CustomAdapter(mutableListOf())

    private val store by viewModels<TodosStore>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}