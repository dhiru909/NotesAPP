package com.justdevelopers.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.justdevelopers.roomdemo.databinding.ActivityMainBinding
import com.justdevelopers.roomdemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var binding :ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding?.btnAdd?.setOnClickListener {
            addRecords(employeeDao =employeeDao )
        }
        lifecycleScope.launch {
            employeeDao.fetchAllEmployee().collect{
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list,employeeDao)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }
    fun addRecords(employeeDao: EmployeeDao){
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmail?.text.toString()
        if(name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext,"Record saved",Toast.LENGTH_LONG).show()
                binding?.etName?.text?.clear()
                binding?.etEmail?.text?.clear()
            }
        }else{
            Toast.makeText(applicationContext,"Name or email cannot be blank",Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListOfDataIntoRecyclerView(employeeList:ArrayList<EmployeeEntity>,
    employeeDao: EmployeeDao){
        if(employeeList.isNotEmpty()){
            val itemAdapter= ItemAdapter(employeeList,
                {
                    updateId ->
                    updateRecordDialog(updateId,employeeDao)
                },
                {
                    deleteId ->
                    deleteRecordDialog(deleteId,employeeDao)
                }
            )
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility =View.GONE

        }else{
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility =View.VISIBLE
        }
    }

    private fun updateRecordDialog(id:Int,employeeDao: EmployeeDao){
        val updateDialog = Dialog(this,R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)
        updateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect{
                try {
                    binding.etUpdateName.setText(it.name)
                    binding.etUpdateEmailId.setText(it.email)

                }catch (e:NullPointerException){
                    e.printStackTrace()
                }
            }
        }

        binding.tvUpdate.setOnClickListener {
            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()
            if(name.isNotEmpty() && email.isNotEmpty()){
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id,name = name, email = email))
                    Toast.makeText(applicationContext,"Record updated",Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }
            }else{
                Toast.makeText(applicationContext,"Name or email cannot be blank",Toast.LENGTH_LONG).show()
            }
        }
        binding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    private fun deleteRecordDialog(id:Int,employeeDao: EmployeeDao){
        try{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Record")
            lifecycleScope.launch {
                employeeDao.fetchEmployeeById(id).collect {
                    try {
                        builder.setMessage("Are you sure you want to delete ${it.name}")
                    }catch (e:NullPointerException){
                        e.printStackTrace()
                    }
                }
            }

            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { dialogInterface, _ ->
                lifecycleScope.launch {
                    employeeDao.delete(EmployeeEntity(id))
                }
                Toast.makeText(applicationContext, "Record deleted successfully", Toast.LENGTH_LONG)
                    .show()
                dialogInterface.dismiss()
            }

            builder.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()

            }
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}