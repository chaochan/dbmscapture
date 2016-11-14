package chaochan.dbmscapture;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by  on 2016/11/12.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }

}
