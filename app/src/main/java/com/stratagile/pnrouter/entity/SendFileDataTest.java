package com.stratagile.pnrouter.entity;

import java.io.Serializable;

/**
 * Created by hjk on 2018/10/9.
 */

public class SendFileDataTest implements Serializable {

    private int Magic = 1;



    public int getMagic() {
        return Magic;
    }

    public void setMagic(int magic) {
        Magic = magic;
    }


}
