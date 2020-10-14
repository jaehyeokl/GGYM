package project.jaehyeok.ggym;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// 예약하기 팝업창
public class GetReservationActivity extends Activity {
    // Error : You need to use a Theme.AppCompat theme ( or descendant ) with this activity
    // AppCompatActivity 아닌 Activity 상속받음으로써 해결

    private Button reservationSelect;
    private Button reservationCancel;

    private String classNameText;
    private String classManagerText;
    private String classStartTimeText;
    private int classMaxNumberInt;
    private int classAttendNumberInt;
    //private String classEndTimeText;
    //private String classGroupNameText;

    private TextView checkClassName;
    private TextView checkStartTime;
    private TextView checkManagerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_reservation);

        reservationSelect = findViewById(R.id.reservationSelect);
        reservationCancel = findViewById(R.id.reservationCancel);

        // Intent 전달받은 데이터, 팝업창에 예약정보를 표기하기 위해 사용
        Intent intent = getIntent();
        classNameText = intent.getStringExtra("className");
        classManagerText = intent.getStringExtra("classManager");
        classStartTimeText = intent.getStringExtra("classStartTime");
        classMaxNumberInt = intent.getIntExtra("classMaxNumber",1);
        classAttendNumberInt = intent.getIntExtra("classAttendNumber",1);
        // int 불러올때는 intent.getIntExtra, defaultValue -> 1 을 꼭 넣어주어야 불러올 수 있다
        //classEndTimeText = intent.getStringExtra("classEndTime");
        //classGroupNameText = intent.getStringExtra("classGroupName");

        checkClassName = findViewById(R.id.checkClassName);
        checkStartTime = findViewById(R.id.checkStartTime);
        checkManagerName = findViewById(R.id.checkManagerName);

        Log.d("예약팝업", "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkClassName.setText(classNameText);
        checkManagerName.setText(classManagerText);
        checkStartTime.setText(classStartTimeText);

        reservationSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 예약 확인 즉시 클래스의 예약인원이 변하도록 구현하기 위해서
                // 예약인원에 본인을 포함하여 되돌려준다.
                classAttendNumberInt++;

                Intent intent = new Intent();
                intent.putExtra("classAttendNumber", classAttendNumberInt);
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        reservationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 영역 밖 터치로 인한 액티비티 종료 방지
        this.setFinishOnTouchOutside(false);

        Log.d("예약팝업", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("예약팝업", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("예약팝업", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("예약팝업", "onDestroy");
    }

    @Override
    public void onBackPressed() {
//        백버튼 비활성화
//        super.onBackPressed();
    }

}