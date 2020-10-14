package project.jaehyeok.ggym;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScheduleInformationActivity extends AppCompatActivity {

    private int itemPosition;

    private DatePicker datePicker;
    private TextView changeScheduleStartTime;
    private TextView changeScheduleEndTime;
    private EditText changeScheduleName;
    private EditText changeScheduleManager;
    private EditText changeScheduleMax;

    private Button buttonScheduleChange;
    private Button buttonScheduleDelete;

    // date,time picker 통해 전달받는 데이터
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private String startTime;
    private String endTime;

    /*
     * 스케줄 정보 및 수정, 삭제
     * resultCode 수정(4002), 삭제(4003)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_information);

        // 인텐트를 불러온다
        Intent getIntent = getIntent();
        ClassSchedule schedule = getIntent.getParcelableExtra("Schedule");
        itemPosition = getIntent.getIntExtra("ItemPosition",1);

        // 저장된 날짜, 시간 초기화
        // 날짜,시간 선택하지 않았을때는 null 값이 아닌 기존 데이터를 저장하기 위함
        selectYear = schedule.getYear();
        selectMonth = schedule.getMonth();
        selectDay = schedule.getDay();
        startTime = schedule.getStartTime();
        endTime = schedule.getEndTime();

        datePicker = findViewById(R.id.changeScheduleDatepicker);
        datePicker.setMinDate(System.currentTimeMillis());
        datePicker.setMaxDate(System.currentTimeMillis() + (24*60*60*1000)*100L);

        changeScheduleStartTime = findViewById(R.id.changeScheduleStartTime);
        changeScheduleEndTime = findViewById(R.id.changeScheduleEndTime);
        changeScheduleName = findViewById(R.id.changeScheduleName);
        changeScheduleManager = findViewById(R.id.changeScheduleManager);
        changeScheduleMax = findViewById(R.id.changeScheduleMax);

        buttonScheduleChange = findViewById(R.id.buttonScheduleChange);
        buttonScheduleDelete = findViewById(R.id.buttonScheduleDelete);

        // 참조한 뷰에 스케줄 데이터를 초기화한다
        datePicker.init(schedule.getYear(),schedule.getMonth() - 1,schedule.getDay(),null);
        changeScheduleStartTime.setText(schedule.getStartTime());
        changeScheduleEndTime.setText(schedule.getEndTime());
        changeScheduleName.setText(schedule.getClassName());
        changeScheduleManager.setText(schedule.getClassMaster());
        changeScheduleMax.setText(String.valueOf(schedule.getMaxMember()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // datePicker 날짜선택 이벤트 처리
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                selectYear = year;
                selectMonth = month + 1;
                selectDay = day;
//                Toast.makeText(AddScheduleActivity.this, selectYear +","+ selectMonth + "," + selectDay, Toast.LENGTH_SHORT).show();
            }
        });

        // 클래스 시작시간 timePickerDial 에서 받아오기
        changeScheduleStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(ScheduleInformationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int time, int minute) {

                        // 시간 숫자가 적을때 간격을 맞추기 위해 앞에 "0" 추가
                        String startTimeZero = "";
                        String startMinuteZero = "";
                        if (time < 10) {
                            startTimeZero = "0";
                        }
                        if (minute < 10) {
                            startMinuteZero = "0";
                        }
                        startTime = startTimeZero + Integer.toString(time) + ":" + startMinuteZero + Integer.toString(minute);
                        // 시간을 설정하면 텍스트뷰에 시간 반영
                        changeScheduleStartTime.setText(startTime);
                    }
                }, 0, 0, true);
                dialog.show();
            }
        });

        // 클래스 종료시간 timePickerDial 에서 받아오기
        changeScheduleEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(ScheduleInformationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int time, int minute) {
                        String startTimeZero = "";
                        String startMinuteZero = "";
                        if (time < 10) {
                            startTimeZero = "0";
                        }
                        if (minute < 10) {
                            startMinuteZero = "0";
                        }
                        endTime = startTimeZero + Integer.toString(time) + ":" + startMinuteZero + Integer.toString(minute);
                        // 시간을 설정하면 텍스트뷰에 시간 반영
                        changeScheduleEndTime.setText(endTime);

                    }
                }, 00, 00, true);
                dialog.show();
            }
        });

        // 그룹 수정 - 유저가 수정한 데이터를 추가한 ClassSchedule 객체를 생성
        // 리사이클러뷰에 수정된 내용 적용하기 위해서 인텐트를 통해서 GroupSchedule Activity 객체를 전달
        buttonScheduleChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClassSchedule changeSchedule;

                // ClassSchedule 객체에 저장할 유저 입력 데이터
                String inputScheduleName = changeScheduleName.getText().toString();
                String inputScheduleManager = changeScheduleManager.getText().toString();
                int inputScheduleMax;

                try {
                    inputScheduleMax = Integer.parseInt(changeScheduleMax.getText().toString());
                } catch (NumberFormatException e) {
                    inputScheduleMax = 0;
                }

                // 데이터 입력 확인 후 ClassSchedule 객체생성
                if (inputScheduleName.length() > 0) {
                    changeSchedule = new ClassSchedule(inputScheduleName);

                    if (inputScheduleManager.length() > 0) {
                        changeSchedule.setClassMaster(inputScheduleManager);

                        if (inputScheduleMax > 0) {
                            changeSchedule.setMaxMember(inputScheduleMax);

                            if (changeScheduleStartTime.length() > 0 && changeScheduleEndTime.length() > 0) {
                                changeSchedule.setStartTime(startTime);
                                changeSchedule.setEndTime(endTime);

                                changeSchedule.setYear(selectYear);
                                changeSchedule.setMonth(selectMonth);
                                changeSchedule.setDay(selectDay);

                                // 수정된 내용으로 새로 만들어진 ClassSchedule 전달
                                Intent intent = new Intent();
                                changeSchedule.setDayOfWeek();
                                intent.putExtra("ChangeSchedule", changeSchedule);
                                intent.putExtra("ChangeItemPosition", itemPosition);
                                setResult(4002, intent);
                                finish();
                            } else {
                                Toast.makeText(ScheduleInformationActivity.this, "시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ScheduleInformationActivity.this, "참석 인원을 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ScheduleInformationActivity.this, "담당자 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ScheduleInformationActivity.this, "클래스 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 그룹 삭제 - 전달받은 아이템의 position 번호를 다시 반환하여
        // 리사이클러뷰의 데이터에서 해당 포지션의 데이터를 삭제
        buttonScheduleDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 삭제를 확인하는 AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleInformationActivity.this);
                builder.setTitle("일정을 삭제 하시겠습니까?");
                builder.setCancelable(false);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 일정의 순번을 인텐트를 통해 전달한다
                        Intent intent = new Intent();
                        intent.putExtra("DeleteItemPosition", itemPosition);
                        setResult(4003, intent);
                        finish();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });
    }

}