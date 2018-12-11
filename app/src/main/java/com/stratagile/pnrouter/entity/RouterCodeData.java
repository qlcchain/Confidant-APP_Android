package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.utils.FormatTransfer;

import java.io.Serializable;

/**
 * Created by hjk on 2018/10/9.
 */

public class RouterCodeData implements Serializable {

    private byte[] Id = new byte[6];
    private byte[] RouterId = new byte[76];
    private byte[] UserSn = new byte[32];

    public byte[] getId() {
        return Id;
    }

    public void setId(byte[] id) {
        System.arraycopy(id, 0, Id, 0, id.length> Id.length ? Id.length : id.length);
    }

    public byte[] getRouterId() {
        return RouterId;
    }

    public void setRouterId(byte[] routerId) {
        System.arraycopy(routerId, 0, RouterId, 0, routerId.length > RouterId.length ? RouterId.length:routerId.length );
    }

    public byte[] getUserSn() {
        return UserSn;
    }

    public void setUserSn(byte[] userSn) {
        System.arraycopy(userSn, 0, UserSn, 0, userSn.length > UserSn.length ? UserSn.length:userSn.length );
    }

    public byte[] toByteArray() {

        int length = Id.length + RouterId.length + UserSn.length;
        byte[] result = new byte[length];
        int copyLength = 0;
        System.arraycopy(Id, 0, result, copyLength, Id.length);
        copyLength += Id.length;
        System.arraycopy(RouterId, 0, result, copyLength, RouterId.length);
        copyLength += RouterId.length;
        System.arraycopy(UserSn, 0, result, copyLength, UserSn.length);
        copyLength += UserSn.length;

        return result;
    }
}
