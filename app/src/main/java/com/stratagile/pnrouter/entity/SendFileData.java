package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.utils.FileUtil;
import com.stratagile.pnrouter.utils.FormatTransfer;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by hjk on 2018/10/9.
 */

public class SendFileData implements Serializable {
    //  int 4, short 2, byte 1, char 2, long 8, float 4
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
        System.arraycopy(fileName, 0, FileName, 0, fileName.length> FileName.length ? FileName.length : fileName.length);
    }

    public byte[] getFromId() {
        return FromId;
    }

    public void setFromId(byte[] fromId) {
        System.arraycopy(fromId, 0, FromId, 0, fromId.length > FromId.length ? FromId.length:fromId.length );
    }

    public byte[] getToId() {
        return ToId;
    }

    public void setToId(byte[] toId) {
        System.arraycopy(toId, 0, ToId, 0, toId.length > FromId.length ? FromId.length : toId.length);
    }

    public byte[] getContent() {
        return Content;
    }

    public void setContent(byte[] content) {
        System.arraycopy(content, 0, Content, 0, content.length > Content.length ? Content.length : content.length);
    }

    public byte[] toByteArray() {
        byte[] magicByte = FormatTransfer.toLH(this.Magic);
        byte[] ActionByte = FormatTransfer.toLH(this.Action);
        byte[] SegSizeByte = FormatTransfer.toLH(this.Magic);
        byte[] SegSeqByte = FormatTransfer.toLH(this.SegSeq);
        byte[] FileOffsetByte = FormatTransfer.toLH(this.FileOffset);
        byte[] FileIdByte = FormatTransfer.toLH(this.FileId);
        byte[] CRCByte = FormatTransfer.toLH(this.CRC);
        byte[] SegMoreByte = new byte[]{this.SegMore};
        byte[] CotinueByte = new byte[]{this.Cotinue};
        int length = magicByte.length + ActionByte.length + SegSizeByte.length + SegSeqByte.length + FileOffsetByte.length + FileIdByte.length + CRCByte.length + SegMoreByte.length + CotinueByte.length
                + this.FileName.length + this.FromId.length + this.ToId.length + this.Content.length;
        byte[] result = new byte[length + 2];
        int copyLength = 0;
        System.arraycopy(magicByte, 0, result, copyLength, magicByte.length);
        copyLength += magicByte.length;
        System.arraycopy(ActionByte, 0, result, copyLength, ActionByte.length);
        copyLength += ActionByte.length;
        System.arraycopy(SegSizeByte, 0, result, copyLength, SegSizeByte.length);
        copyLength += SegSizeByte.length;
        System.arraycopy(SegSeqByte, 0, result, copyLength, SegSeqByte.length);
        copyLength += SegSeqByte.length;
        System.arraycopy(FileOffsetByte, 0, result, copyLength, FileOffsetByte.length);
        copyLength += FileOffsetByte.length;
        System.arraycopy(FileIdByte, 0, result, copyLength, FileIdByte.length);
        copyLength += FileIdByte.length;
        System.arraycopy(CRCByte, 0, result, copyLength, CRCByte.length);
        copyLength += CRCByte.length;
        System.arraycopy(SegMoreByte, 0, result, copyLength, SegMoreByte.length);
        copyLength += SegMoreByte.length;
        System.arraycopy(CotinueByte, 0, result, copyLength, CotinueByte.length);
        copyLength += CotinueByte.length;
        System.arraycopy(this.FileName, 0, result, copyLength, this.FileName.length);
        copyLength += this.FileName.length;
        System.arraycopy(this.FromId, 0, result, copyLength,this.FromId.length);
        copyLength += this.FromId.length;
        System.arraycopy(this.ToId, 0, result, copyLength,this.ToId.length);
        copyLength += this.ToId.length;
        System.arraycopy(this.Content, 0, result, copyLength,this.Content.length);
        byte[] add = new byte[]{0, 0};
        System.arraycopy(add, 0, result, copyLength,add.length);
        return result;
    }
}
