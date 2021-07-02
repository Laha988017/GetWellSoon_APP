package com.example.getwellsoon.staticClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.example.getwellsoon.R

class DialogCaller{
    companion object {
        fun positiveDialog(context: Context, message:String?, positiveBtn:String="Ok"){
            val dialog = AlertDialog.Builder(context)
            dialog.setMessage(message)
            dialog.setPositiveButton(positiveBtn, null)
            dialog.show()
        }

        fun positiveActionDialog(context: Context, message:String?, onClickListener: DialogInterface.OnClickListener, positiveBtn:String="Ok"){
            val dialog = AlertDialog.Builder(context)
            dialog.setMessage(message)
            dialog.setPositiveButton(positiveBtn, onClickListener)
            dialog.show()
        }

        fun yesNoActionDialog(context: Context, message:String?, yesOnClickListener: DialogInterface.OnClickListener){
            val dialog = AlertDialog.Builder(context)
            dialog.setMessage(message)
            dialog.setPositiveButton("Yes", yesOnClickListener)
            dialog.setNegativeButton("No",null)
            dialog.show()
        }

        fun alertDialog(context: Context, message:String?){
            val dialog = AlertDialog.Builder(context)
            dialog.setMessage(message)
            dialog.setIcon(R.drawable.ic_alert)
            dialog.setTitle("Alert")
            dialog.setPositiveButton("Ok, I Understand", null)
            dialog.show()
        }

        fun alertActionDialog(context: Context, message:String?, OnClickListener: DialogInterface.OnClickListener){
            val dialog = AlertDialog.Builder(context)
            dialog.setMessage(message)
            dialog.setIcon(R.drawable.ic_alert)
            dialog.setTitle("Alert")
            dialog.setPositiveButton("Ok, I Understand", OnClickListener)
            dialog.show()
        }
    }
}