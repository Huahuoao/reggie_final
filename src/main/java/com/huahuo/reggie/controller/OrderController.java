package com.huahuo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huahuo.reggie.common.R;
import com.huahuo.reggie.entity.Orders;
import com.huahuo.reggie.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/** 订单 */
@Slf4j
@RestController
@RequestMapping("/order")
@Api("订单管理")
public class OrderController {

  @Autowired private OrderService orderService;

  /**
   * 用户下单
   *
   * @param orders
   * @return
   */
  @ApiOperation("用户下单")
  @PostMapping("/submit")
  public R<String> submit(@RequestBody Orders orders) {
    log.info("订单数据：{}", orders);
    orderService.submit(orders);
    return R.success("下单成功");
  }

  @ApiOperation("分页查询订单信息")
  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, Long number, Date beginTime, Date endTime) {
    // 构造分页构造器对象
    Page<Orders> pageInfo = new Page<>(page, pageSize);

    // 条件构造器
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    // 添加排序条件，根据sort进行排序
    queryWrapper.orderByAsc(Orders::getOrderTime);
    queryWrapper
        .eq(number != null, Orders::getId, number)
        .ge(beginTime != null, Orders::getOrderTime, beginTime)
        .le(endTime != null, Orders::getOrderTime, endTime);
    // 分页查询
    orderService.page(pageInfo, queryWrapper);
    return R.success(pageInfo);
  }

  @PutMapping
  R<String> send(@RequestBody Orders orders) {

    orders.setStatus(1);
    orderService.updateById(orders);
    return R.success("派送成功！");
  }
}
