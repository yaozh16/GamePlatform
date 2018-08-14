package zachary_yao.GamePlatformMobile.RoomComponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import BasicState.PlayerGameState;
import Message.RoomMessage.MRoomStateBroadcast;
import zachary_yao.GamePlatformMobile.R;
import zachary_yao.Universal.ColoredButton;

/**
 * Created by yaozh16 on 18-8-13.
 */

public class MyScorePanelFragment extends Fragment {

    private String account;
    public MyScorePanelFragment setAccount(String account){
        this.account=account;
        return this;
    }
    public static MyScorePanelFragment newInstance(String account){
        return new MyScorePanelFragment().setAccount(account);
    }
    private PlayerGameState playerGameState=null;
    public void updateScorePanel(final MRoomStateBroadcast mRoomStateBroadcast){
        if(this.playerGameState!=null&&this.playerGameState.equals(mRoomStateBroadcast.playerGameStates.get(account)))
            return;
        this.playerGameState=mRoomStateBroadcast.playerGameStates.get(account);
        updateDisplay();
    }
    private ColoredButton coloredButton;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        View view=layoutInflater.inflate(R.layout.room_option_myscorepanel,viewGroup,false);
        coloredButton=view.findViewById(R.id.room_option_myscorepanel_myscore_label);
        coloredButton.setParameter("还未开始",10,10, getResources().getColor(R.color.colorPrimary),20);
        updateDisplay();
        return view;
    }

    private void updateDisplay(){
        if(playerGameState==null)
            return;
        String str=account+"\nlife:"+playerGameState.getLife()+"\nscore:"+playerGameState.getScore();
        final SpannableString spannableString=new SpannableString(account+"\nlife:"+playerGameState.getLife()+"\nscore:"+playerGameState.getScore());
        spannableString.setSpan(new AbsoluteSizeSpan(15,true),0,account.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(12,true),account.length(),str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(coloredButton!=null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    coloredButton.setText(spannableString);
                }
            });
    }
}
