package project.jaehyeok.ggym;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private ArrayList<JSONObject> data = null;

    // 아이템의 클릭 상태를 저정
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item 의 position
    private int prePosition = -1;

    public GroupListAdapter(ArrayList<JSONObject> data) {
        this.data = data;
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    private GroupScheduleAdapter.OnItemClickListener onItemClickListener = null;

    // 액티비티에서 아이템에 대한 클릭이벤트를 참조하기 위한 메소드
    public void setOnItemClickListener(GroupScheduleAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // 뷰홀더
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView groupImage;
        TextView groupName;
        TextView groupAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupImage = itemView.findViewById(R.id.itemGroupImage);
            groupName = itemView.findViewById(R.id.itemGroupName);
            groupAddress = itemView.findViewById(R.id.itemGroupAddress);

            // 해당 위치에 리사이클러뷰 클릭메서드가 있는 이유
            // 리사이클러뷰는 아이템이 뷰홀더안에 있기때문에 클릭을 하기 위해서는
            // 뷰홀더가 만들어지는 이곳에서 setOnClickListener 사용한다
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 뷰홀더의 현재 포지션
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        onItemClickListener.OnItemClick(view, position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_group_list, parent, false);
        GroupListAdapter.ViewHolder holder = new GroupListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListAdapter.ViewHolder holder, int position) {

        JSONObject groupJson = data.get(position);

        try {
            holder.groupName.setText(groupJson.getString("groupName"));
            holder.groupAddress.setText(groupJson.getString("groupAddress"));

            BitmapAndString bitmapAndString = new BitmapAndString();
            Bitmap bitmap = bitmapAndString.stringToBitmap(groupJson.getString("groupProfileUrl"));
            holder.groupImage.setImageBitmap(bitmap);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
