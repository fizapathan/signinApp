package com.example.signinapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class AppUser implements Parcelable {
    private Uri profilePicture;
    private String userName;
    private String userMail;

    public AppUser() {

    }

    public AppUser(Uri profilePicture, String userName, String userMail) {
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.userMail = userMail;
    }

    protected AppUser(Parcel in) {
        profilePicture = in.readParcelable(Uri.class.getClassLoader());
        userName = in.readString();
        userMail = in.readString();
    }

    public static final Creator<AppUser> CREATOR = new Creator<AppUser>() {
        @Override
        public AppUser createFromParcel(Parcel in) {
            return new AppUser(in);
        }

        @Override
        public AppUser[] newArray(int size) {
            return new AppUser[size];
        }
    };

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Uri profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(profilePicture, flags);
        dest.writeString(userName);
        dest.writeString(userMail);
    }
}
