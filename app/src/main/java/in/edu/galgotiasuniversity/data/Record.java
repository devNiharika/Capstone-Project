package in.edu.galgotiasuniversity.data;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import in.edu.galgotiasuniversity.models.Date;
import in.edu.galgotiasuniversity.models.Month;
import in.edu.galgotiasuniversity.models.Subject;

/**
 * Created on 25-01-2016.
 */

@Table(name = "Records", id = "_id")
public class Record extends TruncatableModel {

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
        super();
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

            System.out.println(new Select()
                    .from(Record.class)
                    .where("SUBJECT_NAME = ?", subject.NAME)
                    .and("STATUS = ?", "P").toSql());

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

    public static List<Month> getMonths() {
        List<Month> months = new ArrayList<>();
        List<Record> records = new Select()
                .distinct()
                .from(Record.class)
                .groupBy("MM")
                .orderBy("MM ASC")
                .execute();

        for (Record record : records) {
            Month month = new Month();
            month.NAME = new DateFormatSymbols().getMonths()[record.MM - 1];

            month.PRESENT = new Select()
                    .from(Record.class)
                    .where("MM = ?", record.MM)
                    .and("STATUS = ?", "P")
                    .execute().size();

            month.ABSENT = new Select()
                    .from(Record.class)
                    .where("MM = ?", record.MM)
                    .and("STATUS = ?", "A")
                    .execute().size();

            month.PERCENTAGE = (100f * month.PRESENT) / (month.PRESENT + month.ABSENT);
            months.add(month);
        }

        return months;
    }
}
