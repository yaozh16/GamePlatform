package zachary_yao.GamePlatformMobile.RoomComponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import CommunicateControl.MsgThreadAsynHolder;
import Direction.DirectionWriter;
import Message.RoomMessage.MPause;
import Message.RoomMessage.MReady;
import Message.RoomMessage.MRoomStateBroadcast;
import zachary_yao.ClientEngine.Configs.ClientConfigHolder;
import zachary_yao.ClientEngine.Configs.GameControlConfig;
import zachary_yao.ClientEngine.GameControler.ControlConfigChangeNotifier;
import zachary_yao.GamePlatformMobile.R;


/**
 * Created by yaozh16 on 18-8-11.
 */

public class OptionFragment extends Fragment {

    private GameControlConfig gameControlConfig;
    private MsgThreadAsynHolder msgThreadAsynHolder;
    private ClientConfigHolder clientConfigHolder;
    private ControlConfigChangeNotifier controlConfigChangeNotifier;
    private GamePanelFragment gamePanelFragment;
    private ControlPanelFragment controlPanelFragment;
    private MyScorePanelFragment myScorePanelFragment;
    public static OptionFragment newInstance(
            GameControlConfig gameControlConfig,
            MsgThreadAsynHolder msgThreadAsynHolder,
            ClientConfigHolder clientConfigHolder,
            ControlConfigChangeNotifier controlConfigChangeNotifier){
        OptionFragment optionFragment=new OptionFragment();
        optionFragment.gameControlConfig=gameControlConfig;
        optionFragment.msgThreadAsynHolder=msgThreadAsynHolder;
        optionFragment.clientConfigHolder=clientConfigHolder;
        optionFragment.controlConfigChangeNotifier=controlConfigChangeNotifier;
        return optionFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {

            Log.d(getClass().getName()+"操作","onCreateView");
            View view = inflater.inflate(R.layout.room_option_layout, container, false);
            gamePanelFragment = GamePanelFragment.newInstance(msgThreadAsynHolder,clientConfigHolder);
            controlPanelFragment =ControlPanelFragment.newInstance();
            myScorePanelFragment=MyScorePanelFragment.newInstance(clientConfigHolder.getClientConfig().getAccount());
            getFragmentManager().beginTransaction()
                    .replace(R.id.room_option_gamepanel, gamePanelFragment)
                    .replace(R.id.room_option_controlpanel, controlPanelFragment)
                    .replace(R.id.room_option_myscorepanel, myScorePanelFragment)
                    .commit();
            return view;
        }finally {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    msgThreadAsynHolder.toSendObj(new MReady(clientConfigHolder.getClientConfig().getAccount(), clientConfigHolder.getClientConfig().getValidateCode(), false));
                }
            }).start();
        }
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(getClass().getName(),"onDestroyView");
    }



    public void markInit(){
        gamePanelFragment.markInit();
    }
    public void notifyReadyDone(boolean ready){
        gamePanelFragment.markReadyDone(ready);
    }
    public void notifyPauseDone(boolean pause){
        if(!pause){
            gamePanelFragment.markGame();//return
        }else {
            gamePanelFragment.markPauseConfirmed();
        }
    }
    public void notifyStart(){
        gamePanelFragment.markGame();
    }
    public void notifyPause(MPause mPause){
        gamePanelFragment.markPauseRequested(mPause);
    }
    public void notifyEnd(){
        gamePanelFragment.markEnd();
    }
    public void finish(){
        controlPanelFragment.finish();
    }


    public void transferDirectionWriter(DirectionWriter directionWriter) {
        controlPanelFragment.setDirectionWriter(directionWriter);
    }
    public void notifyRoomStateUpdated(MRoomStateBroadcast mRoomStateBroadcast){
        myScorePanelFragment.updateScorePanel(mRoomStateBroadcast);
    }
}
