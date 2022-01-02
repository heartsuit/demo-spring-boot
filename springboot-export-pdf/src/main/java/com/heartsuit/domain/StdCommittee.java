package com.heartsuit.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 技术委员会对象 std_committee
 * 
 * @author Heartsuit
 * @date 2021-12-14
 */
@Data
public class StdCommittee
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 编号 */
    private String code;

    /** 技术委员会名称 */
    private String name;

    /** 对口全国组织 */
    private String nationalOrganization;

    /** 届数 */
    private Integer numberSession;

    /** 本届成立时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date establishDate;

    /** 第一届成立时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date firstEstablishDate;

    /** 筹建单位名称 */
    private String buildUnitName;

    /** 业务指导单位名称 */
    private String guideUnitName;

    /** 负责制修订地方标准的专业领域 */
    private String professionalField;

    /** 申报人id */
    private Long applyId;

    /** 审批人id */
    private Long approvalId;

    /** 所有审批人id，用逗号分割 */
    private String allApprovalId;

    /** 审批状态 0-未提交，1-已提交，2-待审批，3-已通过，4-被驳回 */
    private String approvalStatus;

    // 技术委员会秘书处承担单位表
    @TableField(exist = false)
    private StdCommitteeSecretariat stdCommitteeSecretariat;

    // 技术委员会下设分技术委员会或标准化技术专家组表
    @TableField(exist = false)
    private List<StdCommitteeBranch> stdCommitteeBranches;
}
