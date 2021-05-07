package com.acv.mvp.ui.legacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.acv.mvp.databinding.MainBinding
import com.acv.mvp.presentation.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivityLegacy : ComponentActivity() {

    private lateinit var binding: MainBinding

    private val adapter: CustomAdapter = CustomAdapter(mutableListOf())

    private val store = Store()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTask.adapter = adapter
        binding.rvTask.layoutManager = LinearLayoutManager(this)

        binding.tietTask.addTextChangedListener {
            store.action(ChangeInput(it.toString()))
            binding.tietTask.setSelection(it.toString().length)
        }

        binding.btnAdd.setOnClickListener {
            store.action(AddTask(binding.tietTask.text.toString()))
        }

        lifecycleScope.launch {
            store.state.collect {
                it.render()
            }
        }

        store.action(LoadTasks)
    }

    private fun State.render() {
//        adapter.addAll(tasks.tasks)
//        binding.tietTask.setText(input)
    }
}