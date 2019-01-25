package com.stratagile.pnrouter.entity.file;

import com.chad.library.adapter.base.entity.SectionEntity;

public class TaskFile extends SectionEntity<UpLoadFile> {

    public TaskFile(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public TaskFile(UpLoadFile upLoadFile) {
        super(upLoadFile);
    }
}
