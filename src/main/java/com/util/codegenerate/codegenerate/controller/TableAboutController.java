package com.util.codegenerate.codegenerate.controller;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.util.codegenerate.codegenerate.mapper.QueryMapper;
import com.util.codegenerate.codegenerate.service.ITableAboutService;
import com.util.codegenerate.codegenerate.vo.SaveQueryVO;
import com.util.codegenerate.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
public class TableAboutController {
    @Autowired
    private ITableAboutService tableAboutService;
    @Autowired
    private QueryMapper queryMapper;

    @GetMapping("/getTableColumnVueList")
    @Operation(description = "获取给前端表格用的column")
    public Result<?> getTableColumnVueList(@RequestParam String databaseName, @RequestParam String tableName,
                                           @RequestParam String user, @RequestParam String ds) {

        return Result.ok(tableAboutService.getTableColumnVueList(databaseName, tableName, user, ds));
    }

    @GetMapping("/queryTableDateByPage")
    @Operation(description = "获取表格数据")
    public Result<?> queryTableDateByPage(@RequestParam String databaseName, @RequestParam String tableName,
                                          @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "20") Integer pageSize,
                                          @RequestParam String user, @RequestParam String ds) {

        return Result.ok(tableAboutService.queryTableDateByPage(databaseName, tableName, pageNo, pageSize, user, ds));
    }

    @PostMapping("/saveQuery")
    @Operation(description = "保存查询")
    public Result<?> saveQuery(@RequestBody SaveQueryVO vo) {
        tableAboutService.saveQuery(vo);
        return Result.ok();
    }

    @DS("w51sa35Mz9AW")
    @DeleteMapping("/deleteQuery")
    @Operation(description = "删除查询")
    public Result<?> deleteQuery(@RequestParam String id) {
        queryMapper.deleteById(id);
        return Result.ok();
    }

    @GetMapping("/runQuerySql")
    @Operation(description = "运行查询的sql")
    public Result<?> runQuerySql(@RequestParam String databaseName, @RequestParam String sql,
                                 @RequestParam String user, @RequestParam String ds) {

        return Result.ok(tableAboutService.runQuerySql(databaseName, sql, user, ds));
    }
}
