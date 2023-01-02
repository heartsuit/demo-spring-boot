package com.taosdata.example.mybatisplusdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taosdata.example.mybatisplusdemo.domain.Power;
import com.taosdata.example.mybatisplusdemo.mapper.PowerMapper;
import com.taosdata.example.mybatisplusdemo.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2022-12-29
 */
@RestController
@RequestMapping("power")
public class PowerController {
    @Autowired
    private PowerMapper powerMapper;

    /**
     * TopN查询: 查询最新10条数据
     * @return
     */
    @GetMapping("select")
    public Result selectList() {
        QueryWrapper<Power> wrapper = new QueryWrapper<>();
        wrapper.last("limit 10");
        List<Power> powerList = powerMapper.selectList(wrapper);
        powerList.forEach(System.out::println);
        return Result.success(powerList);
    }

    /**
     * 查询数据总量
     * @return
     */
    @GetMapping("total")
    public Result selectCount() {
        int count = powerMapper.selectCount(null);
        return Result.success(count);
    }

    /**
     * 分页查询
     * @return
     */
    @GetMapping("page")
    public Result selectPage() {
        // 4 Test
        QueryWrapper<Power> wrapper = new QueryWrapper<>();
        wrapper.eq("sn", 1100);
        wrapper.eq("city", "西安");
        wrapper.eq("groupid", 2);

        IPage page = new Page(1, 3);
        IPage<Power> powerIPage = powerMapper.selectPage(page, null);

        System.out.println("total : " + powerIPage.getTotal());
        System.out.println("pages : " + powerIPage.getPages());

        for (Power power : powerIPage.getRecords()) {
            System.out.println(power);
        }
        return Result.success(powerIPage);
    }

}
