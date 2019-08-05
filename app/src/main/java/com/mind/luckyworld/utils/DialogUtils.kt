package com.mind.luckyworld.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.mind.luckyworld.R

fun showDialog(
    context: Context,
    message: String,
    positiveBtnName: String?,
    negativeBtnName: String?,
    positiveBtnListener: DialogInterface.OnClickListener?,
    negativeBtnListener: DialogInterface.OnClickListener?
): Dialog? {
    val dialog: Dialog?
    val builder = AlertDialog.Builder(context, R.style.dialogTheme)
    builder.setMessage(message)
    if (positiveBtnName != null && positiveBtnListener != null)
        builder.setPositiveButton(positiveBtnName, positiveBtnListener)
    if (negativeBtnName != null && negativeBtnListener != null)
        builder.setNegativeButton(negativeBtnName, negativeBtnListener)
    dialog = builder.create()

    return dialog
}

