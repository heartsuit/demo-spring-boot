package com.heartsuit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heartsuit.domain.Poetry;
import com.heartsuit.mapper.PoetryMapper;
import com.heartsuit.service.PoetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2023-06-09
 */
@RestController
public class PoetryController {
    @Autowired
    private PoetryService poetryService;

    @Autowired
    private PoetryMapper poetryMapper;

    @GetMapping("list")
    private List<Poetry> list() {
        return poetryService.list(new QueryWrapper<Poetry>().last("limit 10"));
    }

    @GetMapping("condition")
    private List<Poetry> listByCondition() {
        LambdaQueryWrapper<Poetry> wrapper = new QueryWrapper<Poetry>().lambda().eq(Poetry::getAuthor, "顾城");
        return poetryService.list(wrapper);
    }

    @GetMapping("page")
    private IPage<Poetry> listByPage(@RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "2") Integer size) {
        return poetryMapper.selectPage(new Page<>(page, size), null);
    }

    @PostMapping("save")
    public boolean save() {
        Poetry poetry = new Poetry();
        poetry.setId(400000); // 如果ClickHouse中没有设置ID自增，需要显式赋值
        poetry.setAuthorId(20000);
        poetry.setTitle("一代人");
        poetry.setContent("黑夜给了我黑色的眼睛，我却用它寻找光明");
        poetry.setYunlvRule("balabala");
        poetry.setDynasty('Z');
        poetry.setAuthor("顾城");

        return poetryService.save(poetry);
    }

    // Update和Delete语句在ClickHouse中报错，ClickHouse的修改和删除SQL操作与MySQL不同。
    // 参考解决：https://github.com/saimen90/clickhouse
    @PutMapping("update/{id}")
    public boolean update(@PathVariable Integer id) {
        Poetry poetry = poetryService.getById(id);
        poetry.setYunlvRule("wow");
        return poetryMapper.updateByIdClickHouse(poetry); // 扩展方法
    }

//    报错！！需要扩展MyBatis源码
//    @PutMapping("update")
//    public boolean updateByCondition() {
//        UpdateWrapper<Poetry> updateWrapper = new UpdateWrapper<>();
//        return poetryService.update(updateWrapper.lambda().set(Poetry::getDynasty, "C").eq(Poetry::getId, 40000));
//    }

    @DeleteMapping("delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        // 删除成功或失败，count都为0。。
        int count = poetryMapper.deleteByIdClickHouse(id); // 扩展方法
        return count > 0;
    }
}
