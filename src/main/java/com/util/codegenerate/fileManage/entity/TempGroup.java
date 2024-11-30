package com.util.codegenerate.fileManage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
*
* 模板组表
*
* @author author
* @since 2024-11-26
*/
@Data
@Accessors(chain = true)
@TableName("temp_group")
@Schema(name = "TempGroup", description = "")
public class TempGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String groupName;

    private String controllerTempId;

    private String entityTempId;

    private String mapperTempId;

    private String xmlTempId;

    private String serviceTempId;

    private String serviceImplTempId;

    private String username;

    private Date createTime;

    private Date editTime;
}
