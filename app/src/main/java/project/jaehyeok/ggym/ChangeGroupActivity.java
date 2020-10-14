package project.jaehyeok.ggym;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

// 그룹 정보 수정 팝업 액티비티
public class ChangeGroupActivity extends Activity {

    private EditText viewGroupName;
    private EditText viewGroupPhone;
    private EditText viewGroupAddress;
    private Switch switchGroupOpen;
    private TextView viewGroupOpen;

    private Button buttonGetChange;
    private Button buttonCancelChange;

    GroupData getGroupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group);

        /*
        그룹 수정을 위해서 그룹정보와 동일한 GroupData 를 EditText 에 표시
        */
        viewGroupName = findViewById(R.id.viewChangeName);
        viewGroupPhone = findViewById(R.id.viewChangePhone);
        viewGroupAddress = findViewById(R.id.viewChangeAddress);
        switchGroupOpen = findViewById(R.id.switchChangeOpen);
        viewGroupOpen = findViewById(R.id.viewChangeOpen);

        buttonGetChange = findViewById(R.id.buttonGetChange);
        buttonCancelChange = findViewById(R.id.buttonCancelChange);

        Intent getIntent = getIntent();
        getGroupData = getIntent.getParcelableExtra("GroupData");
        viewGroupName.setText(getGroupData.getGroupName());
        viewGroupPhone.setText(getGroupData.getGroupPhone());
        viewGroupAddress.setText(getGroupData.getGroupAddress());
        switchGroupOpen.setChecked(getGroupData.getGroupOpen());
        if (getGroupData.getGroupOpen()) {
            // 그룹공개 일때
            viewGroupOpen.setText("공개");
        } else {
            viewGroupOpen.setText("비공개");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 스위치 클릭에 따른 공개/비공개 텍스트 변환
        switchGroupOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchGroupOpen.isChecked()) {
                    viewGroupOpen.setText("공개");
                } else {
                    viewGroupOpen.setText("비공개");
                }
            }
        });

        /*
        수정버튼을 눌렀을때
         */
        buttonGetChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                String groupName = viewGroupName.getText().toString();
                String groupPhone = viewGroupPhone.getText().toString();
                String groupAddress = viewGroupAddress.getText().toString();
                boolean groupOpen = switchGroupOpen.isChecked();

                /*
                수정된 GroupData 를 이전 액티비티로 전달한다.
                */
                getGroupData.setGroupOpen(groupOpen);
                if (groupName.length() > 0) {
                    getGroupData.setGroupName(groupName);

                    if (groupPhone.length() > 0) {
                        getGroupData.setGroupPhone(groupPhone);

                        if (groupAddress.length() > 0) {
                            getGroupData.setGroupAddress(groupAddress);

                            intent.putExtra("ChangeGroupData", getGroupData);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(ChangeGroupActivity.this, "주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChangeGroupActivity.this, "전화번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangeGroupActivity.this, "그룹명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 취소 눌렀을떄
        buttonCancelChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 영역 밖 터치로 인한 액티비티 종료 방지
        this.setFinishOnTouchOutside(false);
    }
}