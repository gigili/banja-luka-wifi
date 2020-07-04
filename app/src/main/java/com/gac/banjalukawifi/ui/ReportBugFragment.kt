package com.gac.banjalukawifi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.helpers.AppInstance
import com.gac.banjalukawifi.helpers.ProgressDialogHelper
import com.gac.banjalukawifi.helpers.network.VolleyTasks
import kotlinx.android.synthetic.main.fragment_report_bug.*

class ReportBugFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_report_bug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSave.setOnClickListener {
            val name = edtBugAuthorName.text.toString()
            val email = edtBugAuthorEmail.text.toString()
            val bug = edtBugDescription.text.toString()
            var canSubmit = true

            edtBugAuthorName.error = null
            edtBugDescription.error = null

            if (name.isBlank() || name == "-1") {
                edtBugAuthorName.error = getString(R.string.field_required)
                canSubmit = false
            }

            if (bug.isBlank() || bug == "-1") {
                edtBugDescription.error = getString(R.string.field_required)
                canSubmit = false
            }

            if (canSubmit) {
                ProgressDialogHelper.showProgressDialog(requireActivity())
                VolleyTasks.submitBugReport(name, email, bug, Response.Listener { response ->
                    try {
                        AppInstance.globalConfig.logMsg("Response: $response")
                        if (!response.contains("error")) {
                            AppInstance.globalConfig.showMessageDialog(getString(R.string.bug_reported_success), getString(R.string.notice))
                        } else {
                            AppInstance.globalConfig.showMessageDialog(getString(R.string.bug_reported_error))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppInstance.globalConfig.showMessageDialog(getString(R.string.bug_reported_error))
                    } finally {
                        ProgressDialogHelper.hideProgressDialog()
                    }
                }, Response.ErrorListener { error ->
                    ProgressDialogHelper.hideProgressDialog()
                    AppInstance.globalConfig.handleExceptionErrors(error)
                })
            }
        }
    }
}