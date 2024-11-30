package com.util.codegenerate.fileManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("temp_file")
@Data
public class TempFile {
    private String id;
    private String folderId;
    private String file;
    private String fileName;
    private Integer fileType;
    private String username;
    private Date uploadTime;
    private Date editTime;

    @TableField(exist = false)
    private String fileContent;
}
