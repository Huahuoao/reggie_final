package com.huahuo.reggie.controller;

import com.huahuo.reggie.service.OrderDetailService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 订单明细 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
@Api("订单明细")
public class OrderDetailController {

  @Autowired private OrderDetailService orderDetailService;
}
