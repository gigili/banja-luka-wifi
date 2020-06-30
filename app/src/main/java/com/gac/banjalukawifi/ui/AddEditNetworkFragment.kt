package com.gac.banjalukawifi.ui

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.entities.Network
import kotlinx.android.synthetic.main.fragment_add_edit_network.*

class AddEditNetworkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_edit_network, container, false)

        val appDB = AppDatabase.getDatabase(requireContext().applicationContext)

        val mBntSave = view.findViewById<Button>(R.id.btnSave)
        mBntSave.setOnClickListener {
            val network = Network(
                edtNetworkName.text.toString(),
                edtNetworkPassword.text.toString(),
                edtNetworkAddress.text.toString(),
                "0",
                "0",
                ""
            )

            AsyncTask.execute {
                appDB.networkDao().insert(network)
            }
        }

        return view
    }
}