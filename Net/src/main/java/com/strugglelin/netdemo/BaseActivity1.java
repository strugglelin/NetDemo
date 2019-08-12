package com.strugglelin.netdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * @author struggleLin
 * @date 2019/8/12
 * @description:
 */
public class BaseActivity1 extends AppCompatActivity {

    private NetWorkStateReceiver mNetWorkStateReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNetWorkStateReceiver = new NetWorkStateReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkStateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetWorkStateReceiver != null) {
            unregisterReceiver(mNetWorkStateReceiver);
        }
    }

    public class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetUtil.isConnected(context)) {
                onSuccessNetwork();
            } else {
                onFailNetwork();
            }
        }
    }

    protected void onFailNetwork() {
        Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
    }

    ;

    protected void onSuccessNetwork() {
        Toast.makeText(this, "网络连接成功", Toast.LENGTH_SHORT).show();
    }

    ;
}
