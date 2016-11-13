package in.edu.galgotiasuniversity.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Rohan Garg on 15-06-2016.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetViewsFactory(getApplicationContext(), intent));
    }
}