package project.jaehyeok.ggym;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyGroupAdapter extends BaseAdapter {
    private List<GroupData> data;
    private String userId;

    public MyGroupAdapter(List<GroupData> data, String userId) {
        this.data = data;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        // 아이템 레이아웃 인플레이트
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.item_my_group,viewGroup,false);

        ImageView myGroupImage = view.findViewById(R.id.myGroupImage);
        TextView myGroupName = view.findViewById(R.id.myGroupName);
        TextView myGroupMember = view.findViewById(R.id.myGroupMember);

        final GroupData groupData = data.get(i);
        String groupProfileUrl = groupData.getGroupProfileUrl();
        BitmapAndString bitmapAndString = new BitmapAndString();
        Bitmap bitmap = bitmapAndString.stringToBitmap(groupProfileUrl);

        myGroupImage.setImageBitmap(bitmap);
        myGroupName.setText(groupData.getGroupName());
        myGroupMember.setText(groupData.getGroupMember() + "명");

        // 리스트뷰 아이템에 버튼을 추가
        // 아이템에 setOnItemClick 이 작동하지 않는데 이를 해결하기 위해서는
        // 버튼의 xml 속성으로 android:focusable="false" 를 추가한다.
        Button buttonGroupInformation = (Button) view.findViewById(R.id.buttonGroupInfo);
        buttonGroupInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(viewGroup.getContext(), "일정", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(viewGroup.getContext(), GroupScheduleActivity.class);
                intent.putExtra("GroupData", groupData);
                intent.putExtra("GroupDataPosition", i);
                intent.putExtra("userId", userId);

                // 리스트뷰의 adaptor 에서 startActivityForResult 를 구현하기 위해서
                // requestCode 를 전달받아야 할 액티비티를 아래와 같이 작성해주어야한다
                // ((Activity) context).startActivityForResult
                ((MyPageActivity) viewGroup.getContext()).startActivityForResult(intent,3003);


            }
        });

        return view;
    }
}
