package com.heartsuit.springbootmybatis.oa.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.heartsuit.springbootmybatis.oa.entity.Employee;
import com.heartsuit.springbootmybatis.oa.page.PageRequest;
import com.heartsuit.springbootmybatis.oa.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Heartsuit
 * @Date 2021-08-09
 */
@RestController
@RequestMapping("employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 导出全量数据，实际一般为条件检索后导出
     * @param response
     * @throws IOException
     */
    @GetMapping("export-xls")
    public void exportExcel(HttpServletResponse response) throws IOException, ClassNotFoundException {
        ExcelWriter writer = ExcelUtil.getWriter();
        List<Employee> employees = employeeService.findAll();

        List<Map<String, Object>> rows = employees.stream().map(item -> {
            Map<String, Object> maps = new HashMap<>();
            maps.put("id", item.getId().toString());
            maps.put("name", item.getName());
            maps.put("age", item.getAge());
            maps.put("phone", item.getPhone());
            maps.put("createTime", item.getCreateTime().toString());
            return maps;
        }).collect(Collectors.toList());

        // Title
        int columns = Class.forName("com.heartsuit.springbootmybatis.oa.entity.Employee").getDeclaredFields().length;
        writer.merge(columns - 1, "员工信息");

        // Header
        writer.addHeaderAlias("id", "ID");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("age", "年龄");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("createTime", "时间");

        // Body
        writer.setColumnWidth(0, 30);
        writer.setColumnWidth(1, 30);
        writer.setColumnWidth(2, 30);
        writer.setColumnWidth(3, 30);
        writer.setColumnWidth(4, 30);
        writer.write(rows, true);

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("员工信息表-" + DateUtil.today() + ".xls", "utf-8"));

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }

    @PostMapping("/findByPage")
    public Object findPage(@RequestBody PageRequest pageRequest) {
        return employeeService.findByPage(pageRequest);
    }
}
