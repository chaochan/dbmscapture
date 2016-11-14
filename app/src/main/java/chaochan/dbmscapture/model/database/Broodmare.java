package chaochan.dbmscapture.model.database;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 繁殖牝馬モデル
 */
@Table(database = AppDatabase.class)
public class Broodmare extends BaseModel {
    public static final int LEVEL_NORMAL    = 0;
    public static final int LEVEL_GOOD      = 1;
    public static final int LEVEL_VERY_GOOD = 2;
    public static final int LEVEL_EXCELLENT = 3;


    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public String name;
    @Column
    public int level;
    @Column
    public String sire_line;


    public void setSireLine(String[] list) {
        this.sire_line = TextUtils.join(",", list);
    }


    public String[] getSireLineArray() {
        return this.sire_line.split(",");
    }
}
