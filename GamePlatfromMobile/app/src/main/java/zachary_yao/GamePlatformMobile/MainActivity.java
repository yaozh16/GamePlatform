package zachary_yao.GamePlatformMobile;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import java.net.CookieHandler;
import java.net.CookieManager;

import zachary_yao.ClientEngine.Configs.ClientConfig;

public class MainActivity extends AppCompatActivity {
    private final ClientConfig clientConfig=new ClientConfig();
    private final String TAG="mainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        Log.d(TAG,"build");
        SetDefaultContent();
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
    }
    private void SetDefaultContent(){
        Log.d(TAG,"DefaultContentSet");
        FragmentManager fm=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction trans=fm.beginTransaction();
        trans.replace(R.id.main_content,LoginFragment.newInstance(clientConfig));
        trans.commit();
        Log.d(TAG,"DefaultContentSet Done");
    }

}
