package com.util.codegenerate.codegenerate.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TableColumnVueVO {
    private String dataIndex;
    private String key;
    private String title;
    private Integer width;
    private Integer maxWidth;
    private Boolean resizable;
    private Boolean ellipsis;
}
