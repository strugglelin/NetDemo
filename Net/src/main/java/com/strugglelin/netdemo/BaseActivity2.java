package com.strugglelin.netdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * @author struggleLin
 * @date 2019/8/12
 * @description:
 */
public class BaseActivity2 extends AppCompatActivity {

    private NetWorkStateReceiver mNetWorkStateReceiver;
    private NetworkCallbackImpl mNetworkCallback;
    private ConnectivityManager mConnectivityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mNetworkCallback = new NetworkCallbackImpl();
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            mConnectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), mNetworkCallback);
        } else {
            mNetWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetWorkStateReceiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetWorkStateReceiver != null) {
            unregisterReceiver(mNetWorkStateReceiver);
        }
        if (mConnectivityManager != null && mNetworkCallback != null) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
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

    private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

        // 网络可用
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            onSuccessNetwork();
            Toast.makeText(BaseActivity2.this, "onAvailable:" + network.toString(), Toast.LENGTH_SHORT).show();
            Log.e("tag", "onAvailable:" + network.toString());
        }

        // 网络失去连接的时候回调，但是如果是一个生硬的断开，他可能不回调
        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Log.e("tag", "onLosing:" + network.toString());
        }

        // 网络丢失
        @Override
        public void onLost(Network network) {
            super.onLost(network);
            onFailNetwork();
            Toast.makeText(BaseActivity2.this, "onLost:" + network.toString(), Toast.LENGTH_SHORT).show();
            Log.e("tag", "onLost:" + network.toString());

        }

        // 当网络的某个能力发生了变化回调，可能会回调多次
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Log.e("tag", "onCapabilitiesChanged:" + networkCapabilities.toString() + ".network:" + network.toString());
        }

        // 当建立网络连接时，回调连接的属性
        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            Log.e("tag", "onLinkPropertiesChanged:" + linkProperties.toString() + ".network:" + network.toString());
        }

        // 如果在超时时间内都没有找到可用的网络时进行回调
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            Log.e("tag", "onUnavailable:");
        }
    }

    protected void onFailNetwork() {
        Toast.makeText(this, "网络连接失败", Toast.LENGTH_LONG).show();
    }

    ;

    protected void onSuccessNetwork() {
        Toast.makeText(this, "网络连接成功", Toast.LENGTH_LONG).show();
    }

    ;
}
