package project.jaehyeok.ggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// 예약
public class ReservationActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Spinner selectGroupSpinner = findViewById(R.id.selectGroupSpinner);

        // 로그인한 계정의 유저데이터
        Intent getIntent = getIntent();
        String userDataString = getIntent.getStringExtra("userData");
        try {
            JSONObject userDataJson = new JSONObject(userDataString);
            userId = userDataJson.getString("userId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 그룹데이터에서 유저아이디가 가입한 그룹을 찾기
        SharedPreferences getGroupData = getSharedPreferences("saveGroupData",MODE_PRIVATE);
        String getGroupListString = getGroupData.getString("myGroupListJson", null);

        ArrayList<GroupObject> groupList = new ArrayList<>();
        Gson gson = new Gson();

        try {
            JSONArray getGroupListJson = new JSONArray(getGroupListString);
            for (int i = 0; i < getGroupListJson.length(); i++) {

                JSONObject groupJson = (JSONObject) getGroupListJson.get(i);
                //System.out.println("groupJsonToString : " + groupJson);
                GroupObject groupObject = gson.fromJson(groupJson.toString(), GroupObject.class);
                groupList.add(groupObject);

                System.out.println(groupObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        GroupObject[] groupObjectList = gson.fromJson(getGroupListString, GroupObject[].class);
//        List<GroupObject> groupList = Arrays.asList(groupObjectList);
//
//        System.out.println(groupList.get(0).toString());

//        for (GroupObject groupObject :groupList) {
//
//        }
    }
}


