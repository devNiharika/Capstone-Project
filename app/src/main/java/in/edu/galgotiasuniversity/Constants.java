package in.edu.galgotiasuniversity;

import org.jsoup.Connection;

/**
 * Created on 25-01-2016.
 */
public class Constants {

    public static int TIMEOUT = 30000;
    public static Connection.Response ATTENDANCE;

    //Common
    public static String USER_AGENT_IPHONE = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
    public static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
    public static String[] HEADER1 = {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"};
    public static String[] HEADER2 = {"Accept-Encoding", "gzip, deflate"};
    public static String[] HEADER3 = {"Accept-Language", "en-US,en;q=0.8"};
    public static String UPDATES_URL = "http://galgotias.ga/UpdatesGU";
    public static int MAX_DIFFERENCE = 29;
    public static String SEM_START_DATE = "26/07/2016";

    //GU
    public static String LOGIN_URL = "http://182.71.87.38/iSIM/Login";
    public static String HOME_URL = "http://182.71.87.38/iSIM/Home";
    public static String INFO_URL = "http://182.71.87.38/iSIM/Student/Course";
    public static String REFERRER = "http://182.71.87.38/iSIM/Login";
    public static String[] HEADER4 = {"Host", "182.71.87.38"};
    public static String LIBRARY_URL = "http://182.71.87.38/iSIM/Library/LibIssueReturn";
    public static String ATTENDANCE_URL = "http://182.71.87.38/iSIM/Student/TodayAttendence";
    public static String CAPTCHA_URL = "http://182.71.87.38/iSIM/Student/capimage";
    static boolean isCaptchaRequired = true;
}