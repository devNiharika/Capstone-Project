package in.edu.galgotiasuniversity.data;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

public class Utils {

    @SuppressWarnings("unchecked")
    public static Map<String, String> readObjectFromMemory(Context context, String filename) {
        Object defaultObject = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            defaultObject = is.readObject();
            is.close();
        } catch (Exception e) {
            Log.d("READ_OBJECT", e.getMessage());
        }
        return (Map<String, String>) defaultObject;
    }
}
