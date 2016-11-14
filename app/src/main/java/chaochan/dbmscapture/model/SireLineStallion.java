package chaochan.dbmscapture.model;

import chaochan.dbmscapture.model.database.Stallion;

/**
 * Created by  on 2016/11/12.
 */

public class SireLineStallion extends SireLine<Stallion> {




    /**
     * 新しいインスタンスを生成する
     */
    public SireLineStallion newInstance() {
        return new SireLineStallion();
    }


    /**
     * コンストラクタ
     */
    private SireLineStallion() {
    }


    /**
     * 種牡馬DBモデルを生成する
     * @return
     */
    protected Stallion createDatabaseModel() {
        Stallion stallion = new Stallion();
        stallion.name = getName();


        return null;
    }
}
