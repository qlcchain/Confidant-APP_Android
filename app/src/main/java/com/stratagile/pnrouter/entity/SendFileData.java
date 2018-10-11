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

    public byte[] toByteArray(int size) {
        byte[] fileBuffer = new byte[size];
        //System.arraycopy(src, 0, fileBuffer, 0, count);
        return fileBuffer;
    }

    public String toByteLength() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Magic ").append(FileUtil.toByteArray(Magic));
        return stringBuilder.toString();
    }

    public static void main(String args[]) {
        SendFileData sendFileData = new SendFileData();
        //int = 4
        sendFileData.Magic = 1;
        sendFileData.Action = 2;
        sendFileData.SegSize = 3;
        sendFileData.SegSeq = 4;
        sendFileData.FileOffset = 5;
        sendFileData.FileId = 6;
        //short = 2
        sendFileData.CRC = 7;
        //byte = 1
        sendFileData.SegMore = 8;
        sendFileData.Cotinue = 9;

        sendFileData.FileName = new byte[256];
        sendFileData.FromId = new byte[77];
        sendFileData.ToId = new byte[77];
        sendFileData.Content = new byte[1024*100];
//        System.out.println(Arrays.toString(FileUtil.toByteArray(sendFileData)));
        byte[] magicByte = FormatTransfer.toLH(sendFileData.Magic);
        byte[] ActionByte = FormatTransfer.toLH(sendFileData.Action);
        byte[] SegSizeByte = FormatTransfer.toLH(sendFileData.Magic);
        byte[] SegSeqByte = FormatTransfer.toLH(sendFileData.SegSeq);
        byte[] FileOffsetByte = FormatTransfer.toLH(sendFileData.FileOffset);
        byte[] FileIdByte = FormatTransfer.toLH(sendFileData.FileId);

        byte[] CRCByte = FormatTransfer.toLH(sendFileData.CRC);

        byte[] SegMoreByte = new byte[sendFileData.SegMore];
        byte[] CotinueByte = new byte[sendFileData.Cotinue];

        System.out.println(magicByte.length);
        System.out.println(ActionByte.length);
        System.out.println(SegSizeByte.length);
        System.out.println(SegSeqByte.length);
        System.out.println(FileOffsetByte.length);
        System.out.println(FileIdByte.length);

        System.out.println(CRCByte.length);

        System.out.println(SegMoreByte.length);
        System.out.println(CotinueByte.length);

        System.out.println(sendFileData.FileName.length);
        System.out.println(sendFileData.FromId.length);
        System.out.println(sendFileData.ToId.length);
        System.out.println(sendFileData.Content.length);

        int length = magicByte.length + ActionByte.length + SegSizeByte.length + SegSeqByte.length + FileOffsetByte.length + FileIdByte.length + CRCByte.length + SegMoreByte.length + CotinueByte.length
                + sendFileData.FileName.length + sendFileData.FromId.length + sendFileData.ToId.length + sendFileData.Content.length;
        System.out.println("总长度为：" + length);

        byte[] result = new byte[length];
//        Object src : 原数组
//        int srcPos : 从元数据的起始位置开始
//　　Object dest : 目标数组
//　　int destPos : 目标数组的开始起始位置
//　　int length  : 要copy的数组的长度
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
        System.arraycopy(sendFileData.FileName, 0, result, copyLength, sendFileData.FileName.length);
        copyLength += sendFileData.FileName.length;
        System.arraycopy(sendFileData.FromId, 0, result, copyLength,sendFileData.FromId.length);
        copyLength += sendFileData.FromId.length;
        System.arraycopy(sendFileData.ToId, 0, result, copyLength,sendFileData.ToId.length);
        copyLength += sendFileData.ToId.length;
        System.arraycopy(sendFileData.Content, 0, result, copyLength,sendFileData.Content.length);
        copyLength += sendFileData.Content.length;
        System.out.println("数组的总长度为 ：" + result.length);
        System.out.println(Arrays.toString(result));
//        System.out.println(Arrays.toString(new byte[1]));
    }
}
