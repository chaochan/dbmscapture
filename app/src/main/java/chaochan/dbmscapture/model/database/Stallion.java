package chaochan.dbmscapture.model.database;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 種牡馬モデル
 */
@Table(database = AppDatabase.class)
public class Stallion extends BaseModel {
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public String name;
    @Column
    public int level;
    @Column
    public String grow_type;
    @Column
    public int distance1;
    @Column
    public int distance2;
    @Column
    public String dirt;
    @Column
    public String health;
    @Column
    public String temper;
    @Column
    public String results;
    @Column
    public String spirit;
    @Column
    public String stable;
    @Column
    public String sire_line;



    public static String[] toSireLineArray(String sireLine) {
        return sireLine.split(",");
    }


    public static String fromSireLineArray(String[] list) {
        return TextUtils.join(",", list);
    }
}
