package com.a.anyx.fragment.adapter

class AdapterSelection() {

    val selection = ArrayList<Long>()

    fun setNew(arrayList: ArrayList<Long>){
        selection.clear()
        selection.addAll(arrayList)
    }

    fun setSelection(id:Long){

        if (!selection.contains(id)) selection.add(id) else selection.remove(id)
    }

    fun getSelected(id:Long):Boolean{

        return selection.contains(id)
    }
}