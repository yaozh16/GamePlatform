package zachary_yao.GamePlatformMobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
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
import android.widget.Toast;

import java.net.Socket;
import java.util.HashSet;

import BasicState.RoomState;
import CommunicateControl.MsgThreadAsyn;
import CommunicateControl.MsgThreadAsynHolder;
import CommunicateControl.ObjThreadAsyn;
import GameState.GameConfig.GameConfig;
import GameState.GameConfig.GameType;
import GameState.GameConfig.RoomConfig;
import Message.Common.Message;
import Message.MessageProcessor.MessageProcessor;
import Message.MessageProcessor.MessageProcessorCollection;
import Message.RoomMessage.MRoomStateBroadcast;
import Message.UpdateMessage.MUpdatePlayersReply;
import Message.UpdateMessage.MUpdateRooms;
import Message.UpdateMessage.MUpdateRoomsReply;
import Message.VisitorMessage.MBroadcast;
import Message.VisitorMessage.MBuildRoom;
import Message.VisitorMessage.MBuildRoomReply;
import Message.VisitorMessage.MJoinRoom;
import Message.VisitorMessage.MJoinRoomReply;
import zachary_yao.ClientEngine.Configs.ClientConfig;
import zachary_yao.Universal.RangeNumberPicker;

/**
 * Created by yaozh16 on 18-8-10.
 */

public class LobbyFragment extends Fragment implements MsgThreadAsynHolder {
    private final String TAG="LobbyFragment";
    private ClientConfig clientConfig;
    private MsgThreadAsyn msgThreadAsyn;
    private RoomState roomState=new RoomState(new HashSet<String >(),new HashSet<String>(),new HashSet<String>(),
            new RoomConfig("",
                    new GameConfig(2,20,20,20,20,3,4, GameType.GluttonousSnake)));
    public LobbyFragment setClientConfig(ClientConfig clientConfig){
        this.clientConfig=clientConfig;
        return this;
    }
    public static LobbyFragment newInstance(ClientConfig clientConfig){
        return new LobbyFragment().setClientConfig(clientConfig);
    }

    private Button mBuildRoom;
    private Button mUpdate;
    private LinearLayout mRoomList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try {
            Log.d(TAG, "onCreatView");
            View view = inflater.inflate(R.layout.lobby_layout, container, false);
            mBuildRoom=view.findViewById(R.id.lobby_buildRoom);
            mUpdate=view.findViewById(R.id.lobby_update);
            mRoomList=view.findViewById(R.id.lobby_roomList);
            initOper();
            return view;
        }finally {
            Log.d(TAG,"finish create view");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setUpObjThread(clientConfig.setUpSocket());
                    requestUpdate();
                }
            }).start();
        }
    }
    private void initOper(){
        mBuildRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("房间设置");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.room_config_layout, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);

                final EditText mRoomName = (EditText)view.findViewById(R.id.room_config_roomName);
                final SeekBar mGameSpeed = (SeekBar)view.findViewById(R.id.room_config_speed);
                final RadioGroup mGameType = (RadioGroup) view.findViewById(R.id.room_config_gameType);
                final RadioButton mGameTypeGluttonousSnake = (RadioButton) view.findViewById(R.id.room_config_gameType_GluttonousSnake);
                final RangeNumberPicker mGridWidth = (RangeNumberPicker)view.findViewById(R.id.room_config_gridWidth);
                final RangeNumberPicker mGridHeight = (RangeNumberPicker)view.findViewById(R.id.room_config_gridHeight);
                final RangeNumberPicker mMaxLife = (RangeNumberPicker)view.findViewById(R.id.room_config_maxLife);
                final RangeNumberPicker mMaxPlayer = (RangeNumberPicker)view.findViewById(R.id.room_config_maxPlayer);
                final RangeNumberPicker mBonusCount = (RangeNumberPicker)view.findViewById(R.id.room_config_bonusCount);
                final RangeNumberPicker mHolePair = (RangeNumberPicker)view.findViewById(R.id.room_config_holePair);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                msgThreadAsyn.sendMsg(
                                        new MBuildRoom(
                                                clientConfig.getAccount(),
                                                clientConfig.getValidateCode(),
                                                new RoomConfig(mRoomName.getText().toString(),
                                                        new GameConfig(
                                                                mMaxPlayer.getValue(),
                                                                mGridWidth.getValue(),
                                                                mGridHeight.getValue(),
                                                                mMaxLife.getValue(),
                                                                mBonusCount.getValue(),
                                                                mGameSpeed.getProgress()+1,
                                                                mHolePair.getValue(),
                                                                (mGameTypeGluttonousSnake.isChecked()?GameType.GluttonousSnake:GameType.TankBattle)))));
                            }
                        }).start();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUpdate();
            }
        });
    }

    public void requestUpdate(){
        System.out.println("request update");
        //toSendObj(new MUpdatePlayers(clientConfig.getAccount(),clientConfig.getValidateCode()));
        toSendObj(new MUpdateRooms(clientConfig.getAccount(),clientConfig.getValidateCode()));
    }
    @Override
    public void setUpObjThread(Socket socket) {
        if(msgThreadAsyn!=null)
            msgThreadAsyn.finish();
        msgThreadAsyn=new MsgThreadAsyn(this,socket);
        msgThreadAsyn.start();
    }

    @Override
    public void onRecvObj(final Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                messageProcessorCollection.processMessage(message);
            }
        }).start();
    }

    @Override
    public void toSendObj(Message message) {
        msgThreadAsyn.sendMsg(message);
    }

    @Override
    public void finish(){}

    @Override
    public void exit(ObjThreadAsyn src){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content,LoginFragment.newInstance(clientConfig))
                        .commit();
                Toast.makeText(getActivity(),"失去连接",Toast.LENGTH_LONG).show();
            }
        });
    }

    MessageProcessorCollection messageProcessorCollection=new MessageProcessorCollection()
            .install(new MessageProcessor(MUpdatePlayersReply.class) {
                @Override
                public void process(Message message) {
                    //ignore
                }
            })
            .install(new MessageProcessor(MUpdateRoomsReply.class) {
                @Override
                public void process(final Message message) {

                    MUpdateRoomsReply mUpdateRoomsReply = (MUpdateRoomsReply) message;
                    if (mUpdateRoomsReply.OK) {
                        updateRoomList(mUpdateRoomsReply.roomStates);
                    }
                    System.out.println("process MUpdateRoomsReply done");
                }
            })
            .install(new MessageProcessor(MBroadcast.class) {
                @Override
                public void process(Message message) {
                    MBroadcast mBroadcast=(MBroadcast)message;
                    System.out.println(mBroadcast.info);
                }
            })
            .install(new MessageProcessor(MBuildRoomReply.class) {
                @Override
                public void process(Message message) {
                    final MBuildRoomReply mBuildRoomReply=(MBuildRoomReply)message;
                    if(mBuildRoomReply.OK) {
                        System.out.println(mBuildRoomReply.roomState);
                        roomState.copy(mBuildRoomReply.roomState);
                        enterRoom();
                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),mBuildRoomReply.info,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            })
            .install(new MessageProcessor(MJoinRoomReply.class) {
                @Override
                public void process(Message message) {
                    final MJoinRoomReply mJoinRoomReply=(MJoinRoomReply)message;
                    if(mJoinRoomReply.OK) {
                        System.out.println(mJoinRoomReply.roomState);
                        roomState.copy(mJoinRoomReply.roomState);
                        enterRoom();
                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),mJoinRoomReply.info,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            })
            .install(new MessageProcessor(MRoomStateBroadcast.class) {
                @Override
                public void process(Message message) {
                    //ignore
                }
            });
    private synchronized void updateRoomList(final RoomState[] roomStates){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRoomList.removeAllViews();
                if(roomStates.length==0){
                    Toast.makeText(getContext(),"现在没有已经建立的房间，快建立一个吧",Toast.LENGTH_LONG).show();
                }else {
                    for (final RoomState roomState : roomStates) {
                        Button button = new Button(getActivity());
                        String roomLabel = roomState.getRoomConfig().roomName + "(" + roomState.getRoomStateType() + ")\n";
                        String roomPlayers = "Player:" + String.join("\nPlayer:", roomState.getPlayers()).replaceFirst("\nPlayer:$", "\n");
                        SpannableString buttonText = new SpannableString(roomLabel + roomPlayers);
                        buttonText.setSpan(new AbsoluteSizeSpan(15, true), 0, roomLabel.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        buttonText.setSpan(new AbsoluteSizeSpan(12, true), roomLabel.length(), roomLabel.length() + roomPlayers.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        button.setText(buttonText);
                        button.setWidth(mRoomList.getWidth());
                        button.setBackgroundColor(getResources().getColor(R.color.lobbyRoomButton));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toSendObj(new MJoinRoom(clientConfig.getAccount(), clientConfig.getValidateCode(), roomState.getRoomConfig().roomName));
                            }
                        });
                        mRoomList.addView(button);
                    }
                }
            }
        });
    }
    private synchronized void enterRoom(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction().replace(R.id.main_content,
                        RoomFragment.newInstance(clientConfig,roomState,msgThreadAsyn))
                        .commit();
            }
        });
    }
}
