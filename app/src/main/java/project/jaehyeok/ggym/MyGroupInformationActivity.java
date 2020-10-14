package project.jaehyeok.ggym;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;

public class MyGroupInformationActivity extends AppCompatActivity {
    private TextView viewGroupName;
    private TextView viewGroupPhone;
    private TextView viewGroupAddress;
    private TextView viewGroupOpen;
    private Button buttonGroupChange;
    private Button buttonGroupDelete;
    private Switch switchCheckAlarm;
    private ImageView groupProfileImage;

    private GroupData getGroupData;

    private int listPosition;

    private final int GET_GALLERY_IMAGE = 3;
    private final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group_information);

        viewGroupName = findViewById(R.id.groupName);
        viewGroupPhone = findViewById(R.id.groupPhone);
        viewGroupAddress = findViewById(R.id.groupAddress);
        viewGroupOpen = findViewById(R.id.groupOpenText);
        buttonGroupChange = findViewById(R.id.buttonGroupChange);
        buttonGroupDelete = findViewById(R.id.buttonGroupDelete);
        switchCheckAlarm = findViewById(R.id.groupCheckAlarm);
        groupProfileImage = findViewById(R.id.myGroupImage);

        // 인텐트로 전달받은 GroupData 를 통해 그룹 정보를 표시한다
        Intent getIntent = getIntent();
        getGroupData = getIntent.getParcelableExtra("GroupData");

        // 삭제시 이전 리스트뷰에 데이터의 순서 알려주기 위한 번호를 받아둔다
        listPosition = getIntent.getIntExtra("listPosition", 1);

        //Log.d("그룹세부정보", getGroupData.toString());

        // 알람에 저장된 일정의 스위치(알람 허용 여부)를 초기화하여 보여준다
        SharedPreferences saveAlarm = getSharedPreferences("saveAlarm", MODE_PRIVATE);
        String alarmGroupData = saveAlarm.getString("alarmGroupData", null);
        JSONObject alarmGroupJsonObject = null;

        if (alarmGroupData != null) {
            try {
                alarmGroupJsonObject = new JSONObject(alarmGroupData);
                String groupName = getGroupData.getGroupName();
                // 그룹 이름(key)을 가지고 있으면 알람 허용된 상태이다
                boolean isChecked = alarmGroupJsonObject.has(groupName);

                if (isChecked) {
                    switchCheckAlarm.setChecked(true);
                } else {
                    switchCheckAlarm.setChecked(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        리스트뷰로부터 전달받은 GroupData, 수정된 GroupData 를 뷰에 나타내는 코드의 중복을 없애기 위해서
        해당 코드가 onCreate 가 아닌 onResume 에서 실행되도록 한다
         */
        String groupProfileUrl = getGroupData.getGroupProfileUrl();
        BitmapAndString bitmapAndString = new BitmapAndString();
        Bitmap bitmap = bitmapAndString.stringToBitmap(groupProfileUrl);

        groupProfileImage.setImageBitmap(bitmap);
        viewGroupName.setText(getGroupData.getGroupName());
        viewGroupPhone.setText(getGroupData.getGroupPhone());
        viewGroupAddress.setText(getGroupData.getGroupAddress());
        if (getGroupData.getGroupOpen()) {
            viewGroupOpen.setText("공개");
        } else {
            viewGroupOpen.setText("비공개");
        }

        /*
        수정된 내용의 GroupData 를 다시 전달받아 해당 액티비티에서 반영한다.
         */
        buttonGroupChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangeGroupActivity.class);
                intent.putExtra("GroupData", getGroupData);
                startActivityForResult(intent, 3002);
            }
        });

        /*
         해당 그룹을 리스트뷰에서 삭제하기 위해
         그룹의 순번을 리스트뷰가 있는 액티비티로 전달한다.
         */
        buttonGroupDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 삭제를 확인하는 AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MyGroupInformationActivity.this);
                builder.setTitle("그룹을 삭제 하시겠습니까?");
                builder.setCancelable(false);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 그룹의 순번을 인텐트를 통해 전달한다
                        Intent intent = new Intent();
                        intent.putExtra("deleteListPosition", listPosition);
                        setResult(RESULT_OK, intent);
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

        // ImageView 터치하여 프로필 이미지를 설정한다.
        groupProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takePhotoAction();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dialogInterface.dismiss();
                    }
                };

                AlertDialog.Builder profileDialog = new AlertDialog.Builder(MyGroupInformationActivity.this);
                profileDialog.setTitle("그룹프로필 등록");
                profileDialog.setPositiveButton("사진촬영", cameraListener);
                profileDialog.setNegativeButton("앨범", albumListener);
                profileDialog.setNeutralButton("취소", cancelListener);

                profileDialog.show();
            }
        });

        // 선택한 알람 그룹을 저장한다
        switchCheckAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                String groupName = getGroupData.getGroupName();
                if (isChecked) {
                    // 알람 활성화
                    JSONArray groupScheduleList = null;
                    try {
                        // 그룹데이터 JSON 에서 해당 그룹(groupName)의 일정 JSONArray 가져온다
                        groupScheduleList = getGroupSchedule(groupName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 알람에 대한 정보를 저장할 SharedPreferences 파일을 새로 생성하여
                    // 알람을 허용하는 그룹명과 일정을 저장한 JSONArray 를 key, value 로 가진 JSON 을 저장한다
                    // 기존의 그룹데이터 JSON 보다 간단하게 파싱하여 일정에 접근하기 위해서 새로운 파일에 저장하였음
                    SharedPreferences saveAlarm = getSharedPreferences("saveAlarm", MODE_PRIVATE);
                    String alarmGroupData = saveAlarm.getString("alarmGroupData", null);

                    if (alarmGroupData != null) {
                        try {
                            JSONObject alarmGroupJsonObject = new JSONObject(alarmGroupData);

                            if (groupScheduleList != null) {
                                // (그룹, 일정) (key, value)형태로 추가
                                alarmGroupJsonObject.put(groupName, groupScheduleList);
                            } else {
                                // 아직 등록된 일정이 없을 경우 빈 JSONArray 를 넣어 null 방지
                                alarmGroupJsonObject.put(groupName, "{[]}");
                            }

                            SharedPreferences.Editor saveAlarmEditor = saveAlarm.edit();
                            saveAlarmEditor.putString("alarmGroupData", alarmGroupJsonObject.toString());
                            saveAlarmEditor.commit();
                            //System.out.println("33" + alarmGroupJsonObject);
                            //Toast.makeText(MyGroupInformationActivity.this, "저장완료", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    // 알람 비활성화
                    // 알람 데이터 전체를 불러온 후 비활성화 그룹을 삭제
                    SharedPreferences saveAlarm = getSharedPreferences("saveAlarm", MODE_PRIVATE);
                    String alarmGroupData = saveAlarm.getString("alarmGroupData", null);

                    if (alarmGroupData != null) {
                        JSONObject alarmGroupJsonObject = null;
                        try {
                            alarmGroupJsonObject = new JSONObject(alarmGroupData);
                            alarmGroupJsonObject.remove(groupName);

                            SharedPreferences.Editor saveAlarmEditor = saveAlarm.edit();
                            saveAlarmEditor.putString("alarmGroupData", alarmGroupJsonObject.toString());
                            saveAlarmEditor.commit();
                            //Toast.makeText(MyGroupInformationActivity.this, "삭제완료", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        그룹 정보를 수정했을때 해당 액티비티에서 가지고 있는 GroupData 전달받은 GroupData 로 변경한다.
        해당 GroupData 를 리스트뷰가 있는 액티비티까지 전달하기 위해서
         */
        if (requestCode == 3002 && resultCode == RESULT_OK) {
            getGroupData = data.getParcelableExtra("ChangeGroupData");

            // 그룹프로필 까지 포함하여서 보낸다
            BitmapDrawable drawable = (BitmapDrawable) groupProfileImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            BitmapAndString bitmapAndString = new BitmapAndString();
            String bitmapString = bitmapAndString.bitmapToString(bitmap);

            getGroupData.setGroupProfileUrl(bitmapString);

            Intent intent = new Intent();
            intent.putExtra("changeGroupData", getGroupData);
            intent.putExtra("changeListPosition", listPosition);
            setResult(3002, intent);

            Log.d("수정확인", getGroupData.toString());
        }

        // 앨범에서 그룹프로필 가져올때
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());

                // !!! FAILED BINDER TRANSACTION !!!
                // 인텐트를 통해 비트맵이미지를 전달할 때, 용량 초과로 인한 오류 발생
                // 이를 해결하기 위해 앨범에서 가져오는 비트맵 크기를 4x4 축소한다
                // Bitmap 이미지의 용량을 줄이기 위한 옵션 설정
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 4;

                Bitmap img = BitmapFactory.decodeStream(in, null, bitmapOptions);
                in.close();

                groupProfileImage.setImageBitmap(img);

                // 프로필 변경내용을 저장한 GroupData 를 이전액티비티로 전달한다 (MyPage)
                // 이전 액티비티에서 그룹데이터 객체를 SharedPreferences 로 저장하기 때문에 꼭 포함해준다
                BitmapDrawable drawable = (BitmapDrawable) groupProfileImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                BitmapAndString bitmapAndString = new BitmapAndString();
                String bitmapString = bitmapAndString.bitmapToString(bitmap);

                getGroupData.setGroupProfileUrl(bitmapString);

                // 프로필 수정과 같은 resultCode 를 사용하여 이미지 수정도 같이 반영되도록 한다
                Intent intent = new Intent();
                intent.putExtra("changeGroupData", getGroupData);
                intent.putExtra("changeListPosition", listPosition);
                setResult(3002, intent);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 그룹프로필 카메라로 촬영할때
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK && data.hasExtra("data")) {

                Bitmap img = (Bitmap) data.getExtras().get("data");
                if (img != null) {
                    groupProfileImage.setImageBitmap(img);

                    // 프로필 변경내용을 저장한 GroupData 를 이전액티비티로 전달한다 (MyPage)
                    // 이전 액티비티에서 그룹데이터 객체를 SharedPreferences 로 저장하기 때문에 꼭 포함해준다
                    BitmapDrawable drawable = (BitmapDrawable) groupProfileImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    BitmapAndString bitmapAndString = new BitmapAndString();
                    String bitmapString = bitmapAndString.bitmapToString(bitmap);

                    getGroupData.setGroupProfileUrl(bitmapString);

                    // 프로필 수정과 같은 resultCode 를 사용하여 이미지 수정도 같이 반영되도록 한다
                    Intent intent = new Intent();
                    intent.putExtra("changeGroupData", getGroupData);
                    intent.putExtra("changeListPosition", listPosition);
                    setResult(3002, intent);
                }
            }
        }
    }

    // 그룹의 일정을 저장한 JSONArray 를 반환
    private JSONArray getGroupSchedule(String groupName) throws JSONException {
        JSONArray groupSchedule = new JSONArray();

        SharedPreferences getGroupList = getSharedPreferences("saveGroupData", MODE_PRIVATE);
        String getGroupDataString = getGroupList.getString("myGroupListJson", null);
        JSONArray getGroupDataJson = new JSONArray(getGroupDataString);

        for (int i = 0; i < getGroupDataJson.length(); i++) {
            JSONObject groupJson = (JSONObject) getGroupDataJson.get(i);
            String getGroupName = groupJson.getString("groupName");

            if (getGroupName.equals(groupName)) {
                groupSchedule = groupJson.getJSONArray("scheduleList");
            }
        }

        return groupSchedule;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // onPause 일때 알람을 지정한다
        //System.out.println("MyGroupInfo : onPause");
        try {
            setAlarm();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 개별 일정에 대한 알람 이벤트를 등록한다
    // 브로드캐스트 이벤트 수신을 통하여 알람 액티비티를 실행하기 때문에
    // AlarmManager 이용하여 일정마다 등록된 시간에 이벤트를 전달하도록 설정한다
    private void setAlarm() throws JSONException {
        //System.out.println("setAlarm 시작점");
        SharedPreferences saveAlarm = getSharedPreferences("saveAlarm", MODE_PRIVATE);
        String alarmGroupData = saveAlarm.getString("alarmGroupData", null);
        JSONArray scheduleJsonArray = new JSONArray();

        // 저장된 알람 데이터는 JSONObject 에 key(그룹이름) : value(일정 JSONArray) 형태로 저장되어있다.
        // AlarmManager 통해 알람을 등록할때 개별 일정에 대한 시간만 필요하기 때문에 그룹이름을 제외한 일정만을 JSONArray 저장
        if (alarmGroupData != null) {
            JSONObject alarmGroupJsonObject = new JSONObject(alarmGroupData);

            for (Iterator<String> keys = alarmGroupJsonObject.keys(); keys.hasNext(); ) {
                String key = keys.next();
                JSONArray scheduleListJson = (JSONArray) alarmGroupJsonObject.get(key);

                for (int i = 0; i < scheduleListJson.length(); i++) {
                    JSONObject schedule = (JSONObject) scheduleListJson.get(i);
                    scheduleJsonArray.put(schedule);
                }
            }
        }

        //System.out.println("일정리스트 : " + scheduleJsonArray);

        // 브로드캐스트 인텐트
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("set.schedule.alarm");
        intent.setPackage("project.jaehyeok.ggym");
        //sendBroadcast(intent);

        // 각각의 예정된 시간에 브로드캐스트 이벤트를 발생시키도록 한다
        for (int i = 0; i < scheduleJsonArray.length(); i++) {
            JSONObject schedule = (JSONObject) scheduleJsonArray.get(i);

            // 알람을 예약할 날짜/시간을 timeInMills 형태로 저장한다
            String classStartTime = schedule.getString("classStartTime");
            String[] hourAndMinute = classStartTime.split(":");
            int hour = Integer.parseInt(hourAndMinute[0]);
            int minute = Integer.parseInt(hourAndMinute[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, schedule.getInt("classYear"));
            calendar.set(Calendar.MONTH, schedule.getInt("classMonth") - 1);
            calendar.set(Calendar.DAY_OF_MONTH, schedule.getInt("classDay"));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            //System.out.println("날짜 추출까지 완료");

            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                // 현 시각 이전의 일정은 등록하지 않음

                // 유저가 알람 액티비티에서 일정에 대한 정보를 확인할 수 있도록 하기 위해
                // 인텐트를 통해 일정 정보가 저장된 JSON 데이터를 전달한다
                intent.putExtra("schedule", schedule.toString());

                // 여러 개의 알람을 등록하기 위해서 getBroadcast 의 두번째 인자를(requestCode)
                // 개별적으로 고유한 값을 가지도록 설정해준다 (for 문의 i로 설정)
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);
//                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15000, pendingIntent);
                //System.out.println("알람매니저 set 완료");
            }
        }
    }

    // 카메라 촬영 (미리보기 가져오기)
    public void takePhotoAction() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    // 앨범에서 가져오기
    public void takeAlbumAction() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }
}