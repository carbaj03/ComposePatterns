package com.acv.mvp.ui.legacy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.acv.mvp.databinding.MainBinding
import com.acv.mvp.domain.Task
import com.acv.mvp.domain.Tasks

class MainActivityLegacy : ComponentActivity() {

    private lateinit var binding: MainBinding

    private val tasks = Tasks(
        listOf(
            Task(id = 1, task = "Create Todo App"),
            Task(id = 2, task = "Create Post"),
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter: CustomAdapter = CustomAdapter(tasks.tasks.toMutableList())
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
    }
}