package project.jaehyeok.ggym;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * ClassSchedule
 * 스케쥴의 데이터에 대한 정보를 정의
 * GroupData 의 scheduleList 저장하기 위해 사용한다
 */
public class ClassSchedule implements Parcelable {

    private String className = "";
    private String classMaster = "";
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private String date = "";
    private String dayOfWeek = "";
    private String startTime = "";
    private String endTime = "";
    private String runningTime = "";
    private int maxMember = 0;
    private int reserveMember = 0;
    //private boolean checkAlarm = false;

    public ClassSchedule(String className) {
        this.className = className;
//        this.classMaster = classMaster;
//        this.date = date;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.maxMember = maxMember;
    }

    // parcelable 보일러플레이트 코드
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected ClassSchedule(Parcel in) {
        className = in.readString();
        classMaster = in.readString();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        date = in.readString();
        dayOfWeek = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        runningTime = in.readString();
        maxMember = in.readInt();
        reserveMember = in.readInt();
        //checkAlarm = in.readBoolean();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(className);
        dest.writeString(classMaster);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeString(date);
        dest.writeString(dayOfWeek);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(runningTime);
        dest.writeInt(maxMember);
        dest.writeInt(reserveMember);
        //dest.writeBoolean(checkAlarm);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClassSchedule> CREATOR = new Creator<ClassSchedule>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public ClassSchedule createFromParcel(Parcel in) {
            return new ClassSchedule(in);
        }

        @Override
        public ClassSchedule[] newArray(int size) {
            return new ClassSchedule[size];
        }
    };

    // 날짜데이터를 통해서 요일을 계산 후 저장
    public void setDayOfWeek() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String[] week = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        Calendar cal = Calendar.getInstance();
        Date getDate;
        String startMonthZero = "";
        String startDayZero = "";
        // 월,일 10보다 작을때 yyyyMMdd 포맷 형식에 맞게 변환
        if (month < 10) {
            startMonthZero = "0";
        }
        if (day < 10) {
            startDayZero = "0";
        }

        date = Integer.toString(year) + startMonthZero + Integer.toString(month) + startDayZero + Integer.toString(day);

        try {
            getDate = format.parse(date);
            cal.setTime(getDate);
            int position = cal.get(Calendar.DAY_OF_WEEK) - 1;
            this.dayOfWeek = week[position];
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //getter setter toString
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(String classMaster) {
        this.classMaster = classMaster;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

//    public void setDayOfWeek(String dayOfWeek) {
//        this.dayOfWeek = dayOfWeek;
//    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public int getReserveMember() {
        return reserveMember;
    }

    public void setReserveMember(int reserveMember) {
        this.reserveMember = reserveMember;
    }

//    public boolean isCheckAlarm() {
//        return checkAlarm;
//    }
//
//    public void setCheckAlarm(boolean checkAlarm) {
//        this.checkAlarm = checkAlarm;
//    }
}
