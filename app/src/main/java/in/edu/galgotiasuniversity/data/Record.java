package in.edu.galgotiasuniversity.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import in.edu.galgotiasuniversity.models.Date;

/**
 * Created on 25-01-2016.
 */

@Table(name = "Records")
public class Record extends Model {

    @Column
    public String SEMESTER;
    @Column
    public long DATE;
    @Column
    public String SUBJECT_NAME;
    @Column
    public String TIME_SLOT;
    @Column
    public String ATTENDANCE_TYPE;
    @Column
    public String STATUS;
    @Column
    public int MM;
    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String KEY;

    public Record() {
    }

    public static List<Record> getAttendance(Date FROM_DATE, Date TO_DATE) {
        return new Select()
                .from(Record.class)
                .where("DATE >= ?", FROM_DATE.getNumericDate())
                .and("DATE <= ?", TO_DATE.getNumericDate())
                .execute();
    }

}
