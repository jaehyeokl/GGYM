package project.jaehyeok.ggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// 마이페이지
public class MyPageActivity extends AppCompatActivity {

    private Button buttonMyInformation;
    private Button buttonToMakeGroup;

    private ListView myGroupListView;
    private List<GroupData> myGroupData;
    private MyGroupAdapter myGroupAdapter;

    private String userDataString;
    public String userId;

    private ImageView userProfileImage;
    private TextView userNameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        userProfileImage = findViewById(R.id.userProfileImage);
        userNameText = findViewById(R.id.userName);

        buttonMyInformation = findViewById(R.id.myInformation);
        buttonToMakeGroup = findViewById(R.id.toMakeGroup);

        // 유저 정보 저장
        Intent getIntent = getIntent();
        userDataString = getIntent.getStringExtra("userData");
        try {
            JSONObject userDataJson = new JSONObject(userDataString);
            // 리스트뷰 어댑터 생성자로 userId 를 입력해야 하기 때문에
            // 먼저 변수에 데이터를 저장한다 (nullException 예방)
            userId = userDataJson.getString("userId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 내 그룹 리스트 - 리스트뷰
        myGroupListView = (ListView) findViewById(R.id.listMyGroup);

        myGroupData = new ArrayList<>();
        myGroupAdapter = new MyGroupAdapter(myGroupData, userId);
        myGroupListView.setAdapter(myGroupAdapter);

        // 시연을 위한 리스트뷰 아이템 추가 //////////////////////
//        myGroupData.add(new GroupData("하나필라테스", "02-111-1111", "서울 성북구 111", false));
//        myGroupData.add(new GroupData("둘둘헬스", "02-111-1111", "서울 성북구 222", false));
//        myGroupData.add(new GroupData("성북구 독서모임", "010-0000-0000", "서울 성북구 333", false));
//        myGroupData.add(new GroupData("**온라인 설명회", "010-0000-0000", "서울 성북구 444", false));
//        myGroupData.add(new GroupData("**온라인 독서모임", "010-0000-0000", "서울 성북구 555", false));
        //////////////////////////////////////////////////////////

        // 내 그룹정보 불러오기
        SharedPreferences getGroupList = getSharedPreferences("saveGroupData", MODE_PRIVATE);

        if (getGroupList != null) {
            String groupListToString = getGroupList.getString("myGroupListJson", null);

            if (groupListToString != null) {
                try {
                    JSONArray groupListJson = new JSONArray(groupListToString);
                    int jsonArraySize = groupListJson.length();

                    for (int i = 0; i < jsonArraySize; i++) {
                        JSONObject groupObject = groupListJson.getJSONObject(i);
                        String masterUserId = groupObject.getString("masterUserId");

                        if (masterUserId.equals(userId)) {
                            // 모든 그룹 정보를 저장하고 있는 SharedPreferences 파일에서
                            // 로그인 한 계정에서 개설한 Group 의 데이터만 가져오기 위함.
                            // 저장된 masterUserId 와 인텐트로 전달받은 userData 의 userId 가 일치하는 경우
                            String groupName = groupObject.getString("groupName");
                            String groupPhone = groupObject.getString("groupPhone");
                            String groupAddress = groupObject.getString("groupAddress");
                            boolean groupOpen = groupObject.getBoolean("groupOpen");
                            int groupMember = groupObject.getInt("groupMember");
                            String groupProfileUrl = groupObject.optString("groupProfileUrl");

                            // 그룹 개설 후 등록된 일정이 없을때  key("scheduleList") value 가 없는 오류
                            // org.json.JSONException: No value for scheduleList
                            // 해결을 위해 getJSONArray 아닌 optJSONArray 를 사용한다 (오류 반환이 아닌 ""를 반환하기 때문에)
                            JSONArray scheduleListJson = groupObject.optJSONArray("scheduleList");

                            System.out.println(scheduleListJson);

                            GroupData groupData = new GroupData(masterUserId, groupName, groupPhone, groupAddress, groupOpen);
                            groupData.setGroupMember(groupMember);
                            groupData.setGroupProfileUrl(groupProfileUrl);
                            groupData.setGroupScheduleList(scheduleListJson);

                            myGroupData.add(groupData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d("마이페이지", "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // MyInformation 액티비티에서 백버튼으로 돌아왔을때 (onRestart)
        // 닉네임과 프로필사진의 변경 내용을 반영하기 위해 onResume 에서 구현
        try {
            JSONObject userDataJson = new JSONObject(userDataString);
            String userId = userDataJson.getString("userId");

            SharedPreferences getUserData = getSharedPreferences("userData", MODE_PRIVATE);
            String newDataString = getUserData.getString(userId, null);

            JSONObject newDataJson = new JSONObject(newDataString);
            // 유저 닉네임
            String userName = newDataJson.getString("userName");
            userNameText.setText(userName);
            // 유저 프로필 이미지
            String profileImageString = newDataJson.getString("userProfileImage");
            BitmapAndString bitmapAndString = new BitmapAndString();
            Bitmap bitmap = bitmapAndString.stringToBitmap(profileImageString);
            userProfileImage.setImageBitmap(bitmap);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        buttonMyInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyInformationActivity.class);
                intent.putExtra("userData", userDataString);
                startActivity(intent);
            }
        });

        buttonToMakeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MakeGroupActivity.class);
                intent.putExtra("userData", userDataString);
                startActivityForResult(intent, 3000);
            }
        });

        Log.d("마이페이지", "onResume");

        // 아이템 클릭으로 세부 정보 액티비티로 이동한다
        // 이때 나타내야할 해당 아이템(그룹) 정보를 인텐트를 통해서 전달한다
        myGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MyGroupInformationActivity.class);
                GroupData setGroupData = myGroupData.get(i);
                intent.putExtra("GroupData", setGroupData);
                // startActivity 에서 그룹 삭제를 원할 경우 해당 position(순서)를 전달받기 위해서
                intent.putExtra("listPosition", i);
                startActivityForResult(intent, 3001);

                // 리스트뷰 아이템 클릭 반응 확인
                // Toast.makeText(MyPageActivity.this, i + "번째", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 3000:
                    // MakeGroup Activity 에서 그룹생성이 정상적으로 완료됐을때
                    // Intent 로 전달받은 GroupData 객체를 리스트뷰에 업데이트한다
                    GroupData createdGroup = data.getParcelableExtra("GroupData");
                    myGroupData.add(createdGroup);
                    myGroupAdapter.notifyDataSetChanged();

                    CharSequence text = "그룹을 개설하였습니다!";
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                    break;

                case 3001:
                    // MyGroupInformation Activity 에서 그룹 삭제를 선택했을 경우
                    // 리스트뷰의 데이터에서 삭제하기위해 인텐트를 통해 데이터의 순서를 전달받는다
                    int deletePosition = data.getIntExtra("deleteListPosition", 1);
                    myGroupData.remove(deletePosition);
                    myGroupAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "그룹이 삭제되었습니다", Toast.LENGTH_SHORT).show();
//                    Log.d("포지션넘버",data.getIntExtra("deleteListPosition",1) + "번째 아이템 삭제");
                    break;
            }
        }

        // 수정된 GroupData 를 리스트뷰 데이터에 반영
        if (resultCode == 3002) {
            Log.d("마이페이지", "수정된 그룹데이터 도착");
            GroupData changeGroupData = data.getParcelableExtra("changeGroupData");
            int changeListPosition = data.getIntExtra("changeListPosition", 1);
            myGroupData.remove(changeListPosition);
            myGroupData.add(changeListPosition, changeGroupData);
            myGroupAdapter.notifyDataSetChanged();
        }

        // MyPageActivity 로 돌아왔을때 전달받은 스케줄 JSON 을 저장한다.
        // JSON 파일을 전달받기 위해서 해당 액티비티에서 startActivityForResult 구현할 수 있도록 해야한다.
        if (resultCode == 3003) {
            String scheduleListString = data.getStringExtra("scheduleList");
            int position = data.getIntExtra("groupPosition", 1);
            try {
                JSONArray scheduleListJson = new JSONArray(scheduleListString);
                myGroupData.get(position).setGroupScheduleList(scheduleListJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // System.out.println("Adaptor startActivityForResult 작동" + myGroupData);
        }
        Log.d("마이페이지", "onActivityResult");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();

        // 데이터 저장
        // onPause 일때 매번 SharedPreference 를 저장
        // 해당 액티비티가 언제 종료가 되더라도 액티비티가 onCreate 될 때 저장된 아이템을 보여줄 수 있도록 하기 위해
        JSONArray groupListJson = groupListToJson();

        SharedPreferences saveGroupList = getSharedPreferences("saveGroupData", MODE_PRIVATE);
        SharedPreferences.Editor saveMyGroupListEditor = saveGroupList.edit();

        // 기존에 해당 계정으로 만든 그룹을 다 삭제하고 다시 현재 데이터를 저장
        // 그룹 추가, 변경, 삭제 세가지 경우 모두 적용되도록 해야 하기 때문에
        String getGroupListString = saveGroupList.getString("myGroupListJson", null);

        try {
            // 현재 계정으로 된 것 삭제
            JSONArray getGroupListJson = new JSONArray(getGroupListString);

            for (int i = getGroupListJson.length() - 1; i >= 0; i--) {
                JSONObject getGroupJson = (JSONObject) getGroupListJson.get(i);
                String getGroupMasterId = getGroupJson.getString("masterUserId");

                if (getGroupMasterId.equals(userId)) {
                    getGroupListJson.remove(i);
                }
            }

            // 새로운 데이터 추가
            for (int i = 0; i < groupListJson.length(); i++) {
                JSONObject saveGroupJson = (JSONObject) groupListJson.get(i);
                getGroupListJson.put(saveGroupJson);
            }

            saveMyGroupListEditor.putString("myGroupListJson", getGroupListJson.toString());
            saveMyGroupListEditor.commit();

            //System.out.println("그룹 저장 정보 / " + getGroupListJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("저장 확인" + groupListJson);
    }

    // 현재 ArrayList<ClassSchedule> -> JSONArray 변환
    // 한번에 ClassSchedule -> JSONObject, ArrayList -> JSONArray 를 변환함으로써
    // 다른 액티비티에서 ClassSchedule 을 JSONObject 로 변환 / Intent 를 통한 전달을 없애고
    // 현재 액티비티 클래스에서 모든 데이터를할 JSON 으로 변환 할 수 있도록 함
    public JSONArray groupListToJson() {
        JSONArray groupListJson = new JSONArray();

        try {
            for (GroupData groupData : myGroupData) {
                JSONObject groupDataJson = new JSONObject();
                groupDataJson.put("masterUserId", groupData.getMasterUserId());
                groupDataJson.put("groupName", groupData.getGroupName());
                groupDataJson.put("groupPhone", groupData.getGroupPhone());
                groupDataJson.put("groupAddress", groupData.getGroupAddress());
                groupDataJson.put("groupOpen", groupData.getGroupOpen());
                groupDataJson.put("groupMember", groupData.getGroupMember());

                if (groupData.getGroupProfileUrl() != null) {
                    groupDataJson.put("groupProfileUrl", groupData.getGroupProfileUrl());
                } else {
                    groupDataJson.put("groupProfileUrl", null);
                }

                if (groupData.getGroupMemberList() != null) {
                    groupDataJson.put("groupMemberList", groupData.getGroupMemberList());
                } else {
                    groupDataJson.put("groupMemberList", null);
                }

                if (groupData.getGroupScheduleList() != null) {
                    groupDataJson.put("scheduleList", groupData.getGroupScheduleList());
                } else {
                    groupDataJson.put("scheduleList", null);
                }

//                if (groupData.getGroupReview() != null) {
//                    groupDataJson.put("groupReviewList", groupData.getGroupReview());
//                } else {
//                    groupDataJson.put("groupReviewList", null);
//                }

                //System.out.println("그룹 데이터 저장여부" + myGroupDataJson);
                groupListJson.put(groupDataJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // System.out.println("그룹 데이터 Array 저장여부" + myGroupListJson);
        return groupListJson;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("마이페이지", "onDestroy");
    }
}