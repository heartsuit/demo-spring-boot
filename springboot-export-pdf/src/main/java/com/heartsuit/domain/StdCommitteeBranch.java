package com.heartsuit.domain;

import lombok.Data;

/**
 * 技术委员会下设分技术委员会或标准化技术专家组对象 std_committee_branch
 * 
 * @author Heartsuit
 * @date 2021-12-14
 */
@Data
public class StdCommitteeBranch
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 技术委员会id */
    private Long committeeId;

    /** 编号 */
    private String code;

    /** 名称 */
    private String name;

    /** 委员数 */
    private Integer numberMember;

}
