package project.jaehyeok.ggym;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// 내 정보
public class MyInformationActivity extends AppCompatActivity {
    private final int GET_GALLERY_IMAGE = 3;
    private final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageProfile;
    private TextView viewUserId;
    private TextView viewUserName;
    private TextView viewUserPhone;

    private Button buttonLogout;
    private Button buttonChangePassword;
    private Button buttonChangeName;
    private Button buttonChangePhone;

    private String userDataString;
    private String userId;

    // 카메라 촬영
    private String takePhotoPath;
    final static int REQUEST_TAKE_PHOTO = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        imageProfile = findViewById(R.id.imageProfile);
        viewUserId = findViewById(R.id.viewUserId);
        viewUserName = findViewById(R.id.viewUserName);
        viewUserPhone = findViewById(R.id.viewUserPhone);

        buttonLogout = findViewById(R.id.buttonLogout);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonChangeName = findViewById(R.id.buttonChangeName);
        buttonChangePhone = findViewById(R.id.buttonChangePhone);

        // 인텐트로 userData 전달받아 초기화
        Intent getIntent = getIntent();
        userDataString = getIntent.getStringExtra("userData");
        try {
            JSONObject userDataJson = new JSONObject(userDataString);
            userId = userDataJson.getString("userId");

            // 인텐트로 전달받은 유저 정보 데이터는 수정된 내용을 반영하지 못한 상태이기 때문에
            // SharedPreferences 파일에 저장된 유저 정보를 레이아웃에 초기화한다
            SharedPreferences getUserData = getSharedPreferences("userData", MODE_PRIVATE);
            String newDataString = getUserData.getString(userId, null);

            JSONObject newDataJson = new JSONObject(newDataString);
            viewUserId.setText(newDataJson.getString("userId"));
            viewUserName.setText(newDataJson.getString("userName"));
            viewUserPhone.setText(newDataJson.getString("userPhone"));
            // 유저 프로필 이미지
            String profileImageString = newDataJson.getString("userProfileImage");
            BitmapAndString bitmapAndString = new BitmapAndString();
            Bitmap bitmap = bitmapAndString.stringToBitmap(profileImageString);
            imageProfile.setImageBitmap(bitmap);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 로그아웃
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                // 기존 백스택에 있는 액티비티들을 삭제하는 intent FLAG 설정
                // 로그아웃으로 LoginActivity 로 돌아갔을때,
                // 백버튼을 통해 로그인을 해야 볼 수 있는 액티비티들로 돌아갈 수 없도록 한다.
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 암호변경
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder changePasswordDialog = new AlertDialog.Builder(MyInformationActivity.this);
                final EditText inputNewPassword = new EditText(MyInformationActivity.this);
                //EditText checkNewPassword = new EditText(MyInformationActivity.this);

                inputNewPassword.setGravity(Gravity.CENTER);
                // 암호변경을 위해 새로운 암호를 입력하는 EditText 에서 패스워드가 '**' 형태로 보여지도록 한다
                inputNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                changePasswordDialog.setTitle("비밀번호 변경");
                changePasswordDialog.setMessage("새로운 비밀번호를 입력해주세요");
                changePasswordDialog.setView(inputNewPassword);
                //changePasswordDialog.setView(checkNewPassword);

                changePasswordDialog.setNeutralButton("취소", null);

                changePasswordDialog.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 조건에 따른 다이얼로그 창 유지여부를 세부적으로 구현하기 위해서
                        // 아래에 AlertDialog 에서 클릭이벤트를 구현하였음
                    }
                });

                // 비밀번호 변경 조건이 맞지 않을경우에는 다이얼로그의 확인 버튼을 눌러도
                // 창이 사라지지 않도록 구현한다. 이를 위해서는 AlertDialog.Builder 가 아닌
                // AlertDialog 에서 세부적으로 조건에 따른 다이얼로그 종료 여부를 설정해준다
                final AlertDialog dialog = changePasswordDialog.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 현재 액티비티에서는 암호를 텍스트뷰에서 보여주지 않는다.
                        // 암호를 변경하는 즉시 SharedPreferences 데이터에 저장한다
                        try {
                            JSONObject userDataJson = new JSONObject(userDataString);
                            String userPassword = userDataJson.getString("userPassword");
                            String newPassword = inputNewPassword.getText().toString();

                            String userId = userDataJson.getString("userId");

                            if (!userPassword.equals(newPassword)) {
                                // 새 암호로 덮어쓰기
                                userDataJson.put("userPassword", newPassword);
                                //System.out.println(userDataJson);

                                SharedPreferences saveNewUserData = getSharedPreferences("userData", MODE_PRIVATE);
                                SharedPreferences.Editor editorNewUserData = saveNewUserData.edit();
                                // 수정된 유저 정보(JSON)을 SharedPreferences 에 덮어쓰기
                                editorNewUserData.putString(userId, userDataJson.toString());
                                editorNewUserData.commit();

                                Toast.makeText(MyInformationActivity.this, "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();
                                // 다이얼로그 종료
                                dialog.dismiss();
                            } else {
                                // 다이얼로그 종료되지 않도록
                                Toast.makeText(MyInformationActivity.this, "현재 비밀번호와 일치합니다", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // 닉네임 변경
        buttonChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder changeNameDialog = new AlertDialog.Builder(MyInformationActivity.this);
                final EditText inputNewName = new EditText(MyInformationActivity.this);

                inputNewName.setGravity(Gravity.CENTER);

                changeNameDialog.setTitle("닉네임 변경");
                changeNameDialog.setMessage("새로운 닉네임을 입력해주세요");
                changeNameDialog.setView(inputNewName);

                changeNameDialog.setNeutralButton("취소", null);

                changeNameDialog.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                });
                final AlertDialog dialog = changeNameDialog.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            String newName = inputNewName.getText().toString();
                            viewUserName.setText(newName);

                            JSONObject userDataJson = new JSONObject(userDataString);
                            String userName = userDataJson.getString("userName");
                            String userId = userDataJson.getString("userId");

                            if (!userName.equals(newName)) {
                                // 새 닉네임 덮어쓰기
                                userDataJson.put("userName", newName);

                                SharedPreferences saveNewUserData = getSharedPreferences("userData", MODE_PRIVATE);
                                SharedPreferences.Editor editorNewUserData = saveNewUserData.edit();
                                // 수정된 유저 정보(JSON)을 SharedPreferences 에 덮어쓰기
                                editorNewUserData.putString(userId, userDataJson.toString());
                                editorNewUserData.commit();

                                Toast.makeText(MyInformationActivity.this, "닉네임 변경 완료", Toast.LENGTH_SHORT).show();
                                // 다이얼로그 종료
                                dialog.dismiss();
                            } else {
                                // 다이얼로그 종료되지 않도록
                                Toast.makeText(MyInformationActivity.this, "현재 닉네임과 일치합니다", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // 연락처 변경
        buttonChangePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder changePhoneDialog = new AlertDialog.Builder(MyInformationActivity.this);
                final EditText inputNewPhone = new EditText(MyInformationActivity.this);

                inputNewPhone.setGravity(Gravity.CENTER);

                changePhoneDialog.setTitle("연락처 변경");
                changePhoneDialog.setMessage("새로운 연락처를 입력해주세요");
                changePhoneDialog.setView(inputNewPhone);

                changePhoneDialog.setNegativeButton("취소", null);

                changePhoneDialog.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String newPhone = inputNewPhone.getText().toString();
                        viewUserPhone.setText(newPhone);
                    }
                });
                final AlertDialog dialog = changePhoneDialog.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            String newPhone = inputNewPhone.getText().toString();
                            viewUserPhone.setText(newPhone);

                            JSONObject userDataJson = new JSONObject(userDataString);
                            String userPhone = userDataJson.getString("userPhone");
                            String userId = userDataJson.getString("userId");

                            if (!userPhone.equals(newPhone)) {
                                // 새 연락처 덮어쓰기
                                userDataJson.put("userPhone", newPhone);

                                SharedPreferences saveNewUserData = getSharedPreferences("userData", MODE_PRIVATE);
                                SharedPreferences.Editor editorNewUserData = saveNewUserData.edit();
                                // 수정된 유저 정보(JSON)을 SharedPreferences 에 덮어쓰기
                                editorNewUserData.putString(userId, userDataJson.toString());
                                editorNewUserData.commit();

                                Toast.makeText(MyInformationActivity.this, "연락처 변경 완료", Toast.LENGTH_SHORT).show();
                                // 다이얼로그 종료
                                dialog.dismiss();
                            } else {
                                // 다이얼로그 종료되지 않도록
                                Toast.makeText(MyInformationActivity.this, "현재 연락처와 일치합니다", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // 프로필 사진 변경
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //takePhotoAction();
                        takePhoto();
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

                AlertDialog.Builder profileDialog = new AlertDialog.Builder(MyInformationActivity.this);
                profileDialog.setTitle("프로필사진 등록");
                profileDialog.setPositiveButton("사진촬영", cameraListener);
                profileDialog.setNegativeButton("앨범", albumListener);
                profileDialog.setNeutralButton("취소", cancelListener);

                profileDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data.hasExtra("data")) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//
//            if (bitmap != null) {
//                imageProfile.setImageBitmap(bitmap);
//            }
//        }

        // 앨범에서 가져왔을때
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

                imageProfile.setImageBitmap(img);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 카메라로 촬영한 사진 미리보기 가져오기
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK && data.hasExtra("data")) {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    imageProfile.setImageBitmap(bitmap);
                }
            }
        }

        // FileProvider 을 활용하여 사진 원본파일에서 이미지 가져오기
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            File file = new File(takePhotoPath);
            // 원본사진 그대로 사용할때
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));

            // 원본사진의 사이즈(용량) 크기때문에 SharedPreferences 에 (Uri)저장하고 불러오는 과정에서 오류 가능성이 있음
            // Error [FAILED BINDER TRANSACTION]
            // 이를 해결하기 위해서 사진이 들어갈 ImageView 의 크기에 맞추어 원본사진을 축소하여 가져온다

            // 이미지를 적용할 ImageView 가로/세로 크기
            int targetWidth = imageProfile.getWidth();
            int targetHeight = imageProfile.getHeight();

            // 원본 사진 가로/세로 크기
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            int photoWidth = bitmapOptions.outWidth;
            int photoHeight = bitmapOptions.outHeight;

            // 가로/세로 크기를 이용하여 축소 비율 구하기
            int scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);

            // 축소된 비율의 사진을 bitmap 으로 저장하기
            bitmapOptions.inJustDecodeBounds = false;
            bitmapOptions.inSampleSize = scaleFactor;
            //bitmapOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(takePhotoPath, bitmapOptions);

            if (bitmap != null) {
                imageProfile.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 현재 프로필 이미지를 SharedPreferences 에 저장하기 위해서
        // Bitmap 으로 불러와 String 으로 변환하여 저장한다
        BitmapDrawable drawable = (BitmapDrawable) imageProfile.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        BitmapAndString bitmapAndString = new BitmapAndString();
        String bitmapString = bitmapAndString.bitmapToString(bitmap);
        //System.out.println(bitmapString);

        try {
            JSONObject userDataJson = new JSONObject(userDataString);
            userId = userDataJson.getString("userId");

            SharedPreferences saveProfileImage = getSharedPreferences("userData", MODE_PRIVATE);
            SharedPreferences.Editor editorProfileImage = saveProfileImage.edit();

            String getDataString = saveProfileImage.getString(userId, null);
            JSONObject getDataJson = new JSONObject(getDataString);
            getDataJson.put("userProfileImage", bitmapString);

            editorProfileImage.putString(userId, getDataJson.toString());
            editorProfileImage.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 앨범에서 가져오기
    public void takeAlbumAction() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }

    // 카메라 촬영 (미리보기만 가져올 수 있음)
    public void takePhotoAction() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    // 카메라 촬영 (원본데이터를 저장할 수 있도록)
    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 사진이 저장될 파일을 초기화한다
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 파일이 정상적으로 만들어 졌을때 진행
        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, "project.jaehyeok.ggym.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }


    // 사진을 고유한 파일 이름으로 만드는 메소드
    // 사진들이 서로 충돌하지 않기 위해서, 또 나중에 사용할 수 있도록 하기 위해서
    public File createImageFile() throws IOException {
        // 파일 이름 만들기
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // ACTION_VIEW 인텐트가 사용할 경로에 파일을 저장
        takePhotoPath = image.getAbsolutePath();
        return image;
    }

//    public void checkCameraPermission() {
//
//        // 카메라 권한 요청
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            // 카메라 권한이 없을때
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                // 1. 사용자가 이전에 거절을 했던경우
//                Toast.makeText(this, "[설정]에서 카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show();
//            } else {
//                // 2. 사용자가 승인 거절과 동시에 다시 표시하지 않기를 선택한 경우 - 이경우 구현하지 않음
//                // 3. 또는 승인요청 한적 없는 최초실행일때
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},0);
//            }
//        } else {
//            // 4. 카메라 권한 승인됐을때
//        }


//        // 마시멜로 버전 이상일때 런타임중 권한이 필요할때 요청하기 때문에
//        if (Build.VERSION.SDK_INT >= 23) {
//            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            ArrayList<String> arrayPermission = new ArrayList<>();
//
//            // 외부 저장소 쓰기 권한 확인
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                // 다시 보기 접근 거절 상태일때
//                // 다시 보기 거절한 이력 있는지 없는지 확인 true/false(보지안음)
//                boolean checkShowPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//                // 다시보기 확인 후 사용자에게 직접 설정해야할 필요성이 있음을 알려준다
//                if (checkShowPermission) {
//                    // true 일때 -> 이전에 권한 요청 거절을 했었을때
//                    // 다시 요청메세지
//                    Toast.makeText(this, "[설정]에서 앨범 접근에 대한 권한을 허용해주세요", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    // false 일때 -> 거절한 이력이 없는 최초일때
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                }
//                // WRITE_EXTERNAL_STORAGE 접근 허락 된 상태일때
//                //arrayPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            } else {
//
//            }
//
//
//            // WRITE_EXTERNAL_STORAGE 에 READ_EXTERNAL_STORAGE 포함되어있다
////            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
////            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
////                arrayPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
////            }
//
//            if (arrayPermission.size() > 0) {
//                String strArray[] = new String[arrayPermission.size()];
//                strArray = arrayPermission.toArray(strArray);
//                // 권한 허용을 묻는다
//                // ActivityCompat.requestPermissions(this, strArray, PERMISSION_WRITE_CODE);
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 카메라 권한 요청받았을때
//        if (requestCode == 0) {
//            // 권한이 승인됐을때
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                takePhotoAction();
//            } else {
//                Toast.makeText(this, "권한이 없어 해당기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
//            }
//        }
    }
}