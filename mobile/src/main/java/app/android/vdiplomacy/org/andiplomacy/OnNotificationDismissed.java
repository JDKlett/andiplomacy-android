package app.android.vdiplomacy.org.andiplomacy;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnNotificationDismissed extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("NOTIFICATION_DISMISSED"));
    }

}