package com.util.codegenerate.codegenerate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.util.codegenerate.codegenerate.entity.DatasourceInfo;
import com.util.codegenerate.codegenerate.entity.Query;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QueryMapper extends BaseMapper<Query> {
}
