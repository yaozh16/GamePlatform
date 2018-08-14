package zachary_yao.GamePlatformMobile.RoomComponents;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import CommunicateControl.MsgThreadAsynHolder;
import Message.RoomMessage.MLeave;
import Message.RoomMessage.MPause;
import Message.RoomMessage.MPauseAnswer;
import Message.RoomMessage.MReady;
import zachary_yao.ClientEngine.Configs.ClientConfigHolder;
import zachary_yao.GamePlatformMobile.LobbyFragment;
import zachary_yao.GamePlatformMobile.R;
import zachary_yao.Universal.ColoredButton;

/**
 * Created by yaozh16 on 18-8-13.
 */

//与网络连接有关
public class GamePanelFragment extends Fragment {
    private volatile boolean ready=false;
    private MsgThreadAsynHolder msgThreadAsynHolder;
    private ClientConfigHolder clientConfigHolder;

    public static GamePanelFragment newInstance(MsgThreadAsynHolder msgThreadAsynHolder, ClientConfigHolder clientConfigHolder){
        GamePanelFragment gamePanelFragment =new GamePanelFragment();
        gamePanelFragment.msgThreadAsynHolder=msgThreadAsynHolder;
        gamePanelFragment.clientConfigHolder=clientConfigHolder;
        return gamePanelFragment;
    }
    TextView msgDisplayBoard;
    Button leaveButton;
    Button readyButton;
    Button pauseButton;
    Button pauseAnswerButton_Yes;
    Button pauseAnswerButton_No;
    public void initOper(){
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
                msgThreadAsynHolder.toSendObj(new MPause(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode()));
            }
        });
        pauseAnswerButton_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
                msgThreadAsynHolder.toSendObj(new MPauseAnswer(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),true));
                markPauseAnswered(true);;
            }
        });
        pauseAnswerButton_No .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
                msgThreadAsynHolder.toSendObj(new MPauseAnswer(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),false));
                markPauseAnswered(false);;
            }
        });
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
                System.out.println("state="+ready);
                msgThreadAsynHolder.toSendObj(new MReady(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),!ready));
                System.out.print("ready done/undone");
            }
        });
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
                msgThreadAsynHolder.toSendObj(new MLeave(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode()));
                getParentFragment()
                        .getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, LobbyFragment.newInstance(clientConfigHolder.getClientConfig()))
                        .commit();
            }
        });

    }
    private LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(getClass().getName()+"操作GamePanel","onCreateView");
            View view = inflater.inflate(R.layout.room_option_gamepanel, container, false);
            linearLayout=view.findViewById(R.id.room_option_gamepanel_linearlayout);
            msgDisplayBoard=new TextView(getContext());
            leaveButton= ColoredButton.newInstance(getContext(),"离开房间",30,30, Color.WHITE,10);
            readyButton=ColoredButton.newInstance(getContext(),"准备",30,30,Color.WHITE,10);
            pauseButton=ColoredButton.newInstance(getContext(),"请求暂停",30,30,Color.WHITE,10);
            pauseAnswerButton_Yes=ColoredButton.newInstance(getContext(),"同意暂停",30,30,Color.WHITE,10);
            pauseAnswerButton_No =ColoredButton.newInstance(getContext(),"拒绝暂停",30,30,Color.WHITE,10);
            initOper();
            markInit();
            return view;
        }finally {
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(getClass().getName(),"onDestroyView");
    }


    /*private GamePanelFragment(){
        markInit();
    }*/
    public void markInit(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayout.removeAllViews();
                readyButton.setBackgroundColor(Color.rgb(25,250,16));
                readyButton.setText("准备");
                ready=false;
                linearLayout.addView(readyButton);


                leaveButton.setBackgroundColor(Color.rgb(255,0,0));
                linearLayout.addView(leaveButton);
                linearLayout.invalidate();
            }
        });
    }
    public void markReadyDone(final boolean ready){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(ready){
                    readyButton.setBackgroundColor(Color.rgb(248, 183, 29));
                    readyButton.setText("取消准备");
                    GamePanelFragment.this.ready=true;
                }else {
                    readyButton.setBackgroundColor(Color.rgb(25, 250, 16));
                    readyButton.setText("准备");
                    GamePanelFragment.this.ready=false;
                }
            }
        });
    }
    public void markGame(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayout.removeAllViews();

                ready=true;
                pauseButton.setBackgroundColor(Color.rgb(152, 77, 255));
                linearLayout.addView(pauseButton);

                leaveButton.setBackgroundColor(Color.rgb(255,0,0));
                linearLayout.addView(leaveButton);
            }
        });
    }
    public void markPauseRequested(final MPause mPause){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayout.removeAllViews();
                msgDisplayBoard.setText(mPause.account+"请求暂停 是否同意？");
                linearLayout.addView(msgDisplayBoard);
                pauseAnswerButton_No.setEnabled(true);
                pauseAnswerButton_Yes.setEnabled(true);
                pauseAnswerButton_No.setBackgroundColor(Color.rgb(9, 14, 91));
                pauseAnswerButton_Yes.setBackgroundColor(Color.rgb(255, 17, 127));
                linearLayout.addView(pauseAnswerButton_No);
                linearLayout.addView(pauseAnswerButton_Yes);

                leaveButton.setBackgroundColor(Color.WHITE);
                leaveButton.setBackgroundColor(Color.rgb(255,0,0));
                linearLayout.addView(leaveButton);
            }
        });

    }
    public void markPauseAnswered(final boolean agree){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgDisplayBoard.setText("已经"+(agree?"同意暂停":"拒绝暂停"));
                pauseAnswerButton_No.setBackgroundColor(Color.argb(127,9, 14, 91));
                pauseAnswerButton_Yes.setBackgroundColor(Color.argb(127,255, 17, 127));
                pauseAnswerButton_No.setEnabled(false);
                pauseAnswerButton_No.setEnabled(false);
            }
        });
    }
    public void markPauseConfirmed(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                linearLayout.removeAllViews();


                readyButton.setBackgroundColor(Color.rgb(25, 250, 16));
                readyButton.setText("继续(准备)");
                ready=false;
                linearLayout.addView(readyButton);


                leaveButton.setBackgroundColor(Color.rgb(255,0,0));
                linearLayout.addView(leaveButton);
            }
        });
    }
    public void markEnd(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayout.removeAllViews();


                readyButton.setBackgroundColor(Color.rgb(25, 250, 16));
                readyButton.setText("重新开始");
                ready=false;
                linearLayout.addView(readyButton);


                leaveButton.setBackgroundColor(Color.rgb(255,0,0));
                linearLayout.addView(leaveButton);
            }
        });
    };
}
