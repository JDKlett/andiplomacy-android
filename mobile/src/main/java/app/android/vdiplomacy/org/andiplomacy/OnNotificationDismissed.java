package app.android.vdiplomacy.org.andiplomacy;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnNotificationDismissed extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Set", "setting");
        Log.i("Set", "setting");
        context.sendBroadcast(new Intent("NOTIFICATION_DISMISSED"));
//        intent.getExtras().putBoolean("isDismissed", true);
    }

}