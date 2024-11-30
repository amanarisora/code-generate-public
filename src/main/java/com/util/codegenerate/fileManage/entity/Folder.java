package com.util.codegenerate.fileManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
@TableName("folder")
@Data
public class Folder {
    private String id;
    private String parentId;
    private String folderName;
    private Integer type;
    private String username;
    private Date createTime;
    private Date editTime;

    @TableField(exist = false)
    private Integer fileNum;
}
