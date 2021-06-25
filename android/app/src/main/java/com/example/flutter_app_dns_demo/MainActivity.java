package com.example.flutter_app_dns_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.xml.transform.Result;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import static android.app.Activity.RESULT_OK;


public class MainActivity extends FlutterActivity {
    private static final int VPN_REQUEST_CODE = 0x0F;
    private static final String CHANNEL = "flutter.native/dns";
    private MethodChannel startVpn;
    private String ip = "", dns = "";


    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (LocalVPNService.BROADCAST_VPN_STATE.equals(intent.getAction()))
            {
                if (intent.getBooleanExtra("running", false));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        LocalBroadcastManager.getInstance(this).registerReceiver(vpnStateReceiver,
                new IntentFilter(LocalVPNService.BROADCAST_VPN_STATE));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler((call, result) -> {
            if (call.method.equals("start")) {
                ip = call.argument("ip");
                dns = call.argument("dns");
                startVPN();
            }
        });

        /*vpnControlMethod.setMethodCallHandler((call, result) -> {
                if (call.method.equals("start")) {
                        ip = call.argument("ip");
                        dns = call.argument("dns");
                        startVPN();
                    result.success(null);

                }
        });*/

    }


    private void startVPN()
    {
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            startService(
                    new Intent(this, LocalVPNService.class)
                    .putExtra("ip",ip)
                    .putExtra("dns", dns)
            );
            /*enableButton(false);*/
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

/*
        enableButton(!waitingForVPNStart && !LocalVPNService.isRunning());
*/
    }

    /*private void enableButton(boolean enable)
    {
        final Button vpnButton = (Button) findViewById(R.id.vpn);
        if (enable)
        {
            vpnButton.setEnabled(true);
            vpnButton.setText(R.string.start_vpn);
        }
        else
        {
            vpnButton.setEnabled(false);
            vpnButton.setText(R.string.stop_vpn);
        }
    }*/
}
