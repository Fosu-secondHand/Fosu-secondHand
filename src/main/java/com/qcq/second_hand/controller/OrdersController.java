package com.qcq.second_hand.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/orders")
public class OrdersController {

}
