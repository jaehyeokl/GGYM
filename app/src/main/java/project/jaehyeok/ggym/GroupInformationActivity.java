package project.jaehyeok.ggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupInformationActivity extends AppCompatActivity {

    private Button buttonJoinGroup;
    private Button buttonOutGroup;

    private String userId;
    private int groupPosition;

    private JSONObject groupDataJson;

//    private final int JOIN_GROUP = 1;
//    private final int OUT_GROUP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 키보드 실행시 레이아웃이 움직일때 레이아웃을 덮도록 하기위해서 사용
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_group_information);

        TextView viewGroupName = findViewById(R.id.selectedGroupName);
        TextView viewGroupPhone = findViewById(R.id.selectedGroupPhone);
        TextView viewGroupAddress = findViewById(R.id.selectedGroupAddress);

        buttonJoinGroup = findViewById(R.id.buttonJoinGroup);
        buttonOutGroup = findViewById(R.id.buttonOutGroup);


        // 전달받은 그룹 데이터를 뷰에 초기화
        Intent getIntent = getIntent();
        String groupDataString = getIntent.getStringExtra("groupJson");
        userId = getIntent.getStringExtra("userId");
        groupPosition = getIntent.getIntExtra("selectPosition", 1);
        // 가입한 그룹 목록의 아이템일 경우에는 true, 탐색 그룹 목록일경우에는 false
        // 통하여 가입하기 또는 탈퇴하기 버튼 중 하나만 보여주도록 한다
        boolean setJoinedButton = getIntent.getBooleanExtra("setJoinedButton",false);
        //System.out.println(setJoinedButton);
        if (setJoinedButton) {
            buttonJoinGroup.setVisibility(View.INVISIBLE);
            buttonOutGroup.setVisibility(View.VISIBLE);
        }

        try {
            groupDataJson = new JSONObject(groupDataString);

            viewGroupName.setText(groupDataJson.getString("groupName"));
            viewGroupPhone.setText(groupDataJson.getString("groupPhone"));
            viewGroupAddress.setText(groupDataJson.getString("groupAddress"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //JSONArray groupMemberListJson = groupDataJson.optJSONArray("groupMemberList");
        //groupMemberListJson.put(userId);
        //System.out.println("멤버리스트 확인 / " + groupMemberListJson);

        // 댓글 리스트뷰

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 그룹 가입하기
        buttonJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 해당그룹의 유저아이디를 저장한 JSONArray (key : groupMemberList) 에 가입한 계정을 추가
                // 유저 아이디를 저장함으로써 유저 아이디를 key 값으로 가지는 userData 에 접근할 수 있도록 한다.
                JSONArray groupMemberListJson = groupDataJson.optJSONArray("groupMemberList");
                String groupName = null;
                try {
                    groupName = groupDataJson.getString("groupName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences getGroupData = getSharedPreferences("saveGroupData", MODE_PRIVATE);
                SharedPreferences.Editor getGroupDataEditor = getGroupData.edit();
                String groupListSting = getGroupData.getString("myGroupListJson", null);

                //System.out.println("null 체크 / " + groupMemberListJson);

                if (groupMemberListJson != null) {

                    groupMemberListJson.put(userId);

                    try {
                        JSONArray groupListJson = new JSONArray(groupListSting);

                        // 전달받은 아이템의 포지션은 그룹 탐색에서 보여지는 아이템의 번호이다
                        // 데이터 수정을 위해서는 전체 그룹데이터에서의 해당 포지션이 필요한데
                        // 현재 가지고 있는 데이터 GroupName 과 일치하는지 비교를통해 전체 그룹데이터에서의 포지션 번호를 구한다
                        int getGroupPosition = 0;

                        for (int i = 0; i < groupListJson.length(); i++) {
                            JSONObject groupData = (JSONObject) groupListJson.get(i);
                            String checkGroupName = groupData.getString("groupName");
                            if (groupName.equals(checkGroupName)) {
                                getGroupPosition = i;
                            }
                        }

                        JSONObject groupJson = (JSONObject) groupListJson.get(getGroupPosition);
                        groupJson.put("groupMemberList", groupMemberListJson);

                        groupListJson.put(getGroupPosition, groupJson);
                        getGroupDataEditor.putString("myGroupListJson", groupListJson.toString());
                        getGroupDataEditor.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 처음 회원을 받는 그룹은 "groupMemberList" key 존재하지 않기 때문에
                    // JSONArray 새로 만든 이후에 userId를 추가할 수 있도록 한다
                    JSONArray newGroupMemberList = new JSONArray();
                    newGroupMemberList.put(userId);
                    //System.out.println("데이터 체크" + groupListJson);
                    try {
                        JSONArray groupListJson = new JSONArray(groupListSting);

                        // 전달받은 아이템의 포지션은 그룹 탐색에서 보여지는 아이템의 번호이다
                        // 데이터 수정을 위해서는 전체 그룹데이터에서의 해당 포지션이 필요한데
                        // 현재 가지고 있는 데이터 GroupName 과 일치하는지 비교를통해 전체 그룹데이터에서의 포지션 번호를 구한다
                        int getGroupPosition = 0;

                        for (int i = 0; i < groupListJson.length(); i++) {
                            JSONObject groupData = (JSONObject) groupListJson.get(i);
                            String checkGroupName = groupData.getString("groupName");
                            if (groupName.equals(checkGroupName)) {
                                getGroupPosition = i;
                            }
                        }

                        JSONObject groupJson = (JSONObject) groupListJson.get(getGroupPosition);
                        groupJson.put("groupMemberList", newGroupMemberList);

                        groupListJson.put(getGroupPosition, groupJson);
                        getGroupDataEditor.putString("myGroupListJson", groupListJson.toString());
                        getGroupDataEditor.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // 유저가 가입을 선택하고 이전 액티비티로 돌아왔을때
                // 즉각적으로 그룹탐색, 내그룹 목록에 변동사항을 적용하는 것이 목적
                // onActivityResult 에서 리사이클러뷰의 ArrayList 데이터를 수정할 수 있도록
                // 그룹(아이템)의 포지션 번호를 전달한다
                Intent intent = new Intent();
                intent.putExtra("joinGroupPosition", groupPosition);
                setResult(RESULT_OK, intent);

                buttonJoinGroup.setVisibility(View.INVISIBLE);
                Toast.makeText(GroupInformationActivity.this, "가입하였습니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 그룹 탈퇴하기
        buttonOutGroup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                JSONArray groupMemberListJson = groupDataJson.optJSONArray("groupMemberList");
                String groupName = null;
                try {
                    groupName = groupDataJson.getString("groupName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences getGroupData = getSharedPreferences("saveGroupData", MODE_PRIVATE);
                SharedPreferences.Editor getGroupDataEditor = getGroupData.edit();
                String groupListSting = getGroupData.getString("myGroupListJson", null);

                //System.out.println("null 체크 / " + groupMemberListJson);

                if (groupMemberListJson != null) {

                    // 멤버 목록에서 유저의 계정을 삭제하기 위한 index 구하기
                    int getUserPosition = 0;
                    for (int i = 0; i < groupMemberListJson.length(); i++) {

                        try {
                            String groupMemberId = (String) groupMemberListJson.get(i);
                            if (groupMemberId.equals(userId)) {
                                getUserPosition = i;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // index 를 통해 유저 아이디 삭제
                    groupMemberListJson.remove(getUserPosition);

                    try {
                        JSONArray groupListJson = new JSONArray(groupListSting);

                        // 전달받은 아이템의 포지션은 그룹 탐색에서 보여지는 아이템의 번호이다
                        // 데이터 수정을 위해서는 전체 그룹데이터에서의 해당 포지션이 필요한데
                        // 현재 가지고 있는 데이터 GroupName 과 일치하는지 비교를통해 전체 그룹데이터에서의 포지션 번호를 구한다
                        int getGroupPosition = 0;
                        for (int i = 0; i < groupListJson.length(); i++) {
                            JSONObject groupData = (JSONObject) groupListJson.get(i);
                            String checkGroupName = groupData.getString("groupName");
                            if (groupName.equals(checkGroupName)) {
                                getGroupPosition = i;
                            }
                        }

                        JSONObject groupJson = (JSONObject) groupListJson.get(getGroupPosition);
                        groupJson.put("groupMemberList", groupMemberListJson);

                        groupListJson.put(getGroupPosition, groupJson);
                        getGroupDataEditor.putString("myGroupListJson", groupListJson.toString());
                        getGroupDataEditor.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // 유저가 가입을 선택하고 이전 액티비티로 돌아왔을때
                // 즉각적으로 그룹탐색, 내그룹 목록에 변동사항을 적용하는 것이 목적
                // onActivityResult 에서 리사이클러뷰의 ArrayList 데이터를 수정할 수 있도록
                // 그룹(아이템)의 포지션 번호를 전달한다
                Intent intent = new Intent();
                intent.putExtra("outGroupPosition", groupPosition);
                setResult(RESULT_OK, intent);

                buttonOutGroup.setVisibility(View.INVISIBLE);
                Toast.makeText(GroupInformationActivity.this, "탈퇴하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }
}