package project.jaehyeok.ggym;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmSoundService extends Service {

    MediaPlayer mp;

    public AlarmSoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mp = MediaPlayer.create(getApplication(), R.raw.alram);
        mp.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mp.start();

        Intent currentActivityIntent = new Intent(this, AlarmScheduleActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 1, currentActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알림 생성시 [failed to post notification on channel] 오류 해결위한 채널 생성(오레오 버전 이상)
        // 알림 채널은 한번만 생성하면 계속 유지되기 때문에, 최초 실행 이후에 주석처리 해놓는다
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            NotificationChannelGroup group1 = new NotificationChannelGroup("testGroup1", "testGroup1");
//            notificationManager.createNotificationChannelGroup(group1);
//
//            NotificationChannel notificationChannel = new NotificationChannel("test_channel_id", "test_channel_name", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.setDescription("Notification [failed to post notification on channel] 해결을 위한 채널설정");
//            notificationChannel.setGroup("testGroup1");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.GREEN);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//        System.out.println("############# 채널 설정 완료!");


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "test_channel_id");
        notificationBuilder.setContentIntent(mPendingIntent);
        notificationBuilder.setSmallIcon(android.R.drawable.btn_star);
        notificationBuilder.setContentTitle("알람");
        notificationBuilder.setContentText("알람이 실행중입니다");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(001, notificationBuilder.build());

        // 어플리케이션 종료와 상관없이 서비스가 실행되도록 하기 위해서
        // 서비스를 포그라운드서비스로 전환해준다 (오레오버전 이상일 경우)

        // 서비스를 포그라운드 서비스로 전환 시 [에러 failed to post notification on channel]
        // startForeground()의 인자에 들어가는 Notification 에 알림 채널을 설정해주어야 한다(오레오 버전 이상일 경우)
        Notification notification = notificationBuilder.build();
        startForeground(1, notification);

        Log.d("서비스", "알람 재생");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mp.stop();
        Log.d("서비스", "알람 종료");

    }
}
