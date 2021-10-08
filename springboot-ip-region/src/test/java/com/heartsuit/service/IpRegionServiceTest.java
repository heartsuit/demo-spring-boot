package com.heartsuit.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author Heartsuit
 * @Date 2021-10-08
 */
@SpringBootTest
@Slf4j
public class IpRegionServiceTest {

    @Autowired
    private IpRegionService ipRegionService;

    @Test
    void getRegion() {
//        String region = ipRegionService.getRegion("223.11.214.177"); //中国|0|山西省|太原市|电信
//        String region = ipRegionService.getRegion("101.132.130.218"); //中国|0|上海|上海市|阿里云
//        String region = ipRegionService.getRegion("114.116.3.223"); //中国|0|广东省|深圳市|鹏博士, 实际为北京华为云
//        String region = ipRegionService.getRegion("114.115.235.120"); //中国|0|北京|北京市|电信, 实际为北京华为云
//        String region = ipRegionService.getRegion("139.199.22.71"); //中国|0|天津|天津市|电信, 实际为天津腾讯云

//        String region = ipRegionService.getRegion("192.168.169.130"); //0|0|0|内网IP|内网IP, 实际为VMWare虚拟机IP
//        String region = ipRegionService.getRegion("127.0.0.1"); //0|0|0|内网IP|内网IP
//        String region = ipRegionService.getRegion("localhost"); //0|0|0|内网IP|内网IP
        String region = ipRegionService.getRegion("www.abc.com"); //0|0|0|内网IP|内网IP, 有个警告：warning: Invalid ip address
        log.info("IP from Region: {}", region);
    }
}