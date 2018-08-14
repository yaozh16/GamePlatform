package zachary_yao.GamePlatformMobile.RoomComponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Direction.DirectionWriter;
import zachary_yao.GamePlatformMobile.R;

/**
 * Created by yaozh16 on 18-8-13.
 */

//与本地配置有关
public class ControlPanelFragment extends Fragment {
    public static ControlPanelFragment newInstance(){
        return new ControlPanelFragment();
    }


    public void setDirectionWriter(DirectionWriter directionWriter){
        optionDirectionView.setDirectionWriter(directionWriter);
    }

    private OptionDirectionView optionDirectionView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(getClass().getName()+"操作ControlPanel","onCreateView");
            optionDirectionView =(OptionDirectionView)inflater.inflate(R.layout.room_option_controlpanel, container, false);
            return optionDirectionView;
        }finally {

        }
    }
    private void initOper(){

    }
    public void finish(){

    };
}
