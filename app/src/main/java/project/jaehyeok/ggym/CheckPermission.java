package project.jaehyeok.ggym;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CheckPermission {
    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    Context context;
    Activity activity;

    public CheckPermission(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

//    public void main(String args[]) {
//        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
//                    (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA))) {
//                new AlertDialog.Builder(context)
//                        .setTitle("알림")
//                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
//                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
//                                activity.startActivity(intent);
//                            }
//                        })
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                activity.finish();
//                            }
//                        })
//                        .setCancelable(false)
//                        .create()
//                        .show();
//
//            } else {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
//
//            }
//        }
//    }

    public void permissionCamera() {
        int cameraCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

        // 카메가 권한이 없을때
        if (cameraCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }

        //activity.onRequestPermissionsResult();
    }

}
