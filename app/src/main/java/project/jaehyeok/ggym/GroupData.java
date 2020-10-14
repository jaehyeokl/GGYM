package project.jaehyeok.ggym;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.util.ArrayList;


/*
GroupData Class
프로젝트에서 다루는 모든 그룹에 대한 정보를 저장하기 위함
Intent 로 객체를 전달할 수 있도록 implements Parcelable
 */
public class GroupData implements Parcelable {

    private String masterUserId;
    private String groupName;
    private String groupPhone;
    private String groupAddress;
    private boolean groupOpen;
    private int groupMember = 0;
    private String groupExplain;
    private String groupProfileUrl;

    private JSONArray groupMemberList;
    private JSONArray groupScheduleList;
    private ArrayList<ClassSchedule> scheduleList = null;
    //private JSONArray groupReview;

    public GroupData(String masterUserId, String groupName, String groupPhone, String groupAddress, boolean groupOpen) {
        this.masterUserId = masterUserId;
        this.groupName = groupName;
        this.groupPhone = groupPhone;
        this.groupAddress = groupAddress;
        this.groupOpen = groupOpen;
//        this.groupMember = groupMember;
    }

    /*
    Parcelable 보일러플레이트 코드
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(masterUserId);
        parcel.writeString(groupName);
        parcel.writeString(groupPhone);
        parcel.writeString(groupAddress);
        parcel.writeByte((byte) (groupOpen ? 1 : 0));
        parcel.writeInt(groupMember);
        parcel.writeString(groupExplain);
        parcel.writeString(groupProfileUrl);
        parcel.writeTypedList(scheduleList);
    }

    protected GroupData(Parcel in) {
        masterUserId = in.readString();
        groupName = in.readString();
        groupPhone = in.readString();
        groupAddress = in.readString();
        groupOpen = in.readByte() != 0;
        groupMember = in.readInt();
        groupExplain = in.readString();
        groupProfileUrl = in.readString();
        // 여기서 ArrayList 객체를 인스턴스화
        scheduleList = new ArrayList<ClassSchedule>();
        in.readTypedList(scheduleList, ClassSchedule.CREATOR);
    }

    public static final Creator<GroupData> CREATOR = new Creator<GroupData>() {
        @Override
        public GroupData createFromParcel(Parcel in) {
            return new GroupData(in);
        }

        @Override
        public GroupData[] newArray(int size) {
            return new GroupData[size];
        }
    };


    //getter, setter, toString
    public String getMasterUserId() {
        return masterUserId;
    }

    public void setMasterUserId(String masterUserId) {
        this.masterUserId = masterUserId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPhone() {
        return groupPhone;
    }

    public void setGroupPhone(String groupPhone) {
        this.groupPhone = groupPhone;
    }

    public String getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(String groupAddress) {
        this.groupAddress = groupAddress;
    }

    public int getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(int groupMember) {
        this.groupMember = groupMember;
    }

    public boolean getGroupOpen() {
        return groupOpen;
    }

    public void setGroupOpen(boolean groupOpen) {
        this.groupOpen = groupOpen;
    }

    public String getGroupProfileUrl() {
        return groupProfileUrl;
    }

    public void setGroupProfileUrl(String groupProfileUrl) {
        this.groupProfileUrl = groupProfileUrl;
    }

    public JSONArray getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(JSONArray groupMemberList) {
        this.groupMemberList = groupMemberList;
    }

    public JSONArray getGroupScheduleList() {
        return groupScheduleList;
    }

    public void setGroupScheduleList(JSONArray groupScheduleList) {
        this.groupScheduleList = groupScheduleList;
    }

    public ArrayList<ClassSchedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(ArrayList<ClassSchedule> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
