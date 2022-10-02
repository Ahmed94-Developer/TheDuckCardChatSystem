package com.example.theduckcardchatsystem.ui.login

import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.EditText
import com.example.theduckcardchatsystem.viewmodel.EnterCodeViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import butterknife.ButterKnife
import androidx.lifecycle.ViewModelProvider
import android.text.TextWatcher
import android.text.Editable
import android.view.View
import androidx.fragment.app.Fragment
import com.example.theduckcardchatsystem.ui.login.LoginActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class EnterCodeFragment : Fragment {
    @JvmField
    @BindView(R.id.register_input)
    var registerInputTxt: EditText? = null
    var enterCodeViewModel: EnterCodeViewModel? = null
    private var phoneNumber: String? = null
    private var id: String? = null
    private var credential: PhoneAuthCredential? = null

    constructor() {
        // Required empty public constructor
    }

    constructor(phoneNumber: String?, id: String?) {
        this.phoneNumber = phoneNumber
        this.id = id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_enter_code, container, false)
        ButterKnife.bind(this, view)
        FirebaseApp.initializeApp(activity!!)
        enterCodeViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                activity!!.application
            )
        ).get(
            EnterCodeViewModel::class.java
        )
        registerInputTxt!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val num = registerInputTxt!!.text.toString()
                if (num.length == 6) {
                    val code = registerInputTxt!!.text.toString()
                    credential = PhoneAuthProvider.getCredential(id!!, code)
                    val activity = activity as LoginActivity?
                    enterCodeViewModel!!.EnterCode(credential, phoneNumber, activity)
                }
            }
        })
        return view
    }
}