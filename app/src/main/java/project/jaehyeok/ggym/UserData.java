package project.jaehyeok.ggym;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

public class UserData implements Parcelable {

    private String userId;
    private String userPassword;
    private String userName;
    private String userPhone;

    private String userProfileImage;
    private JSONArray userGroup;

    public UserData(String userId, String userPassword, String userName, String userPhone) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userPhone = userPhone;
    }

    protected UserData(Parcel in) {
        userId = in.readString();
        userPassword = in.readString();
        userName = in.readString();
        userPhone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userPassword);
        dest.writeString(userName);
        dest.writeString(userPhone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public JSONArray getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(JSONArray userGroup) {
        this.userGroup = userGroup;
    }
}
