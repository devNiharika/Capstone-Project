package in.edu.galgotiasuniversity.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created on 25-01-2016.
 */
public class AttendanceColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String SEMESTER = "semester";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String DATE = "date";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String SUBJECT_NAME = "subject_name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String TIME_SLOT = "time_slot";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String ATTENDANCE_TYPE = "attendance_type";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String STATUS = "status";
}
