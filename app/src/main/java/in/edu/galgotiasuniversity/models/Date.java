package in.edu.galgotiasuniversity.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 25-01-2016.
 */
public class Date implements Parcelable {

    public static final Creator<Date> CREATOR = new Creator<Date>() {
        @Override
        public Date createFromParcel(Parcel in) {
            return new Date(in);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };
    private int day, month, year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    private Date(Parcel in) {
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
    }

    public String getDate() {
        return (Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year));
    }

    public void setDate(String date) {
        String split[] = date.split("/");
        this.day = Integer.valueOf(split[0]);
        this.month = Integer.valueOf(split[1]) - 1;
        this.year = Integer.valueOf(split[2]);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(day);
        dest.writeInt(month);
        dest.writeInt(year);
    }
}