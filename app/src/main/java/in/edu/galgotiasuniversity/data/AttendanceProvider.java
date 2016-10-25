package in.edu.galgotiasuniversity.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created on 25-01-2016.
 */
@ContentProvider(authority = AttendanceProvider.AUTHORITY, database = AttendanceDatabase.class)
public class AttendanceProvider {
    public static final String AUTHORITY = "in.edu.galgotiasuniversity.data.AttendanceProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    interface Path {
        String ATTENDANCE = "attendance";
    }

    @TableEndpoint(table = AttendanceDatabase.ATTENDANCE)
    public static class Attendance {
        @ContentUri(
                path = Path.ATTENDANCE,
                type = "vnd.android.cursor.dir/attendance"
        )
        public static final Uri CONTENT_URI = buildUri(Path.ATTENDANCE);

        @InexactContentUri(
                name = "ATTENDANCE_ID",
                path = Path.ATTENDANCE + "/*",
                type = "vnd.android.cursor.item/attendance",
                whereColumn = AttendanceColumns.SUBJECT_NAME,
                pathSegment = 1
        )
        public static Uri withSymbol(String symbol) {
            return buildUri(Path.ATTENDANCE, symbol);
        }
    }
}
