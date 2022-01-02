package com.heartsuit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartsuit.domain.StdCommitteeBranch;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 技术委员会下设分技术委员会或标准化技术专家组Mapper接口
 * 
 * @author Heartsuit
 * @date 2021-12-14
 */
@Repository
@Mapper
public interface StdCommitteeBranchMapper extends BaseMapper<StdCommitteeBranch>
{

}
