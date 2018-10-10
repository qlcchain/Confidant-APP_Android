package com.stratagile.pnrouter.entity;

import com.alibaba.fastjson.JSONObject;
import com.stratagile.pnrouter.utils.FileUtil;

import java.io.Serializable;

/**
 * Created by hjk on 2018/10/9.
 */

public class SendFileData implements Serializable {

    private int Magic = 0x0dadc0de;
    private int Action = 1;
    private int SegSize;
    private int SegSeq;
    private int FileOffset;
    private int FileId;
    private short CRC;
    private byte SegMore;
    private byte Cotinue;
    private byte[] FileName = new byte[256];
    private byte[] FromId = new byte[77];
    private byte[] ToId = new byte[77];
    private byte[] Content = new byte[1024*100];

    public int getMagic() {
        return Magic;
    }

    public void setMagic(int magic) {
        Magic = magic;
    }
    public int getAction() {
        return Action;
    }

    public void setAction(int action) {
        Action = action;
    }

    public int getSegSize() {
        return SegSize;
    }

    public void setSegSize(int segSize) {
        SegSize = segSize;
    }

    public int getSegSeq() {
        return SegSeq;
    }

    public void setSegSeq(int segSeq) {
        SegSeq = segSeq;
    }

    public int getFileOffset() {
        return FileOffset;
    }

    public void setFileOffset(int fileOffset) {
        FileOffset = fileOffset;
    }

    public int getFileId() {
        return FileId;
    }

    public void setFileId(int fileId) {
        FileId = fileId;
    }

    public short getCRC() {
        return CRC;
    }

    public void setCRC(short CRC) {
        this.CRC = CRC;
    }

    public byte getSegMore() {
        return SegMore;
    }

    public void setSegMore(byte segMore) {
        SegMore = segMore;
    }

    public byte getCotinue() {
        return Cotinue;
    }

    public void setCotinue(byte cotinue) {
        Cotinue = cotinue;
    }

    public byte[] getFileName() {
        return FileName;
    }

    public void setFileName(byte[] fileName) {
        FileName = fileName;
    }

    public byte[] getFromId() {
        return FromId;
    }

    public void setFromId(byte[] fromId) {
        FromId = fromId;
    }

    public byte[] getToId() {
        return ToId;
    }

    public void setToId(byte[] toId) {
        ToId = toId;
    }

    public byte[] getContent() {
        return Content;
    }

    public void setContent(byte[] content) {
        Content = content;
    }

    public byte[] toByteArray(int size) {
        byte[] fileBuffer = new byte[size];
        //System.arraycopy(src, 0, fileBuffer, 0, count);
        return fileBuffer;
    }
}
