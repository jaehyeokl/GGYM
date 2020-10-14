package project.jaehyeok.ggym;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AddScheduleActivity extends AppCompatActivity {

    private ClassSchedule addSchedule;

    private DatePicker datePicker;
    private TextView addScheduleStartTime;
    private TextView addScheduleEndTime;
    private EditText addScheduleName;
    private EditText addScheduleManager;
    private EditText addScheduleMax;

    private Button buttonScheduleCreate;

    // date,time picker 통해 전달받는 데이터
    private Calendar cal;
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private String startTime = "";
    private String endTime = "";

    private boolean saveInputText = false;

    /*
     * 스케줄 생성 시 리사이클러뷰의 아이템에도 추가하기 위함
     * 유저 입력만들어진 ClassSchedule 객체를 인텐트를 통해 이전 액티비티로 전달
     * 리사이클러뷰의 데이터로 전달한다
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        // 오늘 날짜로 초기화
        cal = Calendar.getInstance();
        selectYear = cal.get(Calendar.YEAR);
        selectMonth = cal.get(Calendar.MONTH) + 1;
        selectDay = cal.get(Calendar.DAY_OF_MONTH);

        datePicker = findViewById(R.id.changeScheduleDatepicker);
        // 오늘부터 100일 이내 시간까지 날짜 선택 가능
        datePicker.setMinDate(System.currentTimeMillis());
        datePicker.setMaxDate(System.currentTimeMillis() + (24 * 60 * 60 * 1000) * 100L);

        addScheduleStartTime = findViewById(R.id.changeScheduleStartTime);
        addScheduleEndTime = findViewById(R.id.changeScheduleEndTime);
        addScheduleName = findViewById(R.id.changeScheduleName);
        addScheduleManager = findViewById(R.id.changeScheduleManager);
        addScheduleMax = findViewById(R.id.changeScheduleMax);

        buttonScheduleCreate = findViewById(R.id.buttonScheduleDelete);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // SharedPreference 저장된 데이터 있을때 가져오기
        // 앱을 새로 실행할때(onCreate), 화면 전환후 다시 실행될때 (onRestart)
        // 두 경우 모두에서 저장된 데이터를 가져올 수 있도록 하기 위해 onResume 에서 구현
        SharedPreferences saveMakeGroup = getSharedPreferences("saveAddSchedule", MODE_PRIVATE);

        if (saveMakeGroup != null) {
            Set<String> scheduleDataSet = saveMakeGroup.getStringSet("scheduleData", null);
            Iterator<String> iterator;

            String date = "";
            String time = "";
            String name = "";
            String manager = "";
            String max = "";

            if (scheduleDataSet != null) {
                iterator = scheduleDataSet.iterator();
                // 식별을 위해 추가한 String 을 통해서 데이터를 분류
                while (iterator.hasNext()) {
                    String getScheduleData = iterator.next();
                    int index = getScheduleData.indexOf("@");

                    if (getScheduleData.contains("date@")) {
                        date = getScheduleData.substring(index + 1);
                    } else if (getScheduleData.contains("time@")) {
                        time = getScheduleData.substring(index + 1);
                    } else if (getScheduleData.contains("name@")) {
                        name = getScheduleData.substring(index + 1);
                    } else if (getScheduleData.contains("manager@")) {
                        manager = getScheduleData.substring(index + 1);
                    } else if (getScheduleData.contains("max@")) {
                        max = getScheduleData.substring(index + 1);
                    }
                }

                // 날짜는 datepicker 입력, 시간은 시작, 종료시간 나누어 입력
                String dateArray[] = date.split("-");
                selectYear = Integer.parseInt(dateArray[0]);
                selectMonth = Integer.parseInt(dateArray[1]);
                selectDay = Integer.parseInt(dateArray[2]);

                int idx = time.indexOf("-");
                startTime = time.substring(0, idx);
                endTime = time.substring(idx + 1);

                addScheduleStartTime.setText(startTime);
                addScheduleEndTime.setText(endTime);
                addScheduleName.setText(name);
                addScheduleManager.setText(manager);
                addScheduleMax.setText(max);

            } else {
                // 삭제로 인해 Set<String> groupData 가 null 일 경우에는 모든 값을 초기화
//                inputGroupName.setText(name);
//                inputGroupPhone.setText(phone);
//                inputGroupAddress.setText(address);
//                switchOpen.setChecked(groupOpen);
            }
        }

        // datePicker 날짜선택 이벤트 처리
        datePicker.init(selectYear, selectMonth - 1, selectDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                selectYear = year;
                selectMonth = month + 1;
                selectDay = day;
                System.out.println(selectYear + "-" + selectMonth + "-" + selectDay);
//                Toast.makeText(AddScheduleActivity.this, selectYear +","+ selectMonth + "," + selectDay, Toast.LENGTH_SHORT).show();
            }
        });

        // 클래스 시작시간 timePickerDial 에서 받아오기
        addScheduleStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        addScheduleStartTime.setText(startTime);
                    }
                }, 00, 00, true);
                dialog.show();
            }
        });

        // 클래스 종료시간 timePickerDial 에서 받아오기
        addScheduleEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(AddScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        addScheduleEndTime.setText(endTime);

                    }
                }, 00, 00, true);
                dialog.show();
            }
        });

        // 그룹을 생성 - 유저가 입력한 데이터를 추가한 ClassSchedule 객체를 생성
        // 리사이클러뷰에 추가하기 위해서 인텐트를 통해서 GroupSchedule Activity 객체를 전달
        buttonScheduleCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClassSchedule addSchedule;

                // ClassSchedule 객체에 저장할 유저 입력 데이터
                String inputScheduleName = addScheduleName.getText().toString();
                String inputScheduleManager = addScheduleManager.getText().toString();
                int inputScheduleMax;

                try {
                    inputScheduleMax = Integer.parseInt(addScheduleMax.getText().toString());
                } catch (NumberFormatException e) {
                    inputScheduleMax = 0;
                }

                // 데이터 입력 확인 후 ClassSchedule 객체생성
                if (inputScheduleName.length() > 0) {
                    addSchedule = new ClassSchedule(inputScheduleName);

                    if (inputScheduleManager.length() > 0) {
                        addSchedule.setClassMaster(inputScheduleManager);

                        if (inputScheduleMax > 0) {
                            addSchedule.setMaxMember(inputScheduleMax);

                            if (addScheduleStartTime.length() > 0 && addScheduleEndTime.length() > 0) {
                                addSchedule.setStartTime(startTime);
                                addSchedule.setEndTime(endTime);

                                addSchedule.setYear(selectYear);
                                addSchedule.setMonth(selectMonth);
                                addSchedule.setDay(selectDay);

                                // 유저 입력값을 JSON 데이터로 만들어서 인텐트를 통해 전달
                                JSONObject scheduleJson = new JSONObject();
                                try {
                                    scheduleJson.put("scheduleName", inputScheduleName);
                                    scheduleJson.put("scheduleManager", inputScheduleManager);
                                    scheduleJson.put("scheduleMax", inputScheduleMax);
                                    scheduleJson.put("scheduleStartTime", startTime);
                                    scheduleJson.put("scheduleEndTime", endTime);
                                    scheduleJson.put("scheduleYear", selectYear);
                                    scheduleJson.put("scheduleMonth", selectMonth);
                                    scheduleJson.put("scheduleDay",selectDay);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //////////////////////////////////////////////////////////

                                // ClassSchedule 전달
                                Intent intent = new Intent();
                                addSchedule.setDayOfWeek();
                                intent.putExtra("addSchedule", addSchedule);
                                // intent 로 JSON 전달 시 String 으로 변환하여 전달한다
                                // JSON 을 Parcelable 해야하기 번거롭기 때문이다.
                                // 이 방법으로 구현하지 않았음
                                // intent.putExtra("addScheduleJson", scheduleJson.toString());
                                System.out.println(scheduleJson);
                                System.out.println(scheduleJson.toString());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(AddScheduleActivity.this, "시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddScheduleActivity.this, "참석 인원을 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddScheduleActivity.this, "담당자 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddScheduleActivity.this, "클래스 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        // 유저가 입력한 복수의 데이터를 하나의 Set<String> 에 저장한다.
        // 이후 데이터를 Set 에서 다시 가져올때 분류하기위해서 기존 데이터에
        // 식별하기 위한 String 추가 후 SharedPreference 에 저장한다
        if (saveInputText) {
            SharedPreferences saveAddSchedule = getSharedPreferences("saveAddSchedule", MODE_PRIVATE);
            SharedPreferences.Editor saveAddScheduleEditor = saveAddSchedule.edit();

            Set<String> scheduleDataSet = new HashSet<>();

            String scheduleDate = selectYear + "-" + selectMonth + "-" + selectDay;
            String scheduleTime = startTime + "-" + endTime;
            String scheduleName = addScheduleName.getText().toString();
            String scheduleManager = addScheduleManager.getText().toString();
            String scheduleMax = addScheduleMax.getText().toString();

            String scheduleDateForSet = "date@" + scheduleDate;
            String scheduleTimeForSet = "time@" + scheduleTime;
            String scheduleNameForSet = "name@" + scheduleName;
            String scheduleManagerForSet = "manager@" + scheduleManager;
            String scheduleMaxForSet = "max@" + scheduleMax;

            scheduleDataSet.add(scheduleDateForSet);
            scheduleDataSet.add(scheduleTimeForSet);
            scheduleDataSet.add(scheduleNameForSet);
            scheduleDataSet.add(scheduleManagerForSet);
            scheduleDataSet.add(scheduleMaxForSet);

            saveAddScheduleEditor.putStringSet("scheduleData", scheduleDataSet);
            saveAddScheduleEditor.commit();

        } else {
            // 유저가 저장하기를 선택하지 않았을때는 내용을 삭제한다
            SharedPreferences saveMakeGroup = getSharedPreferences("saveAddSchedule", MODE_PRIVATE);
            SharedPreferences.Editor editor = saveMakeGroup.edit();
            editor.clear();
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        boolean nameChanged = (addScheduleName.getText().toString().length() > 0);
        boolean managerChanged = (addScheduleManager.getText().toString().length() > 0);
        boolean maxChanged = (addScheduleMax.getText().toString().length() > 0);
        boolean dateChanged = false;
        boolean timeChanged = false;

        // 날짜 변경 여부
        if (selectYear != cal.get(Calendar.YEAR)) {
            dateChanged = true;
        }
        if (selectMonth != cal.get(Calendar.MONTH) + 1) {
            dateChanged = true;
        }
        if (selectDay != cal.get(Calendar.DAY_OF_MONTH)) {
            dateChanged = true;
        }

        // 시간 변경 여부
        if (startTime.length() > 0 || endTime.length() > 0) {
            timeChanged = true;
        }

        // 작성중인 내용의 SharedPreference 저장 여부를 확인하기 위해
        // 유저 선택에 따른 saveInputText 의 값 true, false 반환
        if (nameChanged || managerChanged || maxChanged || dateChanged || timeChanged) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddScheduleActivity.this);
            dialog.setTitle("작성 중인 내용이 있습니다");
            dialog.setMessage("내용을 저장하시겠습니까?");

            dialog.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveInputText = true;
                    finish();
                }
            });

            dialog.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveInputText = false;
                    finish();
                }
            });

            dialog.setNeutralButton("취소", null);
            dialog.create().show();

        } else {
            super.onBackPressed();
        }
    }
}