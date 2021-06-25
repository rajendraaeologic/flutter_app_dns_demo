/*
** Copyright 2015, Mohamed Naufal
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package com.example.flutter_app_dns_demo;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;

public class LocalVPNService extends VpnService
{

    public static final String BROADCAST_VPN_STATE = "xyz.hexene.localvpn.VPN_STATE";

    private static boolean isRunning = false;

    private ParcelFileDescriptor vpnInterface = null;

    private PendingIntent pendingIntent;

    private String ip, dns;

    @Override
    public void onCreate()
    {
        super.onCreate();
        isRunning = true;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupVPN()
    {
        if (vpnInterface == null)
        {
            Builder builder = new Builder();

           /* PackageManager packageManager = getPackageManager();
            for (String appPackage: appPackages) {
                try {
                    packageManager.getPackageInfo(appPackage, 0);
                    builder.addAllowedApplication(appPackage);
                } catch (PackageManager.NameNotFoundException e) {
                    // The app isn't installed.
                }
            }*/

            builder.addAddress(ip, 32);
            builder.addDnsServer(dns);
            builder.addRoute("::", 0);
            vpnInterface = builder.setSession("Local VPN").setConfigureIntent(pendingIntent).establish();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
         ip = intent.getStringExtra("ip");
         dns = intent.getStringExtra("dns");

        setupVPN();

        return START_STICKY;
    }

    public static boolean isRunning()
    {
        return isRunning;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        isRunning = false;
        cleanup();
    }

    private void cleanup()
    {
        ByteBufferPool.clear();
    }

}
