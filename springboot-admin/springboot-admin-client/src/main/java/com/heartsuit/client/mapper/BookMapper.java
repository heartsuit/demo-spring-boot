package com.heartsuit.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartsuit.client.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * <p>
 * 书籍 Mapper 接口
 * </p>
 *
 * @author Heartsuit
 * @since 2021-08-25
 */
@Repository
@Mapper
public interface BookMapper extends BaseMapper<Book> {

}
