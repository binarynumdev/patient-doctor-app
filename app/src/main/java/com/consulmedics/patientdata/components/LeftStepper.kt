package com.consulmedics.patientdata.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.consulmedics.patientdata.R
import com.consulmedics.patientdata.components.models.StepItem
import com.consulmedics.patientdata.databinding.ItemLeftStepperBinding

class LeftStepper @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0) : RecyclerView(context, attrs, defStyle) {

    fun initStepper(leftStepperAdapter: LeftStepperAdapter) {
        layoutManager = LinearLayoutManager(context)
        adapter = leftStepperAdapter
    }

    fun setCurrentIndex(i: Int) {
        val leftStepperAdapter = adapter as LeftStepperAdapter
        leftStepperAdapter.setCurrentInex(i)
    }

}
class LeftStepperAdapter(
    private val mContext: Context,
    val stepperCallback: StepperCallback
) :
    RecyclerView.Adapter<LeftStepperAdapter.ViewHolder>(){
    private val menuItems = ArrayList<StepItem>()
    private var currentIndex = 0
    inner class ViewHolder(val itemBinding: ItemLeftStepperBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding  = ItemLeftStepperBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuTitle = menuItems.get(position)
        if(currentIndex == position){
//            holder.itemBinding.root.setBackgroundColor(mContext.resources.getColor(R.color.app_orange))
            holder.itemBinding.rootLayout.background = mContext.resources.getDrawable(R.drawable.bg_orange_circle)
            holder.itemBinding.stepTitle.setTextColor(mContext.resources.getColor(R.color.white))
            holder.itemBinding.stepDescription.setTextColor(mContext.resources.getColor(R.color.white))
        }
        else{
            holder.itemBinding.rootLayout.background = mContext.resources.getDrawable(R.drawable.bg_default_circular)
            holder.itemBinding.stepTitle.setTextColor(mContext.resources.getColor(R.color.white))
            holder.itemBinding.stepDescription.setTextColor(mContext.resources.getColor(R.color.gray_400))
        }
        holder.itemBinding.stepTitle.text = "Step ${position + 1}"
        holder.itemBinding.stepDescription.text = menuTitle.stepTitle
        holder.itemBinding.rootLayout.setOnClickListener {
            stepperCallback.onStepItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    fun updateList(pageTitleList: List<StepItem>) {
        menuItems.clear()
        menuItems.addAll(pageTitleList)
        notifyDataSetChanged()
    }

    fun setCurrentInex(i: Int) {
        currentIndex = i
        notifyDataSetChanged()
    }


}