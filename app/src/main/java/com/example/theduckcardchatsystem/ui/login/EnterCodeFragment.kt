package com.example.theduckcardchatsystem.ui.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.viewmodel.EnterCodeViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EnterCodeFragment extends Fragment {
    @BindView(R.id.register_input)
    EditText registerInputTxt;
    EnterCodeViewModel enterCodeViewModel;
    private String phoneNumber,id;
    private PhoneAuthCredential credential;


    public EnterCodeFragment() {
        // Required empty public constructor
    }

    public EnterCodeFragment(String phoneNumber, String id) {
        this.phoneNumber = phoneNumber;
        this.id = id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_code, container, false);
        ButterKnife.bind(this,view);
        FirebaseApp.initializeApp(getActivity());
        enterCodeViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(EnterCodeViewModel.class);

        registerInputTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String num = registerInputTxt.getText().toString();
                if (num.length() == 6) {
                    String code = registerInputTxt.getText().toString();
                    credential = PhoneAuthProvider.getCredential(id, code);
                    LoginActivity activity = (LoginActivity) getActivity();
                    enterCodeViewModel.EnterCode(credential,phoneNumber,activity);
                }
            }
        });
        return view;
    }
}
