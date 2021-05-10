package com.acv.mvp.ui.legacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.acv.mvp.databinding.MainBinding
import com.acv.mvp.presentation.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivityLegacy : ComponentActivity() {

    private lateinit var binding: MainBinding

    private val adapter: CustomAdapter = CustomAdapter(mutableListOf())

    private val store by viewModels<TodosStore>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tietName.addTextChangedListener {
            store.action(ChangeName(it.toString()))
            binding.tietName.setSelection(it.toString().length)
        }

        binding.tietPhone.addTextChangedListener {
            store.action(ChangePhone(it.toString()))
            binding.tietPhone.setSelection(it.toString().length)
        }

        binding.tietMail.addTextChangedListener {
            store.action(ChangeMail(it.toString()))
            binding.tietMail.setSelection(it.toString().length)
        }

        lifecycleScope.launch {
            store.state.collect {
//                it.render()
            }
        }

        store.action(LoadForm)
    }
}