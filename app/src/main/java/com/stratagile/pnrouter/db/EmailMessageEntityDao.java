package com.stratagile.pnrouter.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EMAIL_MESSAGE_ENTITY".
*/
public class EmailMessageEntityDao extends AbstractDao<EmailMessageEntity, Long> {

    public static final String TABLENAME = "EMAIL_MESSAGE_ENTITY";

    /**
     * Properties of entity EmailMessageEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Account = new Property(1, String.class, "account", false, "ACCOUNT");
        public final static Property MsgId = new Property(2, String.class, "msgId", false, "MSG_ID");
        public final static Property Menu = new Property(3, String.class, "menu", false, "MENU");
        public final static Property Subject = new Property(4, String.class, "subject", false, "SUBJECT");
        public final static Property From = new Property(5, String.class, "from", false, "FROM");
        public final static Property To = new Property(6, String.class, "to", false, "TO");
        public final static Property Cc = new Property(7, String.class, "cc", false, "CC");
        public final static Property Bcc = new Property(8, String.class, "bcc", false, "BCC");
        public final static Property Date = new Property(9, String.class, "date", false, "DATE");
        public final static Property TimeStamp = new Property(10, Long.class, "timeStamp", false, "TIME_STAMP");
        public final static Property IsSeen = new Property(11, boolean.class, "isSeen", false, "IS_SEEN");
        public final static Property IsStar = new Property(12, boolean.class, "isStar", false, "IS_STAR");
        public final static Property Priority = new Property(13, String.class, "priority", false, "PRIORITY");
        public final static Property IsReplySign = new Property(14, boolean.class, "isReplySign", false, "IS_REPLY_SIGN");
        public final static Property Size = new Property(15, long.class, "size", false, "SIZE");
        public final static Property IsContainerAttachment = new Property(16, boolean.class, "isContainerAttachment", false, "IS_CONTAINER_ATTACHMENT");
        public final static Property AttachmentCount = new Property(17, int.class, "attachmentCount", false, "ATTACHMENT_COUNT");
        public final static Property Content = new Property(18, String.class, "content", false, "CONTENT");
        public final static Property ContentText = new Property(19, String.class, "contentText", false, "CONTENT_TEXT");
        public final static Property OriginalText = new Property(20, String.class, "originalText", false, "ORIGINAL_TEXT");
        public final static Property AesKey = new Property(21, String.class, "aesKey", false, "AES_KEY");
        public final static Property MessageTotalCount = new Property(22, long.class, "messageTotalCount", false, "MESSAGE_TOTAL_COUNT");
        public final static Property EmailAttachPath = new Property(23, String.class, "emailAttachPath", false, "EMAIL_ATTACH_PATH");
    }


    public EmailMessageEntityDao(DaoConfig config) {
        super(config);
    }
    
    public EmailMessageEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EMAIL_MESSAGE_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"ACCOUNT\" TEXT," + // 1: account
                "\"MSG_ID\" TEXT," + // 2: msgId
                "\"MENU\" TEXT," + // 3: menu
                "\"SUBJECT\" TEXT," + // 4: subject
                "\"FROM\" TEXT," + // 5: from
                "\"TO\" TEXT," + // 6: to
                "\"CC\" TEXT," + // 7: cc
                "\"BCC\" TEXT," + // 8: bcc
                "\"DATE\" TEXT," + // 9: date
                "\"TIME_STAMP\" INTEGER," + // 10: timeStamp
                "\"IS_SEEN\" INTEGER NOT NULL ," + // 11: isSeen
                "\"IS_STAR\" INTEGER NOT NULL ," + // 12: isStar
                "\"PRIORITY\" TEXT," + // 13: priority
                "\"IS_REPLY_SIGN\" INTEGER NOT NULL ," + // 14: isReplySign
                "\"SIZE\" INTEGER NOT NULL ," + // 15: size
                "\"IS_CONTAINER_ATTACHMENT\" INTEGER NOT NULL ," + // 16: isContainerAttachment
                "\"ATTACHMENT_COUNT\" INTEGER NOT NULL ," + // 17: attachmentCount
                "\"CONTENT\" TEXT," + // 18: content
                "\"CONTENT_TEXT\" TEXT," + // 19: contentText
                "\"ORIGINAL_TEXT\" TEXT," + // 20: originalText
                "\"AES_KEY\" TEXT," + // 21: aesKey
                "\"MESSAGE_TOTAL_COUNT\" INTEGER NOT NULL ," + // 22: messageTotalCount
                "\"EMAIL_ATTACH_PATH\" TEXT);"); // 23: emailAttachPath
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EMAIL_MESSAGE_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, EmailMessageEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(2, account);
        }
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(3, msgId);
        }
 
        String menu = entity.getMenu();
        if (menu != null) {
            stmt.bindString(4, menu);
        }
 
        String subject = entity.getSubject();
        if (subject != null) {
            stmt.bindString(5, subject);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(6, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(7, to);
        }
 
        String cc = entity.getCc();
        if (cc != null) {
            stmt.bindString(8, cc);
        }
 
        String bcc = entity.getBcc();
        if (bcc != null) {
            stmt.bindString(9, bcc);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(10, date);
        }
 
        Long timeStamp = entity.getTimeStamp();
        if (timeStamp != null) {
            stmt.bindLong(11, timeStamp);
        }
        stmt.bindLong(12, entity.getIsSeen() ? 1L: 0L);
        stmt.bindLong(13, entity.getIsStar() ? 1L: 0L);
 
        String priority = entity.getPriority();
        if (priority != null) {
            stmt.bindString(14, priority);
        }
        stmt.bindLong(15, entity.getIsReplySign() ? 1L: 0L);
        stmt.bindLong(16, entity.getSize());
        stmt.bindLong(17, entity.getIsContainerAttachment() ? 1L: 0L);
        stmt.bindLong(18, entity.getAttachmentCount());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(19, content);
        }
 
        String contentText = entity.getContentText();
        if (contentText != null) {
            stmt.bindString(20, contentText);
        }
 
        String originalText = entity.getOriginalText();
        if (originalText != null) {
            stmt.bindString(21, originalText);
        }
 
        String aesKey = entity.getAesKey();
        if (aesKey != null) {
            stmt.bindString(22, aesKey);
        }
        stmt.bindLong(23, entity.getMessageTotalCount());
 
        String emailAttachPath = entity.getEmailAttachPath();
        if (emailAttachPath != null) {
            stmt.bindString(24, emailAttachPath);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, EmailMessageEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(2, account);
        }
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(3, msgId);
        }
 
        String menu = entity.getMenu();
        if (menu != null) {
            stmt.bindString(4, menu);
        }
 
        String subject = entity.getSubject();
        if (subject != null) {
            stmt.bindString(5, subject);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(6, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(7, to);
        }
 
        String cc = entity.getCc();
        if (cc != null) {
            stmt.bindString(8, cc);
        }
 
        String bcc = entity.getBcc();
        if (bcc != null) {
            stmt.bindString(9, bcc);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(10, date);
        }
 
        Long timeStamp = entity.getTimeStamp();
        if (timeStamp != null) {
            stmt.bindLong(11, timeStamp);
        }
        stmt.bindLong(12, entity.getIsSeen() ? 1L: 0L);
        stmt.bindLong(13, entity.getIsStar() ? 1L: 0L);
 
        String priority = entity.getPriority();
        if (priority != null) {
            stmt.bindString(14, priority);
        }
        stmt.bindLong(15, entity.getIsReplySign() ? 1L: 0L);
        stmt.bindLong(16, entity.getSize());
        stmt.bindLong(17, entity.getIsContainerAttachment() ? 1L: 0L);
        stmt.bindLong(18, entity.getAttachmentCount());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(19, content);
        }
 
        String contentText = entity.getContentText();
        if (contentText != null) {
            stmt.bindString(20, contentText);
        }
 
        String originalText = entity.getOriginalText();
        if (originalText != null) {
            stmt.bindString(21, originalText);
        }
 
        String aesKey = entity.getAesKey();
        if (aesKey != null) {
            stmt.bindString(22, aesKey);
        }
        stmt.bindLong(23, entity.getMessageTotalCount());
 
        String emailAttachPath = entity.getEmailAttachPath();
        if (emailAttachPath != null) {
            stmt.bindString(24, emailAttachPath);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public EmailMessageEntity readEntity(Cursor cursor, int offset) {
        EmailMessageEntity entity = new EmailMessageEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // account
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // msgId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // menu
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // subject
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // from
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // to
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // cc
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // bcc
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // date
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // timeStamp
            cursor.getShort(offset + 11) != 0, // isSeen
            cursor.getShort(offset + 12) != 0, // isStar
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // priority
            cursor.getShort(offset + 14) != 0, // isReplySign
            cursor.getLong(offset + 15), // size
            cursor.getShort(offset + 16) != 0, // isContainerAttachment
            cursor.getInt(offset + 17), // attachmentCount
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // content
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // contentText
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // originalText
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // aesKey
            cursor.getLong(offset + 22), // messageTotalCount
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23) // emailAttachPath
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, EmailMessageEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAccount(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMsgId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMenu(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSubject(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFrom(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTo(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCc(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setBcc(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDate(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setTimeStamp(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setIsSeen(cursor.getShort(offset + 11) != 0);
        entity.setIsStar(cursor.getShort(offset + 12) != 0);
        entity.setPriority(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setIsReplySign(cursor.getShort(offset + 14) != 0);
        entity.setSize(cursor.getLong(offset + 15));
        entity.setIsContainerAttachment(cursor.getShort(offset + 16) != 0);
        entity.setAttachmentCount(cursor.getInt(offset + 17));
        entity.setContent(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setContentText(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setOriginalText(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setAesKey(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setMessageTotalCount(cursor.getLong(offset + 22));
        entity.setEmailAttachPath(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(EmailMessageEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(EmailMessageEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(EmailMessageEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
