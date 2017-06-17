package app.android.vdiplomacy.org.andiplomacy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class Dashboard extends AppCompatActivity {

    private int mInterval = 300000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try {
                    CookieManager.resetCookie(getApplicationContext());
                } catch (IOException e) {

                }
                Intent i = new Intent(Dashboard.this, LoginActivity.class);
                cancelCheck();
                finish();
                startActivity(i);
            }
        });

        scheduleCheck();
    }

    private void scheduleCheck(){
        Intent intent = CheckerService.createActionStart(this);
        PendingIntent checkIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
        SystemClock.elapsedRealtime()+10, mInterval, checkIntent);
    }

    private void cancelCheck(){
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = CheckerService.createActionStart(this);
        PendingIntent checkIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(checkIntent);
    }
}
