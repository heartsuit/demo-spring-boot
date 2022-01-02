package com.heartsuit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heartsuit.domain.StdCommittee;

import java.io.OutputStream;

/**
 * @Author Heartsuit
 * @Date 2022-01-02
 */
public interface IStdCommitteeService extends IService<StdCommittee> {

    /**
     * 导出申报书
     * @param stdCommittee
     * @param outputStream
     */
    void generatePdf(StdCommittee stdCommittee, OutputStream outputStream);
}
