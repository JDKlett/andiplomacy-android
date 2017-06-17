package app.android.vdiplomacy.org.andiplomacy;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.vdiplomacy.android.DiplomacyAuthenticationException;
import org.vdiplomacy.android.DiplomacyClient;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class CheckerService extends IntentService {

    private static final String ACTION_DISMISS = "app.android.vdiplomacy.org.andiplomacy.action.DISMISS";
    private static final String ACTION_START = "app.android.vdiplomacy.org.andiplomacy.action.START";
    private static final String EXTRA_ID = "app.android.vdiplomacy.org.andiplomacy.extra.id";
    private static List<Integer> matches = null;
    private boolean isNotificationShown = false;

    public CheckerService(){
        super("Checker Service");
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public static Intent createActionStart(Context context) {
        Intent intent = new Intent(context, CheckerService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static void startActionStart(Context context) {
        context.startService(createActionStart(context));
    }

    public static Intent createActionDismiss(Context context) {
        Intent intent = new Intent(context, CheckerService.class);
        intent.setAction(ACTION_DISMISS);
        return intent;
    }

    public static void startActionDismiss(Context context) {
        context.startService(createActionDismiss(context));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                try {

                    if(DiplomacyClient.getCookie() == null){
                        try {
                            String cookie = CookieManager.loadCookie(getApplicationContext());
                            if(cookie!=null){
                                DiplomacyClient.login(cookie);
                            }
                        } catch (Exception e) {
                            throw new DiplomacyAuthenticationException();
                        }
                    }

                    if(DiplomacyClient.isConnected()) {
                        if (matches == null) {
                            matches = DiplomacyClient.getMatches();
                        }

                        for (Integer id : matches) {
                            if (!isNotificationShown && DiplomacyClient.checkMessages(id)) {
                                showNotification(this);
                            }
                        }
                    }

                } catch (DiplomacyAuthenticationException e) {
                    Log.e(getClass().getName(), e.getMessage());
                }
            } else if (ACTION_DISMISS.equals(action)) {
                isNotificationShown = false;
            }
        }
    }

    private void showNotification(Context context){
        Intent dismissNotificationIntent = CheckerService.createActionDismiss(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, dismissNotificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Log.d("DEBUG", "Pending event created.");
        isNotificationShown = true;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_content))
                .setSmallIcon(R.drawable.ic_message)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDeleteIntent(pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }
}
