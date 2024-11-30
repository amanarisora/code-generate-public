package com.util.codegenerate.fileManage.controller;

import com.util.codegenerate.common.Result;
import com.util.codegenerate.common.exceptions.WrongTempGroupSettingException;
import com.util.codegenerate.fileManage.entity.TempGroup;
import com.util.codegenerate.fileManage.service.ITempGroupService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
*
* 
*
* @author author
* @since 2024-11-26
*/
@RestController
@RequestMapping("/tempGroup")
public class TempGroupController {
    @Autowired
    private ITempGroupService tempGroupService;

    @GetMapping("/listTempGroup")
    public Result<?> listTempGroup(@RequestParam String username){
        return Result.ok(tempGroupService.listTempGroup(username));
    }

    @GetMapping("/getTempGroupSetting")
    public Result<?> getTempGroupSetting(@RequestParam String groupName,@RequestParam String username){
        try {
            return Result.ok(tempGroupService.getTempGroupSetting(groupName,username));
        } catch (WrongTempGroupSettingException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addNewTempGroup")
    public Result<?> addNewTempGroup(@RequestBody TempGroup tempGroup){
        tempGroupService.addNewTempGroup(tempGroup);
        return Result.ok();
    }

    @PostMapping("/updateTempGroup")
    public Result<?> updateTempGroup(@RequestBody TempGroup tempGroup){
        tempGroupService.updateTempGroup(tempGroup);
        return Result.ok();
    }

    @PostMapping("/renameTempGroup")
    public Result<?> renameTempGroup(@RequestBody TempGroup tempGroup){
        tempGroupService.renameTempGroup(tempGroup);
        return Result.ok();
    }

    public static String convertTimestampToDateTime(long timestamp) {
        // 将时间戳转换为LocalDateTime
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 格式化日期时间
        return dateTime.format(formatter);
    }

    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis(); // 当前时间戳
        String formattedDateTime = convertTimestampToDateTime(timestamp);
        System.out.println("Formatted DateTime: " + formattedDateTime);
    }



}
