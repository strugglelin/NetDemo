package com.strugglelin.netdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    NetWorkStateReceiver netWorkStateReceiver;
    NetworkCallbackImpl networkCallback;
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BaseActivity1.class));
            }
        });
        findViewById(R.id.btn02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BaseActivity2.class));
            }
        });
    }

    //在onResume()方法注册
    @Override
    protected void onResume() {
        super.onResume();

        // sdk>=24 采用回调
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = new NetworkCallbackImpl();
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        } else {

        }

        netWorkStateReceiver = new NetWorkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);

    }

    //onPause()方法注销
    @Override
    protected void onPause() {
        super.onPause();
        if (netWorkStateReceiver != null) {
            unregisterReceiver(netWorkStateReceiver);
        }
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

        // 网络可用
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.e("tag", "onAvailable:" + network.toString());
        }

        // 网络失去连接的时候回调，但是如果是一个生硬的断开，他可能不回调
        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Log.e("tag", "onLosing:");
        }

        // 网络丢失
        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.e("tag", "onLost:");
        }

        // 当网络的某个能力发生了变化回调，可能会回调多次
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Log.e("tag", "onCapabilitiesChanged:" + networkCapabilities.toString());
        }

        // 当建立网络连接时，回调连接的属性
        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            Log.e("tag", "onLinkPropertiesChanged:" + linkProperties.toString());
        }

        // 如果在超时时间内都没有找到可用的网络时进行回调
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            Log.e("tag", "onUnavailable:");
        }
    }

    public class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // API21之后getNetworkInfo(int networkType)方法被弃用
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取ConnectivityManager对象对应的NetworkInfo对象
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo mobileNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else if (wifiNetworkInfo.isConnected() && !mobileNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
                } else if (!wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
                }
            } else {

                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo wifiNetworkInfo = null;
                NetworkInfo mobileNetworkInfo = null;
                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                //通过循环将网络信息逐个取出来
                for (int i = 0; i < networks.length; i++) {
                    // 获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                    sb.append(networkInfo.getType() + " connect is " + networkInfo.isConnected());
                    switch (networkInfo.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            wifiNetworkInfo = networkInfo;
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            mobileNetworkInfo = networkInfo;
                            break;
                        default:
                            break;
                    }

                }
                if (wifiNetworkInfo != null && mobileNetworkInfo != null && wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                    Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else if ((wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) && (mobileNetworkInfo == null || !mobileNetworkInfo.isConnected())) {
                    Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
                } else if ((wifiNetworkInfo == null || !wifiNetworkInfo.isConnected()) && (mobileNetworkInfo != null && mobileNetworkInfo.isConnected())) {
                    Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
