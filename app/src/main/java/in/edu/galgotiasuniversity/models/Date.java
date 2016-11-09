package in.edu.galgotiasuniversity.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

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

    public Date() {
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DATE);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

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
        return (String.format(Locale.ENGLISH, "%02d", day) + "/" + String.format(Locale.ENGLISH, "%02d", month + 1) + "/" + String.format(Locale.ENGLISH, "%04d", year));
    }

    public void setDate(String date, DateFormat dateFormat) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(dateFormat.parse(date));
        day = c.get(Calendar.DATE);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public long getNumericDate() {
        return Long.parseLong(String.format(Locale.ENGLISH, "%04d", year) + String.format(Locale.ENGLISH, "%02d", month + 1) + String.format(Locale.ENGLISH, "%02d", day));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(day);
        parcel.writeInt(month);
        parcel.writeInt(year);
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
}