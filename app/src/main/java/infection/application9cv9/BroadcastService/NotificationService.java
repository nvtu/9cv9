package infection.application9cv9.BroadcastService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tu Van Ninh on 4/12/2017.
 */
public class NotificationService extends Service{


    Timer mTimer;
    TimerTask timerTask = new TimerTask(){
        @Override
        public void run() {
            Log.d("abc","Running");
            notifiy();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){
        try{
            mTimer.cancel();
            timerTask.cancel();
        } catch(Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent("infection.application9cv9");
        sendBroadcast(intent);
    }

    public void notifiy(){
        Log.d("TuVanNinh", "hahahaa");
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("RSSPullService");
//        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
//        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
//        Context context = getApplicationContext();
//
//        Notification.Builder builder;
//        builder = new Notification.Builder(context)
//                    .setContentTitle("T")
//                    .setContentText("M")
//                    .setContentIntent(pendingIntent)
//                    .setDefaults(Notification.DEFAULT_SOUND)
//                    .setAutoCancel(true)
//                    .setSmallIcon(R.drawable.lock);
//
//        Notification notification = builder.build();
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notification);
    }


}
