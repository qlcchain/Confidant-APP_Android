package com.stratagile.pnrouter.entity;

/**
 * Created by zl on 2018/11/08.
 */

public class ToxFileData {
    private String FromId;
    private String ToId;
    private String FileName;
    private String FileMD5;
    private int FileSize;
    private FileType FileType;
    private int FileId;
    private String SrcKey;
    private String DstKey;

    public String getFromId() {
        return FromId;
    }

    public void setFromId(String fromId) {
        FromId = fromId;
    }

    public String getToId() {
        return ToId;
    }

    public void setToId(String toId) {
        ToId = toId;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileMD5() {
        return FileMD5;
    }

    public void setFileMD5(String fileMD5) {
        FileMD5 = fileMD5;
    }

    public int getFileSize() {
        return FileSize;
    }

    public void setFileSize(int fileSize) {
        FileSize = fileSize;
    }

    public FileType getFileType() {
        return FileType;
    }

    public void setFileType(FileType fileType) {
        FileType = fileType;
    }

    public int getFileId() {
        return FileId;
    }

    public void setFileId(int fileId) {
        FileId = fileId;
    }

    public String getSrcKey() {
        return SrcKey;
    }

    public void setSrcKey(String srcKey) {
        SrcKey = srcKey;
    }

    public String getDstKey() {
        return DstKey;
    }

    public void setDstKey(String dstKey) {
        DstKey = dstKey;
    }
    public enum FileType {
        PNR_IM_MSGTYPE_TEXT(0), PNR_IM_MSGTYPE_IMAGE(1), PNR_IM_MSGTYPE_AUDIO(2), PNR_IM_MSGTYPE_SYSTEM(2), PNR_IM_MSGTYPE_MEDIA(4), PNR_IM_MSGTYPE_FILE(5);
        private int value = 0;

        private FileType(int value) {    //    必须是private的，否则编译错误
            this.value = value;
        }
        public int value() {
            return this.value;
        }
    }
}


