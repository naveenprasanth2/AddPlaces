package com.androstays.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androstays.happyplaces.activities.AddHappyPlacesActivity
import com.androstays.happyplaces.activities.MainActivity
import com.androstays.happyplaces.databinding.ItemHappyPlaceBinding
import com.androstays.happyplaces.model.HappyPlacesModel

open class HappyPlacesAdapter(
    private val context: Context,
    private val list: ArrayList<HappyPlacesModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //created private onclick variable
    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(binding: ItemHappyPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
        val ivPlaceImage = binding.ivPlaceImage
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemHappyPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.ivPlaceImage.setImageURI(Uri.parse(model.image))
            holder.tvTitle.text = model.title
            holder.tvDescription.text = model.description
            //making onclick in recycler view
            holder.itemView.setOnClickListener {
                onClickListener?.onClick(position, model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //created onClickListener interface
    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlacesModel) {

        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener

    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlacesActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }
}