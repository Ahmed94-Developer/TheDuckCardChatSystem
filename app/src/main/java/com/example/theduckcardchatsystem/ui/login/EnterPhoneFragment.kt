package com.example.theduckcardchatsystem.ui.login

import android.content.Context
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.EditText
import com.example.theduckcardchatsystem.viewmodel.EnterPhoneViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import butterknife.ButterKnife
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import androidx.lifecycle.ViewModelProvider
import butterknife.OnClick
import android.widget.Toast
import com.example.theduckcardchatsystem.ui.login.LoginActivity
import android.content.SharedPreferences
import android.view.View
import androidx.fragment.app.Fragment

class EnterPhoneFragment : Fragment() {
    @JvmField
    @BindView(R.id.code_txt)
    var codeTxt: EditText? = null

    @JvmField
    @BindView(R.id.register_input_phone_number)
    var registerInputPhoneNumber: EditText? = null
    var enterPhoneViewModel: EnterPhoneViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_enter_phone, container, false)
        ButterKnife.bind(this, view)
        codeTxt!!.filters = arrayOf<InputFilter>(LengthFilter(4))
        registerInputPhoneNumber!!.filters = arrayOf<InputFilter>(LengthFilter(10))
        enterPhoneViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(activity!!.application)
        ).get(EnterPhoneViewModel::class.java)
        return view
    }

    @OnClick(R.id.register_btn_next)
    fun btnRegisterOnClick() {
        if (registerInputPhoneNumber!!.text.toString().isEmpty()
            || codeTxt!!.text.toString().isEmpty()
        ) {
            Toast.makeText(activity, "Enter Correct Phone Number", Toast.LENGTH_SHORT).show()
        } else if (!codeTxt!!.text.toString().contains("+")) {
            Toast.makeText(activity, "country code should contain +", Toast.LENGTH_SHORT).show()
        } else {
            val activity = activity as LoginActivity?
            val phoneNumber = codeTxt!!.text.toString() +
                    registerInputPhoneNumber!!.text.toString()
            val editor =
                getActivity()!!.getSharedPreferences("phoneNumber", Context.MODE_PRIVATE).edit()
            editor.putString("phones", phoneNumber)
            editor.apply()
            enterPhoneViewModel!!.EnterPhone(phoneNumber, activity)
        }
    }
}