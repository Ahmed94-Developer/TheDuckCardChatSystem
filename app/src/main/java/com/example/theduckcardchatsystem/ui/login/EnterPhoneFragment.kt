package com.example.theduckcardchatsystem.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.viewmodel.EnterPhoneViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EnterPhoneFragment extends Fragment  {
    @BindView(R.id.code_txt)
    EditText codeTxt;
    @BindView(R.id.register_input_phone_number)
    EditText registerInputPhoneNumber;
    EnterPhoneViewModel enterPhoneViewModel;


    public EnterPhoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_phone, container, false);
        ButterKnife.bind(this,view);

        codeTxt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        registerInputPhoneNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
        enterPhoneViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(EnterPhoneViewModel.class);
        return view;
    }
    @OnClick(R.id.register_btn_next)
    public void btnRegisterOnClick(){
        if (registerInputPhoneNumber.getText().toString().isEmpty()
                || codeTxt.getText().toString().isEmpty() ){
            Toast.makeText(getActivity(), "Enter Correct Phone Number", Toast.LENGTH_SHORT).show();
        }else if (!codeTxt.getText().toString().contains("+")) {
            Toast.makeText(getActivity(), "country code should contain +", Toast.LENGTH_SHORT).show();
        }else {
            LoginActivity activity = (LoginActivity) getActivity();

           String phoneNumber = codeTxt.getText().toString() +
           registerInputPhoneNumber.getText().toString();
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("phoneNumber", Context.MODE_PRIVATE).edit();
            editor.putString("phones",phoneNumber);
            editor.apply();
           enterPhoneViewModel.EnterPhone(phoneNumber,activity);

        }
    }

}
