package zachary_yao.GamePlatformMobile.RoomComponents;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import GameState.GridObjects.Manager.ColorManager;
import GameState.Proxy.ColorProxy;
import Message.RoomMessage.MRoomStateBroadcast;
import zachary_yao.GamePlatformMobile.R;

/**
 * Created by yaozh16 on 18-8-11.
 */

public class ScoreFragment extends Fragment {
    public static ScoreFragment newInstance(){
        return new ScoreFragment();
    }
    MRoomStateBroadcast mRoomStateBroadcast=null;

    LinearLayout playerList;
    public void updateScorePanel(final MRoomStateBroadcast mRoomStateBroadcast){
        this.mRoomStateBroadcast=mRoomStateBroadcast;
        updateDisplay();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(getClass().getName()+"得分", "onCreateView");
            View view = inflater.inflate(R.layout.room_score_layout, container, false);
            playerList=view.findViewById(R.id.scrore_player_list);
            updateDisplay();
            return view;
        }finally {

        }
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(getClass().getName(),"onDestroyView");
    }

    public void updateDisplay(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerList.removeAllViews();
                if(mRoomStateBroadcast!=null)
                    for(String account:mRoomStateBroadcast.playerStates.keySet()){
                        EditText editText=new EditText(getContext());
                        String format=account;
                        format+="(score:";
                        format+=(mRoomStateBroadcast.playerGameStates.get(account).getScore());
                        format+="\tlife:";
                        format+=(mRoomStateBroadcast.playerGameStates.get(account).getLife());
                        format+=")";
                        editText.setText(format);
                        editText.setEnabled(false);
                        ColorProxy colorProxy=ColorManager.getInstance().getColor(account);
                        editText.setTextColor(Color.argb(colorProxy.a,colorProxy.r,colorProxy.g,colorProxy.b));
                        colorProxy=ColorManager.getInstance().getColor(account).darker().darker();
                        editText.setBackgroundColor(Color.argb(colorProxy.a,colorProxy.r,colorProxy.g,colorProxy.b));
                        playerList.addView(editText);
                    }
            }
        });
    }

}
