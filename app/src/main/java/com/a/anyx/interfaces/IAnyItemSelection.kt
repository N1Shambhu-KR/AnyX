package com.a.anyx.interfaces

import com.a.anyx.content.ContentData

interface IAnyItemSelection {

    fun onSelect(item:Long)

    fun onDeselect(item: Long)
}