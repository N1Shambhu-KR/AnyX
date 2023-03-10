package com.a.anyx.content

data class FileObject(var transferStatus: TransferStatus,val path:String,val name:String,val type:Int,var currentLength:Long = 0,val maxLength:Long){

    enum class TransferStatus{

        START,IN_PROGRESS,END,ERROR,CANCELLED
    }

}
