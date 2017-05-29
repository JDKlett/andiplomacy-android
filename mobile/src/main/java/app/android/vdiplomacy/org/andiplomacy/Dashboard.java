package app.android.vdiplomacy.org.andiplomacy;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.vdiplomacy.android.DiplomacyAuthenticationException;
import org.vdiplomacy.android.DiplomacyClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity {

    private int mInterval = 300;
    private boolean isNotificationShown = false;
    private BroadcastReceiver broadcastReceiver = null;
    private BroadcastReceiver dismissReceiver = null;
    private CheckMessagesTask cmTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isNotificationShown = false;
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("NOTIFICATION_DISMISSED"));

        dismissReceiver = new OnNotificationDismissed();
        registerReceiver(dismissReceiver, new IntentFilter("NOTIFICATION_DELETE_REQUEST"));

        cmTask = new CheckMessagesTask();
        cmTask.execute((Void) null);
    }

    private class MessageChecker implements Runnable {

        private int id = 0;

        public MessageChecker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Log.d(getClass().getName(), "Checking presence of messages for game["+id+"]");
                if(!isNotificationShown && DiplomacyClient.checkMessages(id)){
                    Log.d(getClass().getName(), "Game ["+id+"] has new messages.");
                    showNotification();
                }
            } catch (Throwable e) {
                //DO NOTHING
            }
        }
    }

    private void showNotification(){
        Intent dismissNotificationIntent = new Intent("NOTIFICATION_DELETE_REQUEST");
        isNotificationShown = true;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, dismissNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("DEBUG", "Pending event created.");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext());
        builder
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.drawable.ic_message)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDeleteIntent(pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    public class CheckMessagesTask extends AsyncTask<Void, Void, Boolean> {

        public CheckMessagesTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                List<Integer> matches = DiplomacyClient.getMatches();
                for(Integer id: matches){
                    ScheduledExecutorService scheduler =
                            Executors.newSingleThreadScheduledExecutor();
                    scheduler.scheduleAtFixedRate(new MessageChecker(id),  5, mInterval, TimeUnit.SECONDS);
                    Log.i(getClass().getName(), "Created scheduler for match id:"+id);
                }
            } catch (DiplomacyAuthenticationException e) {
                return false;
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
