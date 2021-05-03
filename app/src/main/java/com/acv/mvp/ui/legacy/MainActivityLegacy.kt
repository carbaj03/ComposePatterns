package com.acv.mvp.ui.legacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.acv.mvp.databinding.MainBinding
import com.acv.mvp.domain.Task
import com.acv.mvp.presentation.State
import com.acv.mvp.presentation.Store
import com.acv.mvp.presentation.ViewStore

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

        binding.btnAdd.setOnClickListener {
            adapter.add(
                Task(
                    id = adapter.itemCount + 1,
                    task = binding.tietTask.text.toString(),
                )
            )
            binding.tietTask.setText("")
        }

        store.onCreate(this)
    }

    override fun State.render() {
        adapter.addAll(tasks.tasks)
        binding.tietTask.setText(input)
    }
}