package chaochan.dbmscapture.model.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by  on 2016/11/12.
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, generatedClassSeparator = "_")
public class AppDatabase {
    public static final String NAME = "AppDatabase";
    public static final int VERSION = 1;
}
