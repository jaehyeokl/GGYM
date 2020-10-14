package project.jaehyeok.ggym;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupObject {

    @SerializedName("scheduleList")
    private List<Schedule> scheduleList;
    @SerializedName("groupMemberList")
    private List<String> groupMemberList;
    private String masterUserId;
    private String groupName;
    private String groupPhone;
    private String groupAddress;
    private boolean groupOpen;
    private int groupMember;
    private String groupProfileUrl;



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

    public boolean isGroupOpen() {
        return groupOpen;
    }

    public void setGroupOpen(boolean groupOpen) {
        this.groupOpen = groupOpen;
    }

    public int getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(int groupMember) {
        this.groupMember = groupMember;
    }

    public String getGroupProfileUrl() {
        return groupProfileUrl;
    }

    public void setGroupProfileUrl(String groupProfileUrl) {
        this.groupProfileUrl = groupProfileUrl;
    }

//    public List<String> getGroupMemberList() {
//        return groupMemberList;
//    }
//
//    public void setGroupMemberList(List<String> groupMemberList) {
//        this.groupMemberList = groupMemberList;
//    }

    //    public JSONArray getScheduleList() {
//        return scheduleList;
//    }
//
//    public void setScheduleList(JSONArray scheduleList) {
//        this.scheduleList = scheduleList;
//    }


    @Override
    public String toString() {
        return "GroupObject{" +
                "masterUserId='" + masterUserId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupPhone='" + groupPhone + '\'' +
                ", groupAddress='" + groupAddress + '\'' +
                ", groupOpen=" + groupOpen +
                ", groupMember=" + groupMember +
                ", groupProfileUrl='" + groupProfileUrl + '\'' +
                '}';
    }
}

class Schedule {
    private String className;
    private String classManager;
    private int classYear;
    private int classMonth;
    private int classDay;
    private String classDayOfWeek;
    private String classStartTime;
    private String classEndTime;
    private int classMaxMember;
    private int classReserveMember;
}
