package com.heartsuit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heartsuit.domain.TaskInfo;
import com.heartsuit.job.TestJob;
import com.heartsuit.result.Result;
import com.heartsuit.service.TaskService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Heartsuit
 * @Date 2021-11-09
 */
@RestController
@RequestMapping("/job")
public class TaskController {
    @Autowired
    private TaskService schedulerService;

    /**
     * 获取任务列表
     *
     * @return
     */
    @ApiOperation(value = "获取任务列表", notes = "获取任务列表")
    @GetMapping(value = "/list")
    public Result<IPage<TaskInfo>> getJobList(@RequestParam(required = false) Map map) {
        List<TaskInfo> list = schedulerService.getJobList();
        IPage<TaskInfo> page = new Page<>();
        page.setRecords(list);
        page.setTotal(list.size());
        return Result.success(page);
    }

    /**
     * 添加调度任务
     *
     * @param jobName        任务名称
     * @param jobDescription 任务描述
     * @param jobType        任务类型
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param repeatInterval 间隔时间
     * @param repeatCount    重试次数
     * @param cron           cron表达式
     * @return
     */
    @ApiOperation(value = "添加调度任务", notes = "添加调度任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobDescription", value = "任务描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobType", value = "任务类型", required = true, allowableValues = "simple,cron", paramType = "form"),
            @ApiImplicitParam(name = "cron", value = "cron表达式", required = false, paramType = "form"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatInterval", value = "间隔时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatCount", value = "重试次数", required = false, paramType = "form")
    })
    @PostMapping("/create")
    public Result createJob(@RequestParam(name = "jobName") String jobName,
                             @RequestParam(name = "jobDescription") String jobDescription,
                             @RequestParam(name = "jobType") String jobType,
                             @RequestParam(name = "cron", required = false) String cron,
                             @RequestParam(name = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                             @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                             @RequestParam(name = "repeatInterval", required = false, defaultValue = "0") Long repeatInterval,
                             @RequestParam(name = "repeatCount", required = false, defaultValue = "0") Integer repeatCount) {
        TaskInfo taskInfo = new TaskInfo();
        Map<String, Object> data = new HashMap<>();
        data.put("some", "thing");
        taskInfo.setData(data);
        taskInfo.setJobName(jobName);
        taskInfo.setJobDescription(jobDescription);
        taskInfo.setJobClassName(TestJob.class.getName());
        taskInfo.setJobGroupName(Scheduler.DEFAULT_GROUP);
        taskInfo.setStartDate(startTime);
        taskInfo.setEndDate(endTime);
        taskInfo.setRepeatInterval(repeatInterval);
        taskInfo.setRepeatCount(repeatCount);
        taskInfo.setCronExpression(cron);
        if ("simple".equals(jobType)) {
            Assert.notNull(taskInfo.getStartDate(), "startTime不能为空");
            schedulerService.addSimpleJob(taskInfo);
        } else {
            Assert.notNull(taskInfo.getCronExpression(), "cron表达式不能为空");
            schedulerService.addCronJob(taskInfo);
        }
        return Result.success();
    }

    /**
     * 修改调度任务
     *
     * @param jobName        任务名称
     * @param jobDescription 任务描述
     * @param jobType        任务类型
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param repeatInterval 间隔时间
     * @param repeatCount    重试次数
     * @param cron           cron表达式
     * @return
     */
    @ApiOperation(value = "修改度任务", notes = "修改调度任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobDescription", value = "任务描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "jobType", value = "任务类型", required = true, allowableValues = "simple,cron", paramType = "form"),
            @ApiImplicitParam(name = "cron", value = "cron表达式", required = false, paramType = "form"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatInterval", value = "间隔时间", required = false, paramType = "form"),
            @ApiImplicitParam(name = "repeatCount", value = "重试次数", required = false, paramType = "form")
    })
    @PutMapping("/update")
    public Result updateJob(@RequestParam(name = "jobName") String jobName,
                                    @RequestParam(name = "jobDescription") String jobDescription,
                                    @RequestParam(name = "jobType") String jobType,
                                    @RequestParam(name = "cron", required = false) String cron,
                                    @RequestParam(name = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                    @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                    @RequestParam(name = "repeatInterval", required = false,defaultValue = "0") Long repeatInterval,
                                    @RequestParam(name = "repeatCount", required = false,defaultValue = "0") Integer repeatCount) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobName(jobName);
        taskInfo.setJobDescription(jobDescription);
        taskInfo.setJobClassName(TestJob.class.getName());
        taskInfo.setJobGroupName(Scheduler.DEFAULT_GROUP);
        taskInfo.setStartDate(startTime);
        taskInfo.setEndDate(endTime);
        taskInfo.setRepeatInterval(repeatInterval);
        taskInfo.setRepeatCount(repeatCount);
        taskInfo.setCronExpression(cron);
        if ("simple".equals(jobType)) {
            Assert.notNull(taskInfo.getStartDate(), "startTime不能为空");
            schedulerService.editSimpleJob(taskInfo);
        } else {
            Assert.notNull(taskInfo.getCronExpression(), "cron表达式不能为空");
            schedulerService.editCronJob(taskInfo);
        }
        return Result.success();
    }


    /**
     * 删除任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "删除任务", notes = "删除任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @DeleteMapping("/delete")
    public Result deleteJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.deleteJob(jobName, Scheduler.DEFAULT_GROUP);
        return Result.success();
    }

    /**
     * 暂停任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "暂停任务", notes = "暂停任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/pause")
    public Result pauseJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.pauseJob(jobName, Scheduler.DEFAULT_GROUP);
        return Result.success();
    }

    /**
     * 恢复任务
     *
     * @param jobName 任务名称
     * @return
     */
    @ApiOperation(value = "恢复任务", notes = "恢复任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "任务名称", required = true, paramType = "form")
    })
    @PostMapping("/resume")
    public Result resumeJob(@RequestParam(name = "jobName") String jobName) {
        schedulerService.resumeJob(jobName, Scheduler.DEFAULT_GROUP);
        return Result.success();
    }
}
