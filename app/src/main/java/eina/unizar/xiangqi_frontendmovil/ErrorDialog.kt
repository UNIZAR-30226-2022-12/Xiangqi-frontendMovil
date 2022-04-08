package eina.unizar.xiangqi_frontendmovil

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ErrorDialog(m : String) : DialogFragment() {
    lateinit var msg : String
    init{
        msg = m
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(msg)
                .setNegativeButton("okay", DialogInterface.OnClickListener {
                        dialog, id ->
                    getDialog()?.cancel()
                })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
