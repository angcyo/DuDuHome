package com.dudu.aios.ui.voice;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

/**
 * Created by lxh on 2016/2/13.
 */
public class VoiceFragment extends BaseFragment {

    private Button voiceBack;
    private VoiceCircleAnimView voiceCircleAnimView;
    private VoiceRippleAnimView voiceRippleAnimView;

    @Override
    public View getChildView() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.voice_layout, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

        voiceCircleAnimView = (VoiceCircleAnimView)view.findViewById(R.id.voice_circle);
        voiceRippleAnimView = (VoiceRippleAnimView) view.findViewById(R.id.voice_ripple);

        voiceBack = (Button) view.findViewById(R.id.voiceBack);

        voiceBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dissmiss();
            }
        });

    }


    public void onEventMainThread(VoiceEvent event){
        switch (event){
            case SHOW_MESSAGE:
                voiceCircleAnimView.stopAnim();
                voiceRippleAnimView.stopAnim();
                break;
            case DISMISS_WINDOW:
                dissmiss();
                break;
        }
    }

    private void dissmiss(){
        replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
        voiceCircleAnimView.stopAnim();
        voiceRippleAnimView.stopAnim();
    }

}
