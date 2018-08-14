package zachary_yao.GamePlatformMobile;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import zachary_yao.ClientEngine.Configs.ClientConfig;

/**
 * Created by yaozh16 on 18-8-9.
 */

public class LoginFragment extends Fragment {
    private final String TAG="LoginFragment";
    private ClientConfig clientConfig;
    private TextView mMsgBoard;
    private EditText mAccount;
    private EditText mPassword;
    private EditText mServerAddress;
    private EditText mServerPort;
    private Button mRegister;
    private Button mLogin;
    private Button mExit;
    private LoginFragment setClientConfig(ClientConfig clientConfig){
        this.clientConfig=clientConfig;
        return this;
    }
    public static LoginFragment newInstance(ClientConfig clientConfig){
        return new LoginFragment().setClientConfig(clientConfig);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try {
            initPermission();
            Log.d(TAG, "onCreatView");
            View view = inflater.inflate(R.layout.login_layout, container, false);
            mMsgBoard=view.findViewById(R.id.login_msgBoard);
            mAccount=view.findViewById(R.id.login_account_input);
            mPassword=view.findViewById(R.id.login_password_input);
            mServerAddress=view.findViewById(R.id.login_address_input);
            mServerPort=view.findViewById(R.id.login_port_input);
            mRegister=view.findViewById(R.id.login_btn_register);
            mLogin=view.findViewById(R.id.login_btn_login);
            mExit=view.findViewById(R.id.login_btn_exit);
            initOper();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mAccount.getWindowToken(), 0) ;
            return view;
        }finally {
            Log.d(TAG,"finish create view");
        }
    }
    private void initPermission(){
        String[] permissions = {
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
        };
        ArrayList<String> toApplyList = new ArrayList<>();
        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this.getActivity(), perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this.getActivity(), toApplyList.toArray(tmpList), 123);
        }
    }
    private void initOper(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMsgBoard.setTextColor(getResources().getColor(R.color.loginMsgBoard));
                                mMsgBoard.setText("试图建立连接并注册中...");
                            }
                        });
                        Log.d(TAG,"register "+mServerAddress.getText().toString()+":"+mServerPort.getText().toString());
                        final String local_reply=clientConfig.signup(
                                mServerAddress.getText().toString(),
                                Integer.parseInt(mServerPort.getText().toString()),
                                mAccount.getText().toString(),
                                mPassword.getText().toString());
                        LoginFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMsgBoard.setText(local_reply);
                            }
                        });
                    }
                }).start();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mMsgBoard.setTextColor(getResources().getColor(R.color.loginMsgBoard));
                                mMsgBoard.setText("试图建立连接并登录中...");
                            }
                        });
                        Log.d(TAG,"register "+mServerAddress.getText().toString()+":"+mServerPort.getText().toString());
                        final String local_reply=clientConfig.login(
                                mServerAddress.getText().toString(),
                                Integer.parseInt(mServerPort.getText().toString()),
                                mAccount.getText().toString(),
                                mPassword.getText().toString());
                        if(clientConfig.getValidateCode()!=null){
                            LoginFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMsgBoard.setText(local_reply);
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.main_content,new LobbyFragment().setClientConfig(clientConfig))
                                            .commit();
                                }
                            });
                        }else
                            LoginFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMsgBoard.setText(local_reply);
                                }
                            });

                    }
                }).start();
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }
}
