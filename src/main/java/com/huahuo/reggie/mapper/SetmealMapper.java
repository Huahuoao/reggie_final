package com.huahuo.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huahuo.reggie.entity.Setmeal;
import com.huahuo.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
  @Select("select * from setmeal_dish where setmeal_id = ${setMealId}")
  List<SetmealDish> SelectSetMealIdByDishId(Long setMealId);
}
