package com.heartsuit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartsuit.domain.StdCommitteeSecretariatStaff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 技术委员会秘书处工作人员Mapper接口
 * 
 * @author Heartsuit
 * @date 2021-12-14
 */
@Repository
@Mapper
public interface StdCommitteeSecretariatStaffMapper extends BaseMapper<StdCommitteeSecretariatStaff>
{

}
