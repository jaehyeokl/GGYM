package project.jaehyeok.ggym;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class GroupScheduleAdapter extends RecyclerView.Adapter<GroupScheduleAdapter.ViewHolder> {

    private ArrayList<ClassSchedule> data;
    private String userId = null;
    private String groupName = null;

    public GroupScheduleAdapter(ArrayList<ClassSchedule> data) {
        this.data = data;
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener = null;

    // 액티비티에서 아이템에 대한 클릭이벤트를 참조하기 위한 메소드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // 뷰홀더 생성
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupScheduleDate;
        TextView groupScheduleDayOfWeek;
        TextView groupScheduleTime;
        TextView groupScheduleName;
        TextView groupScheduleReserve;
        TextView groupScheduleMax;
        TextView groupScheduleLeftTime;
        TextView groupScheduleAlarmTime;
        Switch groupScheduleAlarmSwitch;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            groupScheduleDate = itemView.findViewById(R.id.groupScheduleDate);
            groupScheduleDayOfWeek = itemView.findViewById(R.id.groupScheduleDayOfWeek);
            groupScheduleTime = itemView.findViewById(R.id.groupScheduleTime);
            groupScheduleName = itemView.findViewById(R.id.groupScheduleName);
            groupScheduleReserve = itemView.findViewById(R.id.groupScheduleReserve);
            groupScheduleMax = itemView.findViewById(R.id.groupScheduleMax);
            groupScheduleLeftTime = itemView.findViewById(R.id.groupScheduleLeftTime);
            groupScheduleAlarmTime = itemView.findViewById(R.id.groupScheduleAlarmTime);

            // 해당 위치에 리사이클러뷰 클릭메서드가 있는 이유
            // 리사이클러뷰는 아이템이 뷰홀더안에 있기때문에 클릭을 하기 위해서는
            // 뷰홀더가 만들어지는 이곳에서 setOnClickListener 사용한다
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 뷰홀더의 현재 포지션
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
//                        ClassSchedule scheduleList = data.get(position);
//                        Toast.makeText(view.getContext(), position + " OK", Toast.LENGTH_SHORT).show();

                        onItemClickListener.OnItemClick(view, position);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public GroupScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_group_schedule, parent, false);
        GroupScheduleAdapter.ViewHolder holder = new GroupScheduleAdapter.ViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final GroupScheduleAdapter.ViewHolder holder, final int position) {
        ClassSchedule schedule = data.get(position);
        int reserveYear = schedule.getYear();
        int reserveMonth = schedule.getMonth();
        int reserveDay = schedule.getDay();
        String reserveStartTime = schedule.getStartTime();
        String reserveEndTime = schedule.getEndTime();
        //String className = schedule.getClassName();
        //boolean checkAlarm = schedule.isCheckAlarm();

        String date = Integer.toString(reserveMonth) + "/" + Integer.toString(reserveDay);

        holder.groupScheduleDate.setText(date);
        holder.groupScheduleDayOfWeek.setText(schedule.getDayOfWeek());
        holder.groupScheduleTime.setText(reserveStartTime + " - " + reserveEndTime);
        holder.groupScheduleName.setText(schedule.getClassName());
        holder.groupScheduleReserve.setText(Integer.toString(schedule.getReserveMember()));
        holder.groupScheduleMax.setText(Integer.toString(schedule.getMaxMember()));

        // 일정으로 부터 남은 시간을 연산 한 값을 바로 아이템의 TextView 에 표기하도록 한다.
        // onBindViewHolder 내에서 남은 시간을 구하도록 작성한 이유
        // UI 스레드에서 남은 시간을 구한다면, 아이템에 표기되도록 데이터를 리사이클러뷰에 전달하는 과정이 필요하지만
        // adapter 내에서 연산함으로써 UI 스레드에서는 notifyDataSetChanged()일정한 간격으로 실행하는 스레드만 구현하면 된다.
        LocalDate nowDate = LocalDate.now();
        LocalDate reserveDate = LocalDate.of(reserveYear, reserveMonth, reserveDay);
        int leftDay = (int) ChronoUnit.DAYS.between(nowDate, reserveDate);

        if (leftDay == 7) {
            holder.groupScheduleLeftTime.setText("일주일 전");

        } else if (leftDay < 7 && leftDay > 1) {
            holder.groupScheduleLeftTime.setText(Integer.toString(leftDay) + "일 전");

        } else if (leftDay == 1) {
            holder.groupScheduleLeftTime.setText("내일");

        } else if (leftDay == 0) {

            LocalTime nowTime = LocalTime.now();
            LocalTime reserveTime = LocalTime.parse(reserveStartTime, DateTimeFormatter.ofPattern("HH:mm"));

            Duration duration = Duration.between(nowTime, reserveTime);
            long durationSeconds = duration.getSeconds();
            int durationMinute = (int) (duration.getSeconds() / 60L);
            int durationHour = (int) (duration.getSeconds() / 60L / 60L);

            if (durationSeconds <= 3600 * 5 && durationSeconds >= 3600 * 1) {
                holder.groupScheduleLeftTime.setText(durationHour + "시간 전");

            } else if (durationSeconds < 3600 && durationSeconds > 0) {
                holder.groupScheduleLeftTime.setText(durationMinute + "분 전");

            } else if (durationSeconds > 3600 * 6) {
                holder.groupScheduleLeftTime.setText("오늘");
            } else {
                holder.groupScheduleLeftTime.setText("종료");
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}


