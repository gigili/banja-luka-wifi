package com.gac.banjalukawifi.helpers

import android.app.Activity
import android.app.AlertDialog
import com.gac.banjalukawifi.R

class ProgressDialogHelper {

    companion object {

        private var progressDialog: AlertDialog? = null

        fun showProgressDialog(
            activity: Activity, message_res: Int? = null, cancelable: Boolean = false,
            messageString: String? = null
        ) {

            if (progressDialog != null) {
                hideProgressDialog()
            }

            progressDialog = AlertDialog
                .Builder(activity)
                .setView(R.layout.loading)
                .setCancelable(cancelable)
                .create()

            if (message_res != null) {
                progressDialog!!.setMessage(activity.getString(message_res))
            }

            if (messageString != null) {
                progressDialog!!.setMessage(messageString)

            }

            if (message_res == null && messageString == null)
                progressDialog!!.setMessage(activity.getString(R.string.please_wait))


            if (!activity.isFinishing && progressDialog != null) {
                progressDialog?.apply {
                    show()
                }
            }
        }

        fun hideProgressDialog() {

            if (progressDialog != null) {
                progressDialog?.dismiss()
                progressDialog?.cancel()
                progressDialog = null
            }
        }

    }
}
