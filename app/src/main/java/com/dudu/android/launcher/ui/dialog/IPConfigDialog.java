package com.dudu.android.launcher.ui.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.dudu.android.launcher.ui.activity.IpConfigActivity;

/**
 * Created by lxh on 2016/1/2.
 */
public class IPConfigDialog {

    private  EditText editText;

    public void showDialog(final Context context) {
        editText = new EditText(context);
        final AlertDialog builder = new AlertDialog.Builder(context).create();

        builder.setTitle("提示");

        builder.setView(editText);

        builder.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkInput(context);
                builder.dismiss();
            }
        });

        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });

        builder.show();
    }

    private void checkInput(Context context){
        if(editText!=null&&!TextUtils.isEmpty(editText.getText())){


            if (editText.getText().toString().equals("dudu@0806")){

                context.startActivity(new Intent(context,IpConfigActivity.class));
            }
        }
    }
}
