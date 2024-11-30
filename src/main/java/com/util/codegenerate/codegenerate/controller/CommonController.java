package com.util.codegenerate.codegenerate.controller;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.util.codegenerate.codegenerate.entity.DatasourceInfo;
import com.util.codegenerate.codegenerate.mapper.CommonMapper;
import com.util.codegenerate.common.Result;
import com.util.codegenerate.codegenerate.service.ICodeGenerateService;
import com.util.codegenerate.codegenerate.vo.GenerateVO;
import com.util.codegenerate.common.aspect.annotation.SelectDataSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Tag(name = "基础")
@RestController
public class CommonController {

    @Autowired
    private ICodeGenerateService codeGenerateService;
    @Autowired
    private CommonMapper commonMapper;

    @GetMapping("/getAllTableList")
    @Operation(description = "获取所有表")
    public Result<?> getAllTableList(@RequestParam String databaseName,@RequestParam Integer datasourceType,
                                     @RequestParam String user,@RequestParam String ds){

        try{
            return Result.ok(codeGenerateService.getAllTableList(databaseName,datasourceType, user, ds));
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("连接数据库失败");
        }
    }
    @GetMapping("/getAllQueryInDatabase")
    @Operation(description = "获取数据库下的query")
    public Result<?> getAllQueryInDatabase(@RequestParam String databaseName,@RequestParam String user,@RequestParam String ds){

        return Result.ok(codeGenerateService.getAllQueryInDatabase(databaseName, user, ds));
    }
    @GetMapping("/getAllDataSource")
    public Result<?> getAllDataSource(@RequestParam String user){

        List<DatasourceInfo> allDataSource = codeGenerateService.getAllDataSource(user);
        return Result.ok(allDataSource);
    }

    @GetMapping("/getAllDataBases")
    public Result<?> getAllDataBases(@RequestParam String user,@RequestParam String ds){
        return Result.ok(codeGenerateService.getAllDataBases(user, ds));
    }

    @GetMapping("/testDataSourceConnection")
    public Result<?> testDataSourceConnection(DatasourceInfo vo){
        return Result.ok(codeGenerateService.testDataSourceConnection(vo));
    }

    @PostMapping("/addDataSource")
    @Operation(description = "添加数据源")
    public Result<?> addDataSource(@RequestBody DatasourceInfo vo){
        codeGenerateService.addDataSource(vo);
        return Result.ok();
    }
    @PostMapping("/editDataSource")
    @Operation(description = "修改数据源")
    public Result<?> editDataSource(@RequestBody DatasourceInfo vo){
        codeGenerateService.editDataSource(vo);
        return Result.ok();
    }

    @PostMapping("/renameDataSource")
    @Operation(description = "重命名数据源")
    public Result<?> renameDataSource(@RequestBody DatasourceInfo info){
        codeGenerateService.renameDataSource(info);
        return Result.ok();
    }

    @DeleteMapping("/deleteDataSource")
    @Operation(description = "删除数据源")
    public Result<?> deleteDataSource(@RequestParam String user,@RequestParam String name){
        codeGenerateService.deleteDataSource(name,user);
        return Result.ok();
    }

    @SelectDataSource
    @PostMapping("/createDatabase")
    @Operation(description = "创建数据库")
    public Result<?> createDatabase(@RequestParam String databaseName,@RequestParam String user,@RequestParam String ds){
        String sql = String.format("CREATE DATABASE %s CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci'",databaseName);
        SqlRunner.db().selectOne(sql);
        return Result.ok();
    }

    @SelectDataSource
    @DeleteMapping("/deleteDatabase")
    @Operation(description = "删除数据库")
    public Result<?> deleteDatabase(@RequestParam String databaseName,@RequestParam String user,@RequestParam String ds){
        String sql = String.format("DROP DATABASE %s",databaseName);
        SqlRunner.db().selectOne(sql);
        return Result.ok();
    }


    @PostMapping("/generate")
    @Operation(description = "代码生成")
    public Result<?> generateCode(@RequestBody GenerateVO vo, HttpServletResponse response){
        codeGenerateService.generateCode(vo,response);
        return Result.ok();
    }
}
