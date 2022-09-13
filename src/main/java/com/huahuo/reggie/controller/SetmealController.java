package com.huahuo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huahuo.reggie.common.R;
import com.huahuo.reggie.dto.SetmealDto;
import com.huahuo.reggie.entity.Category;
import com.huahuo.reggie.entity.Setmeal;
import com.huahuo.reggie.entity.SetmealDish;
import com.huahuo.reggie.mapper.DishMapper;
import com.huahuo.reggie.mapper.SetmealMapper;
import com.huahuo.reggie.service.CategoryService;
import com.huahuo.reggie.service.SetmealDishService;
import com.huahuo.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/** 套餐管理 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api("套餐管理")
public class SetmealController {

  @Autowired private SetmealService setmealService;

  @Autowired private CategoryService categoryService;

  @Autowired private SetmealDishService setmealDishService;

  @Autowired private SetmealMapper setmealMapper;
  /**
   * 新增套餐
   *
   * @param setmealDto
   * @return
   */
  @ApiOperation("新增套餐")
  @PostMapping
  @CacheEvict(value = "setmealCache", allEntries = true)
  public R<String> save(@RequestBody SetmealDto setmealDto) {
    log.info("套餐信息：{}", setmealDto);

    setmealService.saveWithDish(setmealDto);

    return R.success("新增套餐成功");
  }

  /**
   * 套餐分页查询
   *
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @ApiOperation("套餐分页查询")
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    // 分页构造器对象
    Page<Setmeal> pageInfo = new Page<>(page, pageSize);
    Page<SetmealDto> dtoPage = new Page<>();

    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    // 添加查询条件，根据name进行like模糊查询
    queryWrapper.like(name != null, Setmeal::getName, name);
    // 添加排序条件，根据更新时间降序排列
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    setmealService.page(pageInfo, queryWrapper);

    // 对象拷贝
    BeanUtils.copyProperties(pageInfo, dtoPage, "records");
    List<Setmeal> records = pageInfo.getRecords();

    List<SetmealDto> list =
        records.stream()
            .map(
                (item) -> {
                  SetmealDto setmealDto = new SetmealDto();
                  // 对象拷贝
                  BeanUtils.copyProperties(item, setmealDto);
                  // 分类id
                  Long categoryId = item.getCategoryId();
                  // 根据分类id查询分类对象
                  Category category = categoryService.getById(categoryId);
                  if (category != null) {
                    // 分类名称
                    String categoryName = category.getName();
                    setmealDto.setCategoryName(categoryName);
                  }
                  return setmealDto;
                })
            .collect(Collectors.toList());

    dtoPage.setRecords(list);
    return R.success(dtoPage);
  }

  /**
   * 删除套餐
   *
   * @param ids
   * @return
   */
  @ApiOperation("删除套餐")
  @DeleteMapping
  @CacheEvict(value = "setmealCache", allEntries = true)
  public R<String> delete(@RequestParam List<Long> ids) {
    log.info("ids:{}", ids);

    setmealService.removeWithDish(ids);

    return R.success("套餐数据删除成功");
  }

  /**
   * 根据条件查询套餐数据
   *
   * @param setmeal
   * @return
   */
  @ApiOperation("根据条件查询套餐数据")
  @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
  @GetMapping("/list")
  public R<List<Setmeal>> list(Setmeal setmeal) {
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(
        setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
    queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    List<Setmeal> list = setmealService.list(queryWrapper);

    return R.success(list);
  }

  @Autowired private DishMapper dishMapper;

  @ApiOperation("回显套餐信息")
  @GetMapping("/{id}")
  public R<SetmealDto> update(@PathVariable Long id) {
    SetmealDto setmealDto = new SetmealDto();
    Setmeal setmeal = setmealService.getById(id);
    BeanUtils.copyProperties(setmeal, setmealDto);
    Category categoryId = categoryService.getById(setmeal.getCategoryId());
    setmealDto.setCategoryName(categoryId.getName());
    // 获得dish list
    // select dish_id where setmeal_id = ${setmeal_id}
    List<SetmealDish> longs = setmealMapper.SelectSetMealIdByDishId(setmeal.getId());
    setmealDto.setSetmealDishes(longs);
    return R.success(setmealDto);
  }

  /**
   * 停售
   *
   * @param ids
   * @return
   */
  @ApiOperation("停售")
  @PostMapping("/status/0")
  public R<String> stop(@RequestParam List<Long> ids) {
    for (Long id : ids) {
      Setmeal setmeal = setmealService.getById(id);
      setmeal.setStatus(0);
      setmealService.updateById(setmeal);
    }
    return R.success("操作成功");
  }

  /**
   * 启售
   *
   * @param ids
   * @return
   */
  @ApiOperation("启售")
  @PostMapping("/status/1")
  public R<String> up(@RequestParam List<Long> ids) {
    for (Long id : ids) {
      Setmeal setmeal = setmealService.getById(id);
      setmeal.setStatus(1);
      setmealService.updateById(setmeal);
    }
    return R.success("操作成功");
  }
}
