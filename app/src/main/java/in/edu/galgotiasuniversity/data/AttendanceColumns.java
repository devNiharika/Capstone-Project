//package in.edu.galgotiasuniversity.data;
//
//import net.simonvt.schematic.annotation.AutoIncrement;
//import net.simonvt.schematic.annotation.DataType;
//import net.simonvt.schematic.annotation.NotNull;
//import net.simonvt.schematic.annotation.PrimaryKey;
//import net.simonvt.schematic.annotation.Unique;
//
///**
// * Created on 25-01-2016.
// */
//public interface AttendanceColumns {
//    @DataType(DataType.Type.INTEGER)
//    @PrimaryKey
//    @AutoIncrement
//    String _ID = "_id";
//
//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    String SEMESTER = "semester";
//
//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    String DATE = "date";
//
//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    String SUBJECT_NAME = "subject_name";
//
//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    String TIME_SLOT = "time_slot";
//
//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    String ATTENDANCE_TYPE = "attendance_type";
//
//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    String STATUS = "status";
//
//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    @Unique
//    String KEY = "key";
//}
