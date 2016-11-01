//package in.edu.galgotiasuniversity.data;
//
///**
// * Created on 26-10-2016.
// */
//
//import android.content.ContentProviderOperation;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//
///**
// * Created by sam_chordas on 10/8/15.
// */
//public class DB_Utils {
//    private static String LOG_TAG = DB_Utils.class.getSimpleName();
//
//
//    public static ContentProviderOperation buildBatchOperation(JSONArray jsonArray) {
//
//        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
//                AttendanceProvider.Attendance.CONTENT_URI);
//        try {
//            builder.withValue(AttendanceColumns.SEMESTER, jsonArray.get(0));
//            builder.withValue(AttendanceColumns.DATE, jsonArray.get(1));
//            builder.withValue(AttendanceColumns.SUBJECT_NAME, jsonArray.get(2));
//            builder.withValue(AttendanceColumns.TIME_SLOT, jsonArray.get(3));
//            builder.withValue(AttendanceColumns.ATTENDANCE_TYPE, jsonArray.get(4));
//            builder.withValue(AttendanceColumns.STATUS, jsonArray.get(5));
//            builder.withValue(AttendanceColumns.KEY, jsonArray.get(6));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return builder.build();
//    }
//}
