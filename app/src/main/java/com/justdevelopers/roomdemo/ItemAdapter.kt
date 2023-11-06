package com.justdevelopers.roomdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.justdevelopers.roomdemo.databinding.ItemsRowBinding

class ItemAdapter(private val items:ArrayList<EmployeeEntity>,
                  private val updateListener:(id:Int)->Unit,
                  private val deleteListener:(id:Int)->Unit
                    ):RecyclerView.Adapter<ItemAdapter.MainHolder>() {
    inner class MainHolder(itemView: ItemsRowBinding) : RecyclerView.ViewHolder(itemView.root){
        val llMain = itemView.llMain
        val tvName = itemView.tvName
        val tvEmail = itemView.tvEmail
        val ivEdit = itemView.ivEdit
        val ivDelete = itemView.ivDelete


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(ItemsRowBinding.inflate(LayoutInflater
            .from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvEmail.text = item.email
        if (position%2==0){
            holder.llMain.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context,
            R.color.colorLightGray))
        }else{
            holder.llMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
            R.color.white))
        }
        holder.ivEdit.setOnClickListener{
            updateListener.invoke(item.id)
        }
        holder.ivDelete.setOnClickListener{
            deleteListener.invoke(item.id)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}