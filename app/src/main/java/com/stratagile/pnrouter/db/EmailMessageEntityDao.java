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
        public final static Property Subject = new Property(1, String.class, "subject", false, "SUBJECT");
        public final static Property From = new Property(2, String.class, "from", false, "FROM");
        public final static Property To = new Property(3, String.class, "to", false, "TO");
        public final static Property Date = new Property(4, String.class, "date", false, "DATE");
        public final static Property IsSeen = new Property(5, boolean.class, "isSeen", false, "IS_SEEN");
        public final static Property Priority = new Property(6, String.class, "priority", false, "PRIORITY");
        public final static Property IsReplySign = new Property(7, boolean.class, "isReplySign", false, "IS_REPLY_SIGN");
        public final static Property Size = new Property(8, long.class, "size", false, "SIZE");
        public final static Property IsContainerAttachment = new Property(9, boolean.class, "isContainerAttachment", false, "IS_CONTAINER_ATTACHMENT");
        public final static Property AttachmentCount = new Property(10, int.class, "attachmentCount", false, "ATTACHMENT_COUNT");
        public final static Property Content = new Property(11, String.class, "content", false, "CONTENT");
        public final static Property ContentText = new Property(12, String.class, "contentText", false, "CONTENT_TEXT");
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
                "\"SUBJECT\" TEXT," + // 1: subject
                "\"FROM\" TEXT," + // 2: from
                "\"TO\" TEXT," + // 3: to
                "\"DATE\" TEXT," + // 4: date
                "\"IS_SEEN\" INTEGER NOT NULL ," + // 5: isSeen
                "\"PRIORITY\" TEXT," + // 6: priority
                "\"IS_REPLY_SIGN\" INTEGER NOT NULL ," + // 7: isReplySign
                "\"SIZE\" INTEGER NOT NULL ," + // 8: size
                "\"IS_CONTAINER_ATTACHMENT\" INTEGER NOT NULL ," + // 9: isContainerAttachment
                "\"ATTACHMENT_COUNT\" INTEGER NOT NULL ," + // 10: attachmentCount
                "\"CONTENT\" TEXT," + // 11: content
                "\"CONTENT_TEXT\" TEXT);"); // 12: contentText
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
 
        String subject = entity.getSubject();
        if (subject != null) {
            stmt.bindString(2, subject);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(3, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(4, to);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(5, date);
        }
        stmt.bindLong(6, entity.getIsSeen() ? 1L: 0L);
 
        String priority = entity.getPriority();
        if (priority != null) {
            stmt.bindString(7, priority);
        }
        stmt.bindLong(8, entity.getIsReplySign() ? 1L: 0L);
        stmt.bindLong(9, entity.getSize());
        stmt.bindLong(10, entity.getIsContainerAttachment() ? 1L: 0L);
        stmt.bindLong(11, entity.getAttachmentCount());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(12, content);
        }
 
        String contentText = entity.getContentText();
        if (contentText != null) {
            stmt.bindString(13, contentText);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, EmailMessageEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String subject = entity.getSubject();
        if (subject != null) {
            stmt.bindString(2, subject);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(3, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(4, to);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(5, date);
        }
        stmt.bindLong(6, entity.getIsSeen() ? 1L: 0L);
 
        String priority = entity.getPriority();
        if (priority != null) {
            stmt.bindString(7, priority);
        }
        stmt.bindLong(8, entity.getIsReplySign() ? 1L: 0L);
        stmt.bindLong(9, entity.getSize());
        stmt.bindLong(10, entity.getIsContainerAttachment() ? 1L: 0L);
        stmt.bindLong(11, entity.getAttachmentCount());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(12, content);
        }
 
        String contentText = entity.getContentText();
        if (contentText != null) {
            stmt.bindString(13, contentText);
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
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // subject
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // from
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // to
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // date
            cursor.getShort(offset + 5) != 0, // isSeen
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // priority
            cursor.getShort(offset + 7) != 0, // isReplySign
            cursor.getLong(offset + 8), // size
            cursor.getShort(offset + 9) != 0, // isContainerAttachment
            cursor.getInt(offset + 10), // attachmentCount
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // content
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // contentText
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, EmailMessageEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSubject(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFrom(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsSeen(cursor.getShort(offset + 5) != 0);
        entity.setPriority(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIsReplySign(cursor.getShort(offset + 7) != 0);
        entity.setSize(cursor.getLong(offset + 8));
        entity.setIsContainerAttachment(cursor.getShort(offset + 9) != 0);
        entity.setAttachmentCount(cursor.getInt(offset + 10));
        entity.setContent(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setContentText(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
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
