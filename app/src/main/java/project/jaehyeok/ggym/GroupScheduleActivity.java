package project.jaehyeok.ggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GroupScheduleActivity extends AppCompatActivity {

    private Button buttonAddSchedule;

    private GroupData groupData;
    private ArrayList<ClassSchedule> scheduleList;
    private GroupScheduleAdapter adapter;
    private int groupDataPosition;

    private String userId;
    private String groupName;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_schedule);

        TextView titleBar = findViewById(R.id.groupNameBar);
        buttonAddSchedule = findViewById(R.id.buttonAddSchedule);

        // 이곳에서 전달받는 인텐트는 MyPageActivity 의 리스트뷰 MyGroupAdapter 에서 전달받음
        Intent getIntent = getIntent();
        groupData = getIntent.getParcelableExtra("GroupData");
        groupDataPosition = getIntent.getIntExtra("GroupDataPosition", 1);
        userId = getIntent.getStringExtra("userId");
        groupName = groupData.getGroupName();

        titleBar.setText(groupData.getGroupName());

        // 리사이클러뷰 데이터
        scheduleList = groupData.getScheduleList();

        // 리사이클러뷰
        RecyclerView scheduleRecyclerView = findViewById(R.id.recycleGroupSchedule);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));

        // 어댑터
        adapter = new GroupScheduleAdapter(scheduleList);
        scheduleRecyclerView.setAdapter(adapter);
        adapter.setUserId(userId);
        adapter.setGroupName(groupName);

        ////// 시연, 테스트 위한 데이터 추가 /////////////////////////
//        testData("필라테스 기초");
//        testData("필라테스 중급");
//        testData("필라테스 중급");
//        testData("필라테스 고급");
//        testData("필라테스 기초");
        //////////////////////////////////////////////////////////////

        // 저장된 SharedPreference JSON 데이터 받아서 화면에 표기
        // (onResume) 에서 아이템 추가, 수정, 삭제와 같은 일정 아이템의 변경내용을 adapter.notifyDataSetChanged()
        // 통하여 화면에 나타내기 때문에, 액티비티가 실행하는 단계에서만 (onCreate) SharedPreference 파일을 불러온다

        // 하나의 SharedPreferences 파일에 전체 그룹데이터를 저장한다
        // 그룹 추가 삭제 시 생성된 파일의 내용을 지울 수 있지만, 파일 자체는 쌓이기 때문에
        SharedPreferences getGroupData = getSharedPreferences("saveGroupData", MODE_PRIVATE);

        if (getGroupData != null) {

            String groupListString = getGroupData.getString("myGroupListJson", null);
            System.out.println("그룹데이터 JSON" + groupListString);
            try {
                JSONArray groupListJson = new JSONArray(groupListString);

                if (groupListJson != null) {
                    // 인텐트로 전달받은 그룹의 리스트뷰 아이템 포지션 번호를 사용한다
                    //JSONObject groupJson = groupListJson.getJSONObject(groupDataPosition);
                    //System.out.println("그룹 JSON" + groupJson);

                    // 한 계정에서 만들어진 그룹 데이터는 해당 데이터를 아이템으로 가지는 리스트뷰의 나열 순서대로
                    // JSON 에 저장된다. 그래서 JSON 데이터에서 로그인한 유저 아이디와 같고, 그룹명이 일치한 조건의
                    // 그룹 데이터를 순서대로 나열하면 리스트뷰의 아이템 순서와 동일하다.
                    JSONObject groupJson = new JSONObject();
                    int checkItemPosition = 0;

                    for (int i = 0; i < groupListJson.length(); i++) {
                        JSONObject checkGroup = (JSONObject) groupListJson.get(i);
                        String masterUserId = checkGroup.getString("masterUserId");

                        if (userId.equals(masterUserId)) {

                            if (checkItemPosition == groupDataPosition) {
                                groupJson = checkGroup;
                                break;
                            } else {
                                checkItemPosition++;
                            }
                        }
                    }

                    if (groupJson.has("scheduleList")) {
                        String groupScheduleListString = groupJson.getString("scheduleList");

                        if (groupScheduleListString != null) {

                            JSONArray groupScheduleListJson = new JSONArray(groupScheduleListString);
                            int scheduleListSize = groupScheduleListJson.length();

                            for (int i = 0; i < scheduleListSize; i++) {
                                JSONObject groupScheduleJson = groupScheduleListJson.getJSONObject(i);

                                ClassSchedule schedule = new ClassSchedule(groupScheduleJson.getString("className"));
                                schedule.setClassMaster(groupScheduleJson.getString("classManager"));
                                schedule.setYear(groupScheduleJson.getInt("classYear"));
                                schedule.setMonth(groupScheduleJson.getInt("classMonth"));
                                schedule.setDay(groupScheduleJson.getInt("classDay"));
                                schedule.setStartTime(groupScheduleJson.getString("classStartTime"));
                                schedule.setEndTime(groupScheduleJson.getString("classEndTime"));
                                schedule.setMaxMember(groupScheduleJson.getInt("classMaxMember"));
                                schedule.setReserveMember(groupScheduleJson.getInt("classReserveMember"));
                                //schedule.setCheckAlarm(groupScheduleJson.getBoolean("classAlarmCheck"));
                                // 요일은 ClassSchedule 날짜를 통해 계산하는 메소드 사용
                                schedule.setDayOfWeek();

                                scheduleList.add(schedule);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 일정 목록 리사이클러뷰의 아이템에는 일정까지 남은 시간을 표기하는 TextView 존재.
        // 시간이 흐름에 따라 아이템에 표기되는 남은 시간의 변화를 즉각적으로 보여주기 위해서
        // 스레드로 1분마다 리사이클러뷰를 새로고침 해준다. adapter.notifyDataSetChanged();
        RefreshLeftTimeThread refreshLeftTimeThread = new RefreshLeftTimeThread();
        refreshLeftTimeThread.start();

    }

    // 일정 목록 리사이클러뷰 1분간격으로 새로고침 스레드
    class RefreshLeftTimeThread extends Thread {

        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ViewHolder 가 아닌 액티비티에서 아이템에 대한 클릭이벤트를 처리하기 위한 메서드
        adapter.setOnItemClickListener(new GroupScheduleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                ClassSchedule schedule = groupData.getScheduleList().get(position);
                //Toast.makeText(view.getContext(), position + scheduleList.getClassName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ScheduleInformationActivity.class);
                intent.putExtra("Schedule", schedule);
                intent.putExtra("ItemPosition", position);
                startActivityForResult(intent, 4001);
            }
        });

        // 일정 추가
        buttonAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddScheduleActivity.class);
                startActivityForResult(intent, 4000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 4000) {
                // 일정 생성 완료
                // 생성된 일정을 리사이클러뷰의 아이템에 추가하기위해
                // 전달받은 ClassSchedule 객체를 리사이클러뷰 데이터에 추가
                ClassSchedule addSchedule = data.getParcelableExtra("addSchedule");
                scheduleList.add(addSchedule);
                //그룹 날짜순으로 정렬
                ScheduleTimeSort comparator = new ScheduleTimeSort();
                Collections.sort(scheduleList, comparator);
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "일정이 추가되었습니다", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 4001) {
            switch (resultCode) {
                case 4002:
                    // 일정 수정 완료 - 전달받은 포지션에 수정된 ClassSchedule 교체
                    ClassSchedule changeClassSchedule = data.getParcelableExtra("ChangeSchedule");
                    int changeItemPosition = data.getIntExtra("ChangeItemPosition", 1);
                    scheduleList.set(changeItemPosition, changeClassSchedule);

                    ScheduleTimeSort comparator = new ScheduleTimeSort();
                    Collections.sort(scheduleList, comparator);

                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();
                    break;

                case 4003:
                    // 일정 삭제 완료
                    int deleteItemPosition = data.getIntExtra("DeleteItemPosition", 1);
                    scheduleList.remove(deleteItemPosition);

                    comparator = new ScheduleTimeSort();
                    Collections.sort(scheduleList, comparator);

                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();

        // 현재 일정 JSON 저장하기
        JSONArray resetScheduleListJson = scheduleListToJson();

        SharedPreferences getGroupData = getSharedPreferences("saveGroupData", MODE_PRIVATE);
        SharedPreferences.Editor getGroupDataEditor = getGroupData.edit();
        String groupListString = getGroupData.getString("myGroupListJson", null);

        // 전체 그룹 -> 로그인한 계정으로 개설된 그룹, 이후 유저가 선택한 특정 그룹을 찾기 위해서
        // 유저가 리스트뷰에서 선택한 아이템의 포지션번호를 인텐트로 전달 받아 사용한다
        if (groupListString != null) {
            try {
                JSONArray groupListJson = new JSONArray(groupListString);
                int checkItemPosition = 0;

                for (int i = groupListJson.length() - 1; i >= 0; i--) {
                    JSONObject groupJson = (JSONObject) groupListJson.get(i);
                    String masterUserId = groupJson.getString("masterUserId");

                    if (masterUserId.equals(userId)) {

                        if (checkItemPosition == groupDataPosition) {
                            // 선택한 그룹데이터 접근 후 일정 덮어쓴 후 JSON 새로 반영
                            groupJson.put("scheduleList", resetScheduleListJson);

                            groupListJson.put(i, groupJson);
                            getGroupDataEditor.putString("myGroupListJson", groupListJson.toString());
                            getGroupDataEditor.commit();
                        } else {
                            checkItemPosition++;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        // 하나의 SharedPreferences 파일에 GroupData 를 저장하기 때문에
//        // 파싱하여 전체 그룹목록 -> 특정 아이템 그룹 -> 특정 아이템그룹의 scheduleList 새로 저장 후
//        // 다시 특정 아이템그룹을 전체 그룹목록에 저장하여 데이터 전체를 SharedPreferences 파일 저장
//        JSONArray scheduleListJson = scheduleListToJson();
//        //System.out.println("저장할 JSONArray 메소드 실행 후" + scheduleListJson);
//        SharedPreferences getGroupData = getSharedPreferences("saveGroupData", MODE_PRIVATE);
//        SharedPreferences.Editor getGroupDataEditor = getGroupData.edit();
//
//        String groupListString = getGroupData.getString("myGroupListJson", null);
//
//        // System.out.println("저장된것을 불러왔는가? : " + myGroupListString);
//        if (groupListString != null) {
//            try {
//                JSONArray groupListJson = new JSONArray(groupListString);
//                JSONObject groupJson = groupListJson.getJSONObject(groupDataPosition);
//                // System.out.println("해당 아이템의 그룹데이터인가?" + myGroupJson);
//
//                if (groupJson != null) {
//                    // 현재 시점의 scheduleList JSON 데이터를 새롭게 저장
//                    groupJson.put("scheduleList", scheduleListJson);
//                    // System.out.println("추가후 그룹 데이터" + myGroupJson);
//
//                    // 해당 그룹데이터를 그룹리스트에 저장
//                    groupListJson.put(groupDataPosition, groupJson);
//                    // System.out.println("추가후 그룹리스트 데이터" + myGroupListJson);
//
//                    // 최종 JSONArray 데이터를 SharedPreferences 저장
//                    getGroupDataEditor.putString("myGroupListJson", groupListJson.toString());
//                    System.out.println(groupListJson);
//                    getGroupDataEditor.commit();
//                    // Toast.makeText(this, "스케줄저장완료", Toast.LENGTH_SHORT).show();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
//        }

        Log.d("그룹스케줄", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();


        Log.d("그룹스케줄", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("그룹스케줄", "onDestroy");
    }

    // scheduleList 의 아이템을 시간순으로 정렬하기
    // 추가 했을때 기존 데이터가 누락됨
    class ScheduleTimeSort implements Comparator<ClassSchedule> {
        @Override
        public int compare(ClassSchedule t0, ClassSchedule t1) {
            String t0StartMonthZero = "";
            String t0StartDayZero = "";
            String t1StartMonthZero = "";
            String t1StartDayZero = "";

            // 월,일 10보다 작을때 yyyyMMdd 포맷 형식에 맞게 변환하기 위해 0을 추가
            if (t0.getMonth() < 10) {
                t0StartMonthZero = "0";
            }
            if (t0.getDay() < 10) {
                t0StartDayZero = "0";
            }
            if (t1.getMonth() < 10) {
                t1StartMonthZero = "0";
            }
            if (t1.getDay() < 10) {
                t1StartDayZero = "0";
            }

            // 하나의 문자로 만든 후 다시 숫자로 변환하여 크기를 비교하여 날짜순 정렬
            String t0Date = Integer.toString(t0.getYear()) + t0StartMonthZero + Integer.toString(t0.getMonth()) + t0StartDayZero + Integer.toString(t0.getDay());
            String t1Date = Integer.toString(t1.getYear()) + t1StartMonthZero + Integer.toString(t1.getMonth()) + t1StartDayZero + Integer.toString(t1.getDay());

            int t0DateInt = Integer.parseInt(t0Date);
            int t1DateInt = Integer.parseInt(t1Date);


            if (t0DateInt > t1DateInt) {
                return 1;
            }
            if (t0DateInt < t1DateInt) {
                return -1;
            }
            return 0;
        }
    }

    // 테스트, 시연용 아이템 생성 메소드
//    public void testData(String className) {
//        ClassSchedule testSchedule = new ClassSchedule(className);
//
//        testSchedule.setYear(2020);
//        testSchedule.setMonth(9);
//        testSchedule.setDay(16);
//        testSchedule.setStartTime("18:00");
//        testSchedule.setEndTime("19:00");
//        testSchedule.setClassMaster("ㅇㅇ선생님");
//        testSchedule.setMaxMember(10);
//
//        testSchedule.setDayOfWeek();
//
//        scheduleList.add(testSchedule);
//    }

    // 현재 ArrayList<ClassSchedule> -> JSONArray 변환
    // 한번에 ClassSchedule -> JSONObject, ArrayList -> JSONArray 를 변환함으로써
    // 다른 액티비티에서 ClassSchedule 을 JSONObject 로 변환 / Intent 를 통한 전달을 없애고
    // 현재 액티비티 클래스에서 모든 데이터를할 JSON 으로 변환 할 수 있도록 함
    public JSONArray scheduleListToJson() {
        JSONArray scheduleListJson = new JSONArray();

        try {
            for (ClassSchedule classSchedule : scheduleList) {
                JSONObject classScheduleJson = new JSONObject();
                classScheduleJson.put("className", classSchedule.getClassName());
                classScheduleJson.put("classManager", classSchedule.getClassMaster());
                classScheduleJson.put("classYear", classSchedule.getYear());
                classScheduleJson.put("classMonth", classSchedule.getMonth());
                classScheduleJson.put("classDay", classSchedule.getDay());
                classScheduleJson.put("classDayOfWeek", classSchedule.getDayOfWeek());
                classScheduleJson.put("classStartTime", classSchedule.getStartTime());
                classScheduleJson.put("classEndTime", classSchedule.getEndTime());
                classScheduleJson.put("classMaxMember", classSchedule.getMaxMember());
                classScheduleJson.put("classReserveMember", classSchedule.getReserveMember());
                //classScheduleJson.put("classAlarmCheck",classSchedule.isCheckAlarm());

                scheduleListJson.put(classScheduleJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //System.out.println(scheduleListJson);
        System.out.println("저장할 JSONArray" + scheduleListJson.toString());

        return scheduleListJson;
    }

    @Override
    public void onBackPressed() {
        // 백버튼을 통해 MyPageActivity 로 이동할때 ScheduleList 의 JSON 전달
        // MyPageActivity 의 onPause 에서 SharedPreferences 저장할때
        // ScheduleList 의 데이터를 이곳 Activity 로 다시 전달받을 수 있게 하기 위해
        JSONArray scheduleListJson = scheduleListToJson();
        Intent intent = new Intent();
        intent.putExtra("scheduleList", scheduleListJson.toString());
        intent.putExtra("groupPosition", groupDataPosition);
        setResult(3003, intent);
        // Toast.makeText(this, "백버튼 전달", Toast.LENGTH_SHORT).show();

        super.onBackPressed();


    }
}