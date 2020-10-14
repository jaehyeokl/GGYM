package project.jaehyeok.ggym;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AlarmScheduleActivity extends AppCompatActivity {

    private Button buttonStopAlarm;
    private Button buttonStopActivity;
    private TextView alarmCountText;
    private TextView addTextView;
    private ImageView alarmImage;

    private int countValue;
    private boolean userSelectStop;

    //private MediaPlayer alarmPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_schedule);

        buttonStopAlarm = findViewById(R.id.buttonStopAlarm);
        buttonStopActivity = findViewById(R.id.buttonStopActivity);
        alarmCountText = findViewById(R.id.alarmCheckCount);
        addTextView = findViewById(R.id.addTextView);
        alarmImage = findViewById(R.id.alarmImage);

        userSelectStop = false;

        TextView alarmClassDate = findViewById(R.id.alarmClassDate);
        TextView alarmClassTime = findViewById(R.id.alarmClassTime);
        TextView alarmClassName = findViewById(R.id.alarmClassName);
        TextView alarmClassManager = findViewById(R.id.alarmClassManeger);

        // 인텐트를 통해 전달받은 일정 JSON 데이터
        // 유저에게 일정에 대한 정보를 보여주기 위해 사용한다
        Intent getIntent = getIntent();
        String scheduleString = getIntent.getStringExtra("schedule");
        try {
            JSONObject scheduleJson = new JSONObject(scheduleString);
            String className = scheduleJson.getString("className");
            String classManager = scheduleJson.getString("classManager");
            String classYear = Integer.toString(scheduleJson.getInt("classYear"));
            String classMonth = Integer.toString(scheduleJson.getInt("classMonth"));
            String classDay = Integer.toString(scheduleJson.getInt("classDay"));
            String classDayOfWeek = scheduleJson.getString("classDayOfWeek");
            String classStartTime = scheduleJson.getString("classStartTime");
            String classEndTime = scheduleJson.getString("classEndTime");

            String classDate = classYear + "년 " + classMonth + "월 " + classDay + "일 " + classDayOfWeek;
            String classRunningTime = classStartTime + " - " + classEndTime;

            alarmClassDate.setText(classDate);
            alarmClassTime.setText(classRunningTime);
            alarmClassName.setText(className);
            alarmClassManager.setText(classManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        // 서비스를 onResume 에서 실행하기 위해서 서비스를 실행할 메소드를 가진
        // countFiveSeconds 메소드를 onCreate 가 아닌 onResume 애서 실행한다
        // -> 서비스를 onPause 에서 종료해주면 된다
        CountFiveSeconds countFiveSeconds = new CountFiveSeconds();
        countFiveSeconds.execute();

        // 알람 중지
        buttonStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSelectStop = true;

                //if (alarmPlayer != null) {
                if (true) {
                    // 알람 중지
                    // 유저가 알람이 재생되기 전 알람 중지버튼을 눌렀을 경우
                    // MediaPlayer 인스턴트가 생성되지 않은 상태이기 때문에 null 체크 후 실행
                    //alarmPlayer.stop();
                    /////////
                    Intent intent = new Intent(getApplicationContext(), AlarmSoundService.class);
                    stopService(intent);
                    ////////


                    buttonStopAlarm.setVisibility(View.INVISIBLE);
                    buttonStopActivity.setVisibility(View.VISIBLE);
                    alarmImage.setVisibility(View.INVISIBLE);

                    alarmCountText.setVisibility(View.VISIBLE);
                    alarmCountText.setTextSize(25);
                    alarmCountText.setText("알람을 중지했습니다");
                }
            }
        });

        // 알람 액티비티 종료
        buttonStopActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 알람 사운드 재생
    public void startAlarm() {
        //alarmPlayer = MediaPlayer.create(this, R.raw.alram);
        //alarmPlayer.start();
        /////////
        Intent intent = new Intent(getApplicationContext(), AlarmSoundService.class);
        startService(intent);
        ////////

        // 무한 반복
        //alarmPlayer.setLooping(true);
    }

    // 알람 시작 전 5초를 카운트 하는 스레드
    // 유저가 디바이스 사용 중일때 액티비티가(알람) 실행 될 경우
    // 스레드가 진행되는 5초 동안 유저가 알람 설정 된 일정을 확인 할 수 있도록 하고
    // 이후 선택에 따라 알람이 실행되지 않도록 하기 위해 구현
    class CountFiveSeconds extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            countValue = 5;
            alarmCountText.setText(Integer.toString(countValue));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //super.onPostExecute(integer);
            if (!userSelectStop) {
                // 알람 시작
                startAlarm();
                addTextView.setText("");
                buttonStopAlarm.setText("알람 중지");
                alarmCountText.setVisibility(View.INVISIBLE);
                alarmImage.setVisibility(View.VISIBLE);

            } else {
                addTextView.setText("");
                alarmCountText.setTextSize(25);
                alarmCountText.setText("일정을 확인하였습니다");
                buttonStopAlarm.setVisibility(View.INVISIBLE);
                buttonStopActivity.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            alarmCountText.setText(Integer.toString(values[0]));
        }

        @Override
        protected Integer doInBackground(Integer... integers) {

            while (countValue > 0) {
                // 유저 알람 취소 선택 전까지 5초동안 카운트
                if (!userSelectStop) {
                    try {
                        Thread.sleep(1000);
                        countValue--;
                        publishProgress(countValue);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 유저가 알람 취소 선택 시 스레드 중지
                    break;
                }
            }

            return countValue;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}