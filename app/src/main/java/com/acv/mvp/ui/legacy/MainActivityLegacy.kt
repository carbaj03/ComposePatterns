package com.acv.mvp.ui.legacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.acv.mvp.databinding.MainBinding
import com.acv.mvp.presentation.*

class MainActivityLegacy : ComponentActivity(), ViewStore {

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

        store.onCreate(this)
    }

    override fun State.render() {
        adapter.addAll(tasks.tasks)
        binding.tietTask.setText(input)
    }
}