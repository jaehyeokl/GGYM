package project.jaehyeok.ggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

// 로그인
public class LoginActivity extends AppCompatActivity {
    private Button buttonSignIn;
    private Button buttonLogin;
    private TextView inputId;
    private TextView inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSignIn = findViewById(R.id.buttonSingIn);
        buttonLogin = findViewById(R.id.buttonLogin);
        inputId = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        Log.d("로그인","onCreate");

        // 유저 데이터 삭제
//        SharedPreferences deleteData = getSharedPreferences("userData",MODE_PRIVATE);
//        SharedPreferences.Editor deleteDataEditor = deleteData.edit();
//        deleteDataEditor.remove("user11").commit();

        // 그룹 데이터 확인
        SharedPreferences groupData = getSharedPreferences("saveGroupData",MODE_PRIVATE);
        String groupDataString = groupData.getString("myGroupListJson",null);
        System.out.println("##" + groupDataString);

        //카카오API에 등록하기 위한 해시키 생성
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("project.jaehyeok.ggym", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 로그인 완료로 메뉴액티비티가 시작되면 다시 로그인페이지로 돌아갈 수 없도록
        if (requestCode == 1000) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 로그인 버튼 눌렀을때
                String checkId = inputId.getText().toString();
                String checkPassword = inputPassword.getText().toString();

                if (checkId.length() > 0 && checkPassword.length() > 0) {
                    SharedPreferences checkUserData = getSharedPreferences("userData",MODE_PRIVATE);
                    String userData = checkUserData.getString(checkId,null);
                    String userPassword = null;

                    try {
                        if (userData != null) {
                            JSONObject userDataJson = new JSONObject(userData);
                            userPassword = userDataJson.getString("userPassword");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    boolean subscribedId = checkUserData.contains(checkId);

                    if (subscribedId) {
                        // 가입된 계정일때
                        if (userPassword != null) {

                            if (checkPassword.equals(userPassword)) {
                                //유저 Id, Password 일치할때
                                //Toast.makeText(LoginActivity.this, "정상로그인", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("userData",userData);
                                startActivityForResult(intent, 1000);

                            } else {
                                Toast.makeText(LoginActivity.this, "비밀번호를 잘못 입력하였습니다", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "존재하지 않는 Id 입니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

        Log.d("로그인","onResume");
    }



    @Override
    protected void onPause() {
        super.onPause();

        Log.d("로그인","onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 로그인화면으로 돌아왔을때 기존 입력된 텍스트 초기화
        inputId.setText(null);
        inputPassword.setText(null);

        Log.d("로그인","onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("로그인","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("로그인","onDestroy");
    }
}