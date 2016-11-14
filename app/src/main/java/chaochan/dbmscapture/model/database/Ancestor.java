package chaochan.dbmscapture.model.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 父馬モデル
 */
@Table(database = AppDatabase.class)
public class Ancestor extends BaseModel {
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public String name;
    @Column
    public String ability1;
    @Column
    public String ability2;
    @Column
    public String parent_line;
    @Column
    public String child_line;
}
