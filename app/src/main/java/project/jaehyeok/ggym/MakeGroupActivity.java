package project.jaehyeok.ggym;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// 그룹 개설
public class MakeGroupActivity extends AppCompatActivity {
    private Button buttonToMyPage;
    private EditText inputGroupName;
    private EditText inputGroupPhone;
    private TextView inputGroupAddress;
    private Switch switchOpen;
    private TextView textSwitchOpen;

    private boolean saveInputText = false;

    private String userId;

    private Button searchAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group);

        buttonToMyPage = findViewById(R.id.toMyPage);
        inputGroupName = findViewById(R.id.inputGroupName);
        inputGroupPhone = findViewById(R.id.inputGroupPhone);
        inputGroupAddress = findViewById(R.id.inputGroupAddress);
        switchOpen = findViewById(R.id.switchOpen);
        textSwitchOpen = findViewById(R.id.textSwichOpen);

        searchAddress = findViewById(R.id.searchAddress);

        Intent getIntent = getIntent();
        String userDataSting = getIntent.getStringExtra("userData");
        try {
            JSONObject userDataJson = new JSONObject(userDataSting);
            userId = userDataJson.getString("userId");
        } catch (JSONException e) {
            e.printStackTrace();
        }



//        String URL = "https://dapi.kakao.com/";
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        RetrofitInterface server = retrofit.create(RetrofitInterface.class);
//        Call<String> call = server.searchAddressJson("종로");
//        System.out.println("#######쿼리보냄");
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                System.out.println("#######접근");
//                Log.d("##","접근");
//
//                if (response.isSuccessful()) {
//                    String dummies = response.body();
//                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@" + dummies);
//                } else {
//                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@" + " 실패?");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//            }
//        });
    }

//    Callback dummies = new Callback<String>() {
//
//        @Override
//        public void onResponse(Call<String> call, Response<String> response) {
//            System.out.println("## 날린 이후에 dummies 접근");
//
//            if (response.isSuccessful()) {
//                String dummies = response.body();
//                System.out.println("@@@@@@@@@@@@@@@@@@@@@@" + dummies);
//            } else {
//                System.out.println("@@@@@@@@@@@@@@@@@@@@@@" + " 실패?");
//            }
//        }
//
//        @Override
//        public void onFailure(Call<String> call, Throwable t) {
//
//        }
//    };


    @Override
    protected void onResume() {
        super.onResume();
        /*
        생성한 그룹을 이전 액티비티의 리스트뷰에 추가하기 위해서
        유저가 입력한 그룹 정보를 GroupData 객체로 만들어 intent 를 통해 전달한다.
         */
        buttonToMyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                GroupData groupData;

                String groupName;
                String groupPhone;
                String groupAddress;
                Boolean groupOpen = switchOpen.isChecked();

                /*
                EditText 의 내용은 GroupData 객체의 매게변수로 사용되어야 하기 때문에
                그룹 생성 EditText 의 내용을 입력할 수 있도록 안내
                 */
                if (inputGroupName.getText().toString().length() > 0) {
                    groupName = inputGroupName.getText().toString();

                    if (inputGroupPhone.getText().toString().length() > 0) {
                        groupPhone = inputGroupPhone.getText().toString();

                        if (inputGroupAddress.getText().toString().length() > 0) {
                            groupAddress = inputGroupAddress.getText().toString();

                            // EditText 모든 내용 입력시 GroupData 객체생성, 인텐트를 통해서 전달한다.
                            groupData = new GroupData(userId, groupName, groupPhone, groupAddress, groupOpen);
                            intent.putExtra("GroupData", groupData);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(MakeGroupActivity.this, "주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MakeGroupActivity.this, "전화번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MakeGroupActivity.this, "그룹명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (switchOpen.isChecked()) {
                    textSwitchOpen.setText("공개");
                } else {
                    textSwitchOpen.setText("비공개");
                }
            }
        });


        // 앱을 새로 실행할때(onCreate), 화면 전환후 다시 실행될때 (onRestart)
        // 두 경우 모두에서 저장된 데이터를 가져올 수 있도록 하기 위해 onResume 에서 구현
        SharedPreferences saveMakeGroup = getSharedPreferences("saveMakeGroup",MODE_PRIVATE);

        if (saveMakeGroup != null) {
            Set<String> groupData = saveMakeGroup.getStringSet("groupData",null);
            Iterator<String> iterator;

            String name = "";
            String phone = "";
            String address = "";
            boolean groupOpen = saveMakeGroup.getBoolean("groupOpen",false);

            if (groupData != null) {
                 iterator = groupData.iterator();
                // 식별을 위해 추가한 String 을 통해서 데이터를 분류
                while (iterator.hasNext()) {
                    String getGroupData = iterator.next();
                    int index = getGroupData.indexOf(":");

                    if (getGroupData.contains("name:")){
                        name = getGroupData.substring(index+1);
                    } else if (getGroupData.contains("phone:")) {
                        phone = getGroupData.substring(index+1);
                    } else if (getGroupData.contains("address:")) {
                        address = getGroupData.substring(index+1);
                    }
                }

                inputGroupName.setText(name);
                inputGroupPhone.setText(phone);
                //inputGroupAddress.setText(address);
                switchOpen.setChecked(groupOpen);
            } else {
                // 삭제로 인해 Set<String> groupData 가 null 일 경우에는 모든 값을 초기화
                inputGroupName.setText(name);
                inputGroupPhone.setText(phone);
                //inputGroupAddress.setText(address);
                switchOpen.setChecked(groupOpen);


            }
        }

        // 주소 검색 액티비티 이동
        searchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchAddressActivity.class);
                startActivityForResult(intent, 6666);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 주소검색 액티비티에서 전달받은 주소 받기
        if (resultCode == RESULT_OK && requestCode == 6666) {
            String mainAddress = data.getStringExtra("mainAddress");
            String addAddress = data.getStringExtra("addAddress");
            String totalAddress = mainAddress + "," + addAddress;

            inputGroupAddress.setText(totalAddress);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 유저가 입력한 복수의 데이터를 하나의 Set<String> 에 저장한다.
        // 이후 데이터를 꺼낼때 이름, 주소, 전화번호를 분류하기위해서 기존 데이터에
        // 식별하기 위한 String 추가 후 SharedPreference 에 저장한다
        if (saveInputText) {
            SharedPreferences saveMakeGroup = getSharedPreferences("saveMakeGroup", MODE_PRIVATE);
            SharedPreferences.Editor saveMyGroupEditor = saveMakeGroup.edit();

            Set<String> groupDataSet = new HashSet<>();

            String groupName = inputGroupName.getText().toString();
            String groupPhone = inputGroupPhone.getText().toString();
            String groupAddress = inputGroupAddress.getText().toString();
            boolean groupOpen = switchOpen.isChecked();

            String groupNameForSet = "name:" + groupName;
            String groupPhoneForSet = "phone:" + groupPhone;
            String groupAddressForSet = "address:" + groupAddress;

            groupDataSet.add(groupNameForSet);
            groupDataSet.add(groupPhoneForSet);
            groupDataSet.add(groupAddressForSet);

            saveMyGroupEditor.putStringSet("groupData", groupDataSet);
            saveMyGroupEditor.putBoolean("groupOpen", groupOpen);
            saveMyGroupEditor.commit();
        } else {
            // 유저가 저장하기를 선택하지 않았을때는 내용을 삭제한다
            SharedPreferences saveMakeGroup = getSharedPreferences("saveMakeGroup",MODE_PRIVATE);
            SharedPreferences.Editor editor = saveMakeGroup.edit();
            editor.clear();
            editor.commit();
            //Toast.makeText(this, "클리어했음", Toast.LENGTH_SHORT).show();
        }

        Log.d("그룹 저장하기", "onPause 로 옴");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        /* 백버튼을 통해서 다시 돌아올때 onStop > onRestart
        양식에 작성됐던 내용을 초기화 하기 위함
         */
        inputGroupName.setText(null);
        inputGroupPhone.setText(null);
        //inputGroupAddress.setText(null);
        switchOpen.setChecked(false);
    }

    @Override
    public void onBackPressed() {

        // 작성중인 내용의 SharedPreference 저장 여부를 확인하기 위해
        // 유저 선택에 따른 saveInputText 의 값 true, false 반환
        int groupNameLength = inputGroupName.getText().toString().length();
        int groupPhoneLength = inputGroupPhone.getText().toString().length();
        int groupAddressLength = inputGroupAddress.getText().toString().length();

        if (groupNameLength > 0 || groupPhoneLength > 0 || groupAddressLength > 0) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(MakeGroupActivity.this);
            dialog.setTitle("작성 중인 내용이 있습니다");
            dialog.setMessage("내용을 저장하시겠습니까?");

            dialog.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveInputText = true;
                    Log.d("그룹 저장하기", "저장하기 눌렀음");
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

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("그룹 저장하기", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("그룹 저장하기", "onDestroy");
    }
}