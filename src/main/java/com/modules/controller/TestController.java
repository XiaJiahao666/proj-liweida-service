package com.modules.controller;

import com.client.RedisClient;
import com.common.R;
import com.redis.RedisKeys;
import com.utils.SpringContextUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public R test() {
        return R.ok("Hello World!");
    }

    @GetMapping("redis")
    public R redis() {
        RedisClient redisClient = new RedisClient();
        RedisKeys redisKeys = new RedisKeys();
        redisClient.set(redisKeys.test(), "Hello World!");
        Object object = redisClient.get(redisKeys.test());
        return R.ok(object == null ? "" : object.toString());
    }

    @GetMapping("redis2")
    public R redis2() {
        RedisClient redisClient = SpringContextUtils.getBean(RedisClient.class);
        RedisKeys redisKeys = SpringContextUtils.getBean(RedisKeys.class);
        redisClient.set(redisKeys.test(), "Hello World!");
        Object object = redisClient.get(redisKeys.test());
        return R.ok(object == null ? "" : object.toString());
    }
}
