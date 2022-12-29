package com.heartsuit.springbootmybatisplussqlserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.heartsuit.springbootmybatisplussqlserver.domain.Material;
import com.heartsuit.springbootmybatisplussqlserver.service.MaterialService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Author Heartsuit
 * @Date 2022-12-11
 */
@RestController
public class MaterialController {
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("list")
    public List<Material> list(){
        return materialService.list();
    }

    @PostMapping("save")
    public boolean save(){
        Material material = new Material();
        material.setCangkuNum(888);
        material.setQuantity(100);
        material.setWuliaoName("测试");
        material.setAddTime(new Date());

        return materialService.save(material);
    }

    @PutMapping("update")
    public boolean update(){
        UpdateWrapper<Material> updateWrapper = new UpdateWrapper<>();
        return materialService.update(updateWrapper.lambda().set(Material::getQuantity, 99).eq(Material::getCangkuNum, 888));
    }

    @DeleteMapping("delete/{id}")
    public boolean deleteByCondition(@PathVariable Integer id){
        return materialService.removeById(id);
    }

    @DeleteMapping("deleteByCondition")
    public boolean deleteByCondition(){
        return materialService.remove(new QueryWrapper<Material>().lambda().eq(Material::getCangkuNum, 888));
    }

    @PostMapping("saveTransaction")
    @Transactional
    public boolean saveWithTransaction(){
        Material material = new Material();
        material.setCangkuNum(777);
        material.setQuantity(77);
        material.setWuliaoName("测试");
        material.setAddTime(new Date());
        materialService.save(material);

        // Exception
        int x = 1/0;

        material.setCangkuNum(999);
        material.setQuantity(99);
        material.setWuliaoName("测试");
        material.setAddTime(new Date());

        return materialService.save(material);
    }
}
