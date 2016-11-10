package in.edu.galgotiasuniversity.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import in.edu.galgotiasuniversity.models.Date;
import in.edu.galgotiasuniversity.models.Subject;

/**
 * Created on 25-01-2016.
 */

@Table(name = "Records")
public class Record extends Model {

    @Column
    public String SEMESTER;
    @Column
    public long NUMERIC_DATE;
    @Column
    public String STRING_DATE;
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
                .where("NUMERIC_DATE >= ?", FROM_DATE.getNumericDate())
                .and("NUMERIC_DATE <= ?", TO_DATE.getNumericDate())
                .orderBy("NUMERIC_DATE")
                .execute();
    }

    public static List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<>();
        List<Record> records = new Select()
                .distinct()
                .from(Record.class)
                .groupBy("SUBJECT_NAME")
                .orderBy("SUBJECT_NAME ASC")
                .execute();
        for (Record record : records) {
            Subject subject = new Subject();
            subject.NAME = record.SUBJECT_NAME;

            subject.PRESENT = new Select()
                    .from(Record.class)
                    .where("SUBJECT_NAME = ?", subject.NAME)
                    .and("STATUS = ?", "P")
                    .execute().size();

            subject.ABSENT = new Select()
                    .from(Record.class)
                    .where("SUBJECT_NAME = ?", subject.NAME)
                    .and("STATUS = ?", "A")
                    .execute().size();

            subject.PERCENTAGE = (100f * subject.PRESENT) / (subject.PRESENT + subject.ABSENT);

            subject.ATTENDANCE_TYPE = record.ATTENDANCE_TYPE;
            subjects.add(subject);
        }
        return subjects;
    }


}
