package in.edu.galgotiasuniversity.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created on 25-01-2016.
 */
@Database(version = AttendanceDatabase.VERSION)
public class AttendanceDatabase {
    public static final int VERSION = 1;
    @Table(AttendanceColumns.class)
    public static final String ATTENDANCE = "attendance";

    private AttendanceDatabase() {
    }
}
