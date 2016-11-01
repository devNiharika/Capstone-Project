//package in.edu.galgotiasuniversity.data;
//
//import android.net.Uri;
//
//import net.simonvt.schematic.annotation.ContentProvider;
//import net.simonvt.schematic.annotation.ContentUri;
//import net.simonvt.schematic.annotation.TableEndpoint;
//
///**
// * Created on 25-01-2016.
// */
//@ContentProvider(authority = AttendanceProvider.AUTHORITY, database = AttendanceDatabase.class)
//public class AttendanceProvider {
//
//    public static final String AUTHORITY = "in.edu.galgotiasuniversity.data.AttendanceProvider";
//
//    interface Path {
//        String ATTENDANCE = "attendance";
//    }
//
//    @TableEndpoint(table = AttendanceDatabase.ATTENDANCE)
//    public static class Attendance {
//        @ContentUri(
//                path = Path.ATTENDANCE,
//                type = "vnd.android.cursor.dir/list"
//        )
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/attendance");
//    }
//}
