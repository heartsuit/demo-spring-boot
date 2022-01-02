package com.heartsuit.controller;

import com.heartsuit.domain.StdCommittee;
import com.heartsuit.service.IStdCommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author Heartsuit
 * @Date 2022-01-02
 */
@RestController
@RequestMapping("committee")
public class StdCommitteeController {
    @Autowired
    private IStdCommitteeService stdCommitteeService;

    /**
     * 导出申报书
     */
    @GetMapping("/download/{id}")
    public void downloadPdf(@PathVariable Long id, HttpServletResponse response)
    {
        StdCommittee stdCommittee = stdCommitteeService.getById(id);

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(response.getOutputStream());
            //生成pdf文件
            stdCommitteeService.generatePdf(stdCommittee, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
