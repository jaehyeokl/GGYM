package project.jaehyeok.ggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

// 회원가입 양식 작성
public class SignInActivity extends AppCompatActivity {
    private Button buttonComplete;
    private EditText inputNewId;
    private EditText inputNewPassword;
    private EditText inputNewPasswordAgain;
    private EditText inputNewName;
    private EditText inputNewPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        buttonComplete = findViewById(R.id.completeSignIn);
        inputNewId = findViewById(R.id.inputNewEmail);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputNewPasswordAgain = findViewById(R.id.inputNewPasswordAgain);
        inputNewName = findViewById(R.id.inputNewName);
        inputNewPhone = findViewById(R.id.inputNewPhone);

        Log.d("회원가입", "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원 가입 완료 버튼
//                Intent intent = new Intent(getApplicationContext(), CompletedSingInActivity.class);
//                startActivityForResult(intent, 2000);
//
//                CharSequence text = "회원가입을 완료했습니다!";
//                Toast toast = Toast.makeText(SignInActivity.this, text, Toast.LENGTH_SHORT);
//                toast.show();

                // 회원가입 데이터 저장
                UserData userData;

                String inputId = inputNewId.getText().toString();
                String inputPassword = inputNewPassword.getText().toString();
                String inputCheckPassword = inputNewPassword.getText().toString();
                String inputName = inputNewName.getText().toString();
                String inputPhone = inputNewPhone.getText().toString();

                if (inputId.length() > 0) {

                    if (inputPassword.length() > 0) {

                        if (inputCheckPassword.length() > 0 ) {

                            if (inputPassword.equals(inputCheckPassword)) {

                                if (inputName.length() > 0) {

                                    if (inputPhone.length() > 0) {
                                        userData = new UserData(inputId,inputPassword,inputName,inputPhone);
                                        Intent intent = new Intent(getApplicationContext(), CompletedSingInActivity.class);
                                        startActivity(intent);

                                        // JSONObject 만들기
                                        JSONObject userDataJson = new JSONObject();
                                        try {
                                            userDataJson.put("userId", inputId);
                                            userDataJson.put("userPassword",inputPassword);
                                            userDataJson.put("userName", inputName);
                                            userDataJson.put("userPhone", inputPhone);
                                            userDataJson.put("userProfileImage", userData.getUserProfileImage());
                                            userDataJson.put("userGroup", userData.getUserGroup());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        SharedPreferences saveUserData = getSharedPreferences("userData", MODE_PRIVATE);
                                        SharedPreferences.Editor saveUserDataEditor = saveUserData.edit();

                                        saveUserDataEditor.putString(inputId, userDataJson.toString());
                                        saveUserDataEditor.commit();
                                        //System.out.println("유저 JSON 확인" + userDataJson.toString());

                                    } else {
                                        Toast.makeText(SignInActivity.this, "휴대폰 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(SignInActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignInActivity.this, "비밀번호를 재입력 해주세요", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                }


            }
        });

        Log.d("회원가입", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("회원가입", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        inputNewId.setText(null);
        inputNewPassword.setText(null);
        inputNewPasswordAgain.setText(null);
        inputNewName.setText(null);
        inputNewPhone.setText(null);

        Log.d("회원가입", "onRestart");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //회원가입이 완료됐을때 액티비티를 종료하여 백버튼으로 다시 회원가입 페이지로 돌아오지 않도록
//        if (requestCode == 2000) {
//            finish();
//        }
        Log.d("회원가입", "onActivityResult");
    }


    @Override
    protected void onStop() {
        super.onStop();

        Log.d("회원가입", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("회원가입", "onDestroy");
    }
}