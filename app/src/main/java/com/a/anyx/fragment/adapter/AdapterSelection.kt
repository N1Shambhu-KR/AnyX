package com.a.anyx.fragment.adapter

class AdapterSelection(private val singleSelection:Boolean) {

    val selection = ArrayList<Long>()

    fun setNew(arrayList: ArrayList<Long>){
        selection.clear()
        selection.addAll(arrayList)
    }

    fun add(arrayList: ArrayList<Long>){
        selection.addAll(arrayList)
    }

    fun add(id:Long){
        selection.add(id)
    }

    fun setSelection(id:Long){

        if (singleSelection){
            selection.clear()
            selection.add(id)

        }else{
            if (!selection.contains(id)) selection.add(id) else selection.remove(id)
        }

    }

    fun getSelected(id:Long):Boolean{

        return selection.contains(id)
    }
}