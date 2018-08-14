package zachary_yao.GamePlatformMobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import BasicState.PlayerState;
import BasicState.RoomState;
import CommunicateControl.MsgThreadAsyn;
import CommunicateControl.MsgThreadAsynHolder;
import CommunicateControl.ObjThreadAsyn;
import Message.Common.Message;
import Message.MessageProcessor.MessageProcessor;
import Message.MessageProcessor.MessageProcessorCollection;
import Message.RoomMessage.MChat;
import Message.RoomMessage.MConfigChangeBroadcast;
import Message.RoomMessage.MConfigChangeReply;
import Message.RoomMessage.MConnect;
import Message.RoomMessage.MEnd;
import Message.RoomMessage.MPause;
import Message.RoomMessage.MPauseBroadcast;
import Message.RoomMessage.MRoomBroadcast;
import Message.RoomMessage.MRoomStateBroadcast;
import Message.RoomMessage.MStart;
import Message.VisitorMessage.MTouch;
import zachary_yao.ClientEngine.CommonClientEngine;
import zachary_yao.ClientEngine.Configs.ClientConfig;
import zachary_yao.ClientEngine.Configs.ClientConfigHolder;
import zachary_yao.ClientEngine.Configs.GameControlConfig;
import zachary_yao.ClientEngine.GameControler.ControlConfigChangeNotifier;
import zachary_yao.GamePlatformMobile.RoomComponents.GridDisplayFragment;
import zachary_yao.GamePlatformMobile.RoomComponents.OptionFragment;
import zachary_yao.GamePlatformMobile.RoomComponents.RoomStateFragment;
import zachary_yao.GamePlatformMobile.RoomComponents.ScoreFragment;
import zachary_yao.Universal.MyFragmentPagerAdapter;

/**
 * Created by yaozh16 on 18-8-11.
 */

public class RoomFragment extends Fragment implements MsgThreadAsynHolder,ClientConfigHolder,ControlConfigChangeNotifier {
    private ClientConfig clientConfig;
    private RoomState roomState;
    private MsgThreadAsyn msgThreadAsyn;
    public static RoomFragment newInstance(ClientConfig clientConfig,RoomState roomState,MsgThreadAsyn msgThreadAsyn){
        return new RoomFragment().setClientConfig(clientConfig).setRoomState(roomState).setMsgThreadAsyn(msgThreadAsyn);
    }
    private RoomFragment setClientConfig(ClientConfig clientConfig){
        this.clientConfig=clientConfig;
        return this;
    }
    private RoomFragment setRoomState(RoomState roomState){
        this.roomState=roomState;
        return this;
    }
    private RoomFragment setMsgThreadAsyn(MsgThreadAsyn msgThreadAsyn){
        this.msgThreadAsyn=msgThreadAsyn;
        msgThreadAsyn.setObjThreadAsynHolder(this);
        return this;
    }



    private TextView roomMsgBoard;
    private TabLayout mTb;
    private ViewPager mVp;
    private ArrayList<String> mTitleList;
    private ArrayList<Fragment> mFragmentList;
    private OptionFragment optionFragment;
    private ScoreFragment scoreFragment;
    private RoomStateFragment roomStateFragment;
    private GridDisplayFragment gridDisplayFragment;
    private GameControlConfig gameControlConfig=new GameControlConfig();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try {
            Log.d(getClass().getName(), "onCreateView");
            View view = inflater.inflate(R.layout.room_layout, container, false);
            roomMsgBoard=view.findViewById(R.id.room_msgboard);
            optionFragment = OptionFragment.newInstance(gameControlConfig, this, this, this);
            scoreFragment = ScoreFragment.newInstance();
            roomStateFragment = RoomStateFragment.newInstance(this,this,roomState);
            gridDisplayFragment = GridDisplayFragment.newInstance(null, clientConfig, gameControlConfig, optionFragment);
            initBottomNav(view);
            initGridDisplayFragment();
            return view;
        }finally {

        }
    }
    private void initGridDisplayFragment(){
        getFragmentManager().beginTransaction().replace(R.id.room_displayFrame,gridDisplayFragment).commit();
    }
    private void initBottomNav(View view){
        //初始化控件
        initTabPageView(view);
        //添加标题
        initTitile();
        //添加fragment
        initFragment();
        //设置适配器
        mVp.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),mFragmentList,mTitleList));
        //将tablayout与fragment关联
        mTb.setupWithViewPager(mVp);
    }
    private void initTabPageView(View view){
        mTb =view.findViewById(R.id.room_tabLayout);
        mVp =view.findViewById(R.id.room_viewPager);
    }

    private void initTitile() {
        mTitleList = new ArrayList<>();
        mTitleList.add("操作");
        mTitleList.add("得分");
        mTitleList.add("配置");
        //设置tablayout模式
        mTb.setTabMode(TabLayout.MODE_FIXED);
        //tablayout获取集合中的名称
        mTb.addTab(mTb.newTab().setText(mTitleList.get(0)));
        mTb.addTab(mTb.newTab().setText(mTitleList.get(1)));
        mTb.addTab(mTb.newTab().setText(mTitleList.get(2)));
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();

        mFragmentList.add(optionFragment);
        mFragmentList.add(scoreFragment);
        mFragmentList.add(roomStateFragment);
    }





    @Deprecated@Override
    public void setUpObjThread(Socket socket) {

    }

    @Override
    public void onRecvObj(Message message) {
        messageProcessorCollection.processMessage(message);
    }

    @Override
    public void toSendObj(Message message) {
        if(msgThreadAsyn!=null)
            msgThreadAsyn.sendMsg(message);
    }

    private boolean afterFinish=false;
    public void finish(){
        if(afterFinish)
            System.out.println("Finish Twice");
        afterFinish=true;
        if(msgThreadAsyn!=null) {
            msgThreadAsyn.finish();
            try {
                if (!msgThreadAsyn.getSocket().isClosed()) {
                    System.err.println("try release");
                    msgThreadAsyn.getSocket().close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        gridDisplayFragment.removeEngine();
        optionFragment.finish();
        //musicPanel.finish();
    }

    @Override
    public void exit(ObjThreadAsyn src) {
        if(!afterFinish) {
            Activity activity=getActivity();
            if(activity!=null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction().replace(R.id.main_content,LoginFragment.newInstance(clientConfig)).commit();
                    }
                });
        }
    }

    private MessageProcessorCollection messageProcessorCollection=new MessageProcessorCollection()
            .install(new MessageProcessor(MRoomStateBroadcast.class) {
                @Override
                public void process(Message message) {
                    final MRoomStateBroadcast mRoomStateBroadcast =(MRoomStateBroadcast)message;
                    roomState.copy(mRoomStateBroadcast.roomState);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scoreFragment.updateScorePanel(mRoomStateBroadcast);
                            optionFragment.notifyReadyDone(mRoomStateBroadcast.playerStates.get(clientConfig.getAccount()).getOnlineState()== PlayerState.OnlineState.READY);
                        }
                    });
                    if(roomStateFragment!=null) {
                        roomStateFragment.notifyRoomStateUpdated();
                        optionFragment.notifyRoomStateUpdated(mRoomStateBroadcast);
                    }

                }
            })
            .install(new MessageProcessor(MConnect.class) {
                @Override
                public void process(Message message) {
                    System.out.println("\033[1;32mGame Ready to Start\033[0m");
                    MConnect mConnect=(MConnect)message;
                    System.out.println("MConnect:\033[1;32m"+mConnect.inetAddress+","+mConnect.port+"\033[0m");
                    gridDisplayFragment.installClientEngine(new CommonClientEngine(mConnect,gridDisplayFragment,RoomFragment.this,gameControlConfig));
                }
            })
            .install(new MessageProcessor(MRoomBroadcast.class) {
                @Override
                public void process(final Message message) {
                    Activity activity=getActivity();
                    if(activity!=null)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                roomMsgBoard.setText(((MRoomBroadcast)message).info);
                            }
                        });
                }
            })
            .install(new MessageProcessor(MChat.class) {
                @Override
                public void process(final Message message) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomMsgBoard.setText(((MChat)message).info);
                        }
                    });
                }
            })
            .install(new MessageProcessor(MEnd.class) {
                @Override
                public void process(final Message message) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final MEnd mEnd=(MEnd)message;
                            System.out.println("MEnd remove Client Engine");
                            gridDisplayFragment.removeEngine();

                            optionFragment.notifyEnd();
                            System.out.println("MEnd Process Done");
                            //new GameResultReportMessageBox(mEnd.gameResult).run();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),mEnd.gameResult.formatForLabel(),Toast.LENGTH_LONG).show();
                                    roomMsgBoard.setText(mEnd.gameResult.getWinner().contains(clientConfig.getAccount())?"you win":"you lose");
                                }
                            });
                        }
                    }).start();
                }
            })
            .install(new MessageProcessor(MPauseBroadcast.class) {
                @Override
                public void process(Message message) {
                    final MPauseBroadcast mPauseBroadcast=(MPauseBroadcast)message;
                    optionFragment.notifyPauseDone(mPauseBroadcast.pauseConfirmed);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomMsgBoard.setText(mPauseBroadcast.info);
                        }
                    });
                }
            })
            .install(new MessageProcessor(MPause.class) {
                @Override
                public void process(Message message) {
                    MPause mPause=(MPause)message;
                    optionFragment.notifyPause(mPause);
                }
            })
            .install(new MessageProcessor(MStart.class) {
                @Override
                public void process(Message message) {
                    optionFragment.notifyStart();
                    roomStateFragment.notifyRoomStateUpdated();

                    final MStart mStart=(MStart) message;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),mStart.info,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            })
            .install(new MessageProcessor(MTouch.class) {
                @Override
                public void process(Message message) {
                    System.out.println("Touched");
                }
            })
            .install(new MessageProcessor(MConfigChangeReply.class) {
                @Override
                public void process(Message message) {
                    final MConfigChangeReply mConfigChangeReply=(MConfigChangeReply)message;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomMsgBoard.setText(mConfigChangeReply.info);
                        }
                    });
                    roomState.getRoomConfig().getGameConfig().copy(mConfigChangeReply.getGameConfig());
                    roomStateFragment.notifyRoomStateUpdated();
                }
            })
            .install(new MessageProcessor(MConfigChangeBroadcast.class) {
                @Override
                public void process(Message message) {
                    final MConfigChangeBroadcast mConfigChangeBroadcast=(MConfigChangeBroadcast)message;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomMsgBoard.setText(mConfigChangeBroadcast.info);
                        }
                    });
                    roomState.getRoomConfig().getGameConfig().copy(mConfigChangeBroadcast.getGameConfig());
                    roomStateFragment.notifyRoomStateUpdated();
                }
            })
            ;

    @Override
    public ClientConfig getClientConfig() {
        return clientConfig;
    }


    @Override
    public void notifyControlConfigChange() {
        gridDisplayFragment.notifyControlConfigChange();
    }
}
