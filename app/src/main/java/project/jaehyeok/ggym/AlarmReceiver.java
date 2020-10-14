package project.jaehyeok.ggym;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");

        //System.out.println("리시버 : 브로드캐스트 수신 완료");
        if (intent.getAction().equals("set.schedule.alarm")) {

            Intent newIntent = new Intent(context, AlarmScheduleActivity.class);
            // 알람 예약할때 인텐트에 저장한 일정 JSON 데이터
            String schedule = intent.getStringExtra("schedule");

            newIntent.putExtra("schedule", schedule);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
            //System.out.println("알람 브로드캐스트 수신, 인텐트 실행");
        }

        // 어플리케이션을 실행중일 때에만 알람이 실행된다.
        // 어플리케이션을 실행하고 있지 않더라도 알람 액티비티를 실행시키기 위해서는
        // 서비스에서 알람 액티비티가 실행되어야 하지 않을까

//        if (intent.getAction().equals("set.schedule.alarm")) {
//
//            Intent serviceIntent = new Intent(context, AlarmSoundService.class);
//            context.startService(serviceIntent);
//            // 알람 예약할때 인텐트에 저장한 일정 JSON 데이터
//            String schedule = intent.getStringExtra("schedule");
//        }

    }
}
