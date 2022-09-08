package com.huahuo.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huahuo.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{
}
