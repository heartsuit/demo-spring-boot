package com.heartsuit.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 技术委员会秘书处工作人员对象 std_committee_secretariat_staff
 * 
 * @author Heartsuit
 * @date 2021-12-14
 */
@Data
public class StdCommitteeSecretariatStaff
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 技术委员会秘书处id */
    private Long committeeSecretariatId;

    /** 姓名 */
    private String name;

    /** 秘书类型 */
    private String type;

    /** 职务/职称 */
    private String professionalTitle;

    /** 出生年月 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /** 学历 */
    private String qualification;

    /** 电话 */
    private String phone;
}
