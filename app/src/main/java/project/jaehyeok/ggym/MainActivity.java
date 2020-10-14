package project.jaehyeok.ggym;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

// 메인메뉴 선택
public class MainActivity extends AppCompatActivity {
    // 부모레이아웃
    private LinearLayout parentView;

    private Button buttonReservation;
    private Button buttonSchedule;
    private Button buttonGroupList;
    private Button buttonMyPage;
    private Button buttonCurrentCorona;

    private TextView loadingTextView;
    private LottieAnimationView loadingMenuAnimation;

    private Intent getIntent;
    private String userDataString;

    /*
     * 모든 알람을 정렬하고 알람매니저를 통해 등록을 하는 단계가 필요한데, 어디서 그 작업을 해야할까?
     *
     * 먼저 이전에 AsyncTask 를 적용하려 만져놓았던 액티비티들을 가지런하게 정리해놓는다
     * 가장 먼저 SharedPreferences 에 알람에 관한 데이터가 어떻게 저장될 것인지를 결정해야한다
     * 어디에서 알람매니저를 이용해서 여러개의 알람을 초기화 시킬 수 있을지 결정해야한다
     * 알람매니저를 이용하여 알람을 등록하고, 브로드캐스트로 이벤트를 보낸다
     * 리시버를 만들고 브로드캐스트를 수신하여 알람 액티비티를 실행하도록 한다.
     * 이때 액티비티에서 그룹정보를 가진 데이터를 전달받거나 사용할 수 있는 방법이 있는지 알아본다.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentView = findViewById(R.id.parentLayout);

        buttonReservation = findViewById(R.id.reservation);
        buttonSchedule = findViewById(R.id.schedule);
        buttonGroupList = findViewById(R.id.groupList);
        buttonMyPage = findViewById(R.id.myPage);
        buttonCurrentCorona = findViewById(R.id.currentCorona);

        // 로딩 애니메이션
        loadingTextView = findViewById(R.id.loadingTextView);
        loadingMenuAnimation = findViewById(R.id.loadingLottieView);
        loadingMenuAnimation.playAnimation();
        loadingMenuAnimation.loop(true);

        getIntent = getIntent();
        userDataString = getIntent.getStringExtra("userData");


        final Runnable runnable = new Runnable() {
            // 메인스레드에서 실행할 Runnable, run() 구현
            @Override
            public void run() {
                loadingTextView.setVisibility(View.GONE);
                loadingMenuAnimation.cancelAnimation();
                loadingMenuAnimation.setVisibility(View.GONE);

                buttonReservation.setVisibility(View.VISIBLE);
                buttonSchedule.setVisibility(View.VISIBLE);
                buttonGroupList.setVisibility(View.VISIBLE);
                buttonMyPage.setVisibility(View.VISIBLE);
                buttonCurrentCorona.setVisibility(View.VISIBLE);

                parentView.setBackgroundColor(Color.WHITE);
            }
        };

        // 로딩 효과를 주기위해 애니메이션을 3초간 유지하는 스레드를 실행한다
        // 이후 메인스레드에서 애니메이션이 inVisible 되도록 하고 메뉴버튼이 Visible 되도록 한다
        class LoadingThreeSeconds implements Runnable {
            @Override
            public void run() {

                try {
                    // 시연을 위해 5초동안 보여준다
                    Thread.sleep(5000);
                    //Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(runnable);
            }
        }

        // 로딩 스레드 실행
        LoadingThreeSeconds loadingRunnable = new LoadingThreeSeconds();
        Thread loadingThread = new Thread(loadingRunnable);
        loadingThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        buttonReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);
                intent.putExtra("userData", userDataString);
                startActivity(intent);
            }
        });

        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("userData", userDataString);
                startActivity(intent);
            }
        });

        buttonGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GroupListActivity.class);
                intent.putExtra("userData", userDataString);
                startActivity(intent);
            }
        });

        buttonMyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                intent.putExtra("userData", userDataString);
                startActivity(intent);
            }
        });

        buttonCurrentCorona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CurrentCoronaActivity.class);
                startActivity(intent);
            }
        });
    }

}