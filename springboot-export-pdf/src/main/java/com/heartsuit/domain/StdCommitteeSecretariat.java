package com.heartsuit.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * 技术委员会秘书处承担单位对象 std_committee_secretariat
 * 
 * @author Heartsuit
 * @date 2021-12-14
 */
@Data
public class StdCommitteeSecretariat
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 技术委员会id */
    private Long committeeId;

    /** 名称 */
    private String name;

    /** 统一社会信用代码 */
    private String unifySocialCreditCode;

    /** 单位性质 */
    private String unitProperty;

    /** 通信地址 */
    private String communicationAddress;

    /** 邮政编码 */
    private String postalCode;

    /** 电话 */
    private String phone;

    /** 电子邮箱 */
    private String email;

    /** 传真 */
    private String fax;

    /** 委员数 */
    private Integer numberCommitteeMember;

    /** 顾问数 */
    private Integer numberAdviser;

    /** 观察员数 */
    private Integer numberObserver;

    // 技术委员会秘书处工作人员表
    @TableField(exist = false)
    private List<StdCommitteeSecretariatStaff> stdCommitteeSecretariatStaffs;
}
