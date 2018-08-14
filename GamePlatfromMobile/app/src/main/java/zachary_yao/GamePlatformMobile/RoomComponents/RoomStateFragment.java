package zachary_yao.GamePlatformMobile.RoomComponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import BasicState.RoomState;
import CommunicateControl.MsgThreadAsynHolder;
import GameState.GameConfig.GameConfig;
import GameState.GameConfig.GameType;
import Message.RoomMessage.MConfigChange;
import zachary_yao.ClientEngine.Configs.ClientConfigHolder;
import zachary_yao.GamePlatformMobile.R;
import zachary_yao.Universal.RangeNumberPicker;

/**
 * Created by yaozh16 on 18-8-13.
 */

public class RoomStateFragment extends Fragment {
    EditText mRoomName;
    SeekBar mGameSpeed;
    RadioButton mGameTypeGluttonousSnake;
    RadioButton mGameTypeTankBattle;
    RadioGroup mGameType;
    RangeNumberPicker mGridWidth,mGridHeight,mMaxLife,mMaxPlayer,mBonusCount,mHolePair;
    LinearLayout buttonfield_linear;
    Button confirmButton;

    private RoomState roomState;
    private RoomState lastRoomState;
    private MsgThreadAsynHolder msgThreadAsynHolder;
    private ClientConfigHolder clientConfigHolder;
    public static RoomStateFragment newInstance(MsgThreadAsynHolder msgThreadAsynHolder, ClientConfigHolder clientConfigHolder,RoomState roomState){
        RoomStateFragment roomStateFragment=new RoomStateFragment();
        roomStateFragment.msgThreadAsynHolder=msgThreadAsynHolder;
        roomStateFragment.clientConfigHolder=clientConfigHolder;
        roomStateFragment.roomState=roomState;
        roomStateFragment.lastRoomState=roomState.copy();
        return roomStateFragment;
    }
    boolean viewCreate=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            viewCreate=true;
            Log.d(getClass().getName()+"房间配置", "onCreateView");
            View view = inflater.inflate(R.layout.room_config_layout, container, false);
            mRoomName = (EditText)view.findViewById(R.id.room_config_roomName);
            mGameSpeed = (SeekBar)view.findViewById(R.id.room_config_speed);
            mGameType = (RadioGroup) view.findViewById(R.id.room_config_gameType);
            mGameTypeGluttonousSnake = (RadioButton) view.findViewById(R.id.room_config_gameType_GluttonousSnake);
            mGameTypeTankBattle = (RadioButton) view.findViewById(R.id.room_config_gameType_TankBattle);
            mGridWidth = (RangeNumberPicker)view.findViewById(R.id.room_config_gridWidth);
            mGridHeight = (RangeNumberPicker)view.findViewById(R.id.room_config_gridHeight);
            mMaxLife = (RangeNumberPicker)view.findViewById(R.id.room_config_maxLife);
            mMaxPlayer = (RangeNumberPicker)view.findViewById(R.id.room_config_maxPlayer);
            mBonusCount = (RangeNumberPicker)view.findViewById(R.id.room_config_bonusCount);
            mHolePair = (RangeNumberPicker)view.findViewById(R.id.room_config_holePair);
            buttonfield_linear=view.findViewById(R.id.room_config_buttonfield_linear);
            confirmButton=new Button(getContext());
            confirmButton.setText("修改");
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgThreadAsynHolder.toSendObj(new MConfigChange(clientConfigHolder.getClientConfig().getAccount(),clientConfigHolder.getClientConfig().getValidateCode(),
                            new GameConfig(
                                    mMaxPlayer.getValue(),
                                    mGridWidth.getValue(),
                                    mGridHeight.getValue(),
                                    mMaxLife.getValue(),
                                    mBonusCount.getValue(),
                                    mGameSpeed.getProgress()+1,
                                    mHolePair.getValue(),
                                    mGameTypeGluttonousSnake.isChecked()? GameType.GluttonousSnake:GameType.TankBattle
                            )
                    ));
                }
            });
            buttonfield_linear.addView(confirmButton);
            return view;
        }finally {
            mRoomName.setEnabled(false);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(getClass().getName(),"onDestroyView");
    }
    public void notifyRoomStateUpdated(){
        if(roomState.getRoomStateType().equals(RoomState.RoomStateType.Free)){
            System.out.println("\033[1;32mRoomStateInit\033[0m");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    onInit();
                }
            }).start();
        }else {
            System.out.println("\033[1;32mRoomStateStart\033[0m");
            onStartMessage();
        }
        if(!roomState.getRoomConfig().equals(lastRoomState.getRoomConfig())){
            doUpdate();
            lastRoomState.copy(roomState);
        }
    }
    private void onInit(){
        if(!viewCreate)
            return;
        mGameType.setEnabled(true);
        mMaxLife.setEnabled(true);
        mGridWidth.setEnabled(true);
        mGridHeight.setEnabled(true);
        mMaxLife.setEnabled(true);
        mBonusCount.setEnabled(true);
        mHolePair.setEnabled(true);
    }
    private void onStartMessage(){
        if(!viewCreate)
            return;
        mGameType.setEnabled(false);
        mMaxLife.setEnabled(false);
        mGridWidth.setEnabled(false);
        mGridHeight.setEnabled(false);
        mMaxLife.setEnabled(false);
        mBonusCount.setEnabled(false);
        mHolePair.setEnabled(false);
    }
    private void doUpdate(){
        if(!viewCreate)
            return;
        GameConfig config=roomState.getRoomConfig().getGameConfig();
        switch (config.getGameType()){
            case TankBattle:
                mGameTypeTankBattle.toggle();
                break;
            case GluttonousSnake:
                mGameTypeGluttonousSnake.toggle();
                break;
        }
        mMaxLife.setValue(config.getLifeCount());
        mGridWidth.setValue(config.getGridWidth());
        mGridHeight.setValue(config.getGridHeight());
        mBonusCount.setValue(config.getBonusCount());
        mHolePair.setValue(config.getHolePair());
        mHolePair.setValue(config.getHolePair());
        mGameSpeed.setProgress(config.getSpeed()-1);
    }
}
