//package in.edu.galgotiasuniversity.models;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.revmob.ads.banner.RevMobBanner;
//import com.revmob.ads.interstitial.RevMobFullscreen;
//
///**
// * Created by Rohan Garg on 13-03-2016.
// */
//public class Ads implements Parcelable {
//
//    public static final Creator<Ads> CREATOR = new Creator<Ads>() {
//        @Override
//        public Ads createFromParcel(Parcel in) {
//            return new Ads(in);
//        }
//
//        @Override
//        public Ads[] newArray(int size) {
//            return new Ads[size];
//        }
//    };
//    public RevMobBanner revMobBanner;
//    public RevMobFullscreen revMobFullscreen;
//
//    public Ads(RevMobBanner revMobBanner, RevMobFullscreen revMobFullscreen) {
//
//        this.revMobBanner = revMobBanner;
//        this.revMobFullscreen = revMobFullscreen;
//    }
//
//    protected Ads(Parcel in) {
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//    }
//}
