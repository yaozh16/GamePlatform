package zachary_yao.GamePlatformMobile.RoomComponents;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import Direction.DirectionWriter;
import GameState.GridMapControl.GridMapReader;
import zachary_yao.ClientEngine.ClientEngine;
import zachary_yao.ClientEngine.ClientEngineHolder;
import zachary_yao.ClientEngine.Configs.ClientConfig;
import zachary_yao.ClientEngine.Configs.GameControlConfig;
import zachary_yao.GamePlatformMobile.LobbyFragment;
import zachary_yao.GamePlatformMobile.R;

/**
 * Created by yaozh16 on 18-8-12.
 */

public class GridDisplayFragment extends Fragment implements ClientEngineHolder {
    public static GridDisplayFragment newInstance(GridMapReader gridMapReader,
                                                  ClientConfig clientConfig,
                                                  GameControlConfig gameControlConfig,
                                                  OptionFragment optionFragment){
        GridDisplayFragment gridDisplayFragment= new GridDisplayFragment();
        gridDisplayFragment.clientConfig=clientConfig;
        gridDisplayFragment.gridMapReader=gridMapReader;
        gridDisplayFragment.gameControlConfig=gameControlConfig;
        gridDisplayFragment.optionFragment=optionFragment;
        return gridDisplayFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        System.out.println("GridDisplayFragment onCreateView");
        gridDisplayView =(GridDisplayView) inflater.inflate(R.layout.room_griddisplayview_layout, container, false);
        return gridDisplayView;
    }



    private GridMapReader gridMapReader;
    private ClientConfig clientConfig;
    private GameControlConfig gameControlConfig;
    private GridDisplayView gridDisplayView;
    private OptionFragment optionFragment;

    public void setGridMapReader(GridMapReader gridMapReader) {
        this.gridMapReader = gridMapReader;
        System.out.println(gridDisplayView);
        gridDisplayView.setViewGridMapReader(this.gridMapReader,clientConfig);
    }

    @Override
    public void transferDirectionWriter(DirectionWriter directionWriter) {
        optionFragment.transferDirectionWriter(directionWriter);
    }

    private ClientEngine clientEngine;
    private Timer timer;
    public void installClientEngine (ClientEngine clientEngine){
        System.out.println(this+" install clientEngine:"+clientEngine);
        this.clientEngine = clientEngine;
        this.clientEngine.start();
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gridDisplayView.repaint();
                GridDisplayFragment.this.gridDisplayView.invalidate();
            }
        },100,100);

    }
    //MEnd
    //roomPanel.finish
    public synchronized void removeEngine(){
        if(clientEngine!=null) {
            clientEngine.finish();
            clientEngine=null;
            System.out.println("Client Remove Engine Done");
        };
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
    public void onLost(){
        System.out.println("On Lost连接失败");
        removeEngine();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction().replace(R.id.main_content, LobbyFragment.newInstance(clientConfig));
                Toast.makeText(getContext(),"连接断开",Toast.LENGTH_LONG);
            }
        });

    }
    public void notifyControlConfigChange(){
        if(clientEngine!=null){
            clientEngine.notifyControlConfigChange();
        }
    }
}
