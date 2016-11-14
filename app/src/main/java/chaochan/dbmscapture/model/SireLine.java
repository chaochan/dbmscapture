package chaochan.dbmscapture.model;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import chaochan.dbmscapture.model.database.Ancestor;
import chaochan.dbmscapture.model.database.Ancestor_Table;
import chaochan.dbmscapture.model.database.AppDatabase;

/**
 * Created by  on 2016/11/12.
 */

abstract class SireLine<T extends BaseModel> {

    private String mName = null;
    private Ancestor[] mAncestorList = null;


    /**
     * 名前を設定
     */
    public void setName(String name) {
        mName = name;
    }


    /**
     * 名前を取得
     */
    public String getName() {
        return mName;
    }


    /**
     * 因子リスト設定
     */
    public boolean setAncestors(Ancestor[] list) {
        if (!isValidSireLineList(mAncestorList)) {
            return false;
        }
        mAncestorList = list;
        return true;
    }


    /**
     * 因子リスト取得
     */
    public Ancestor[] getAncestorList() {
        return mAncestorList;
    }


    /**
     * 系統リストをDBへ反映する
     */
    public void updateDatabase() {
        T model = createDatabaseModel();
        List<Ancestor> ancestorList = createAncestorList(mAncestorList);

        ArrayList<BaseModel> models = new ArrayList<>();
        models.addAll(ancestorList);
        models.add(model);

        FlowManager.getDatabase(AppDatabase.class).getTransactionManager()
                .getSaveQueue().addAll2(models);
    }


    /**
     * 種牡馬または繁殖牝馬のDBモデルを生成する
     * @return
     */
    protected abstract T createDatabaseModel();


    /**
     * 父馬のDBモデルリストを生成する
     */
    private static List<Ancestor> createAncestorList(Ancestor[] list) {
        // 因子
        ArrayList<Ancestor> models = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            Ancestor ancestor = list[i];
            if (ancestor.name == null) {
                continue;
            }

            // 既に存在する？
            long count = SQLite.select().from(Ancestor.class)
                    .where(Ancestor_Table.name.eq(list[i].name))
                    .count();
            if (count == 0) {
                // なければ新規
                models.add(ancestor);
            }
        }

        return models;
    }


    /**
     * 指定された系統リストが有効かどうか
     */
    private boolean isValidSireLineList(Object[] list) {
        return (list != null && list.length == 15);
    }
}
