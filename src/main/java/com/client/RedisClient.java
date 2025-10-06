package com.client;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisClient {

    @Lazy
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 默认过期时长为24小时，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24L;

    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1L;

    public void set(String key, Object value, long expire) {
        try {
            redisTemplate.opsForValue().set(key, toJson(value));
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return;
        }
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    public Object get(String key, long expire) {
        Object value;
        try {
            value = redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return null;
        }
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
        return value;
    }

    public Object get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        Object value;
        try {
            value = redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return null;
        }
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
        return value == null ? null : fromJson(value.toString(), clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return new HashSet<>();
        }
    }

    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return 0L;
        }
    }

    public void deleteByPattern(String pattern) {
        try {
            redisTemplate.delete(keys(pattern));
        } catch (Exception e) {
            log.error("redis服务异常", e);
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("redis服务异常", e);
        }
    }

    public void delete(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("redis服务异常", e);
        }
    }

    public Object hGet(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return null;
        }
    }

    public Map<String, Object> hGetAll(String key) {
        try {
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            return hashOperations.entries(key);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return new HashMap<>();
        }
    }

    public void hMSet(String key, Map<String, Object> map) {
        hMSet(key, map, DEFAULT_EXPIRE);
    }

    public void hMSet(String key, Map<String, Object> map, long expire) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return;
        }
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }

    }

    public void hSet(String key, String field, Object value) {
        hSet(key, field, value, DEFAULT_EXPIRE);
    }

    public void hSet(String key, String field, Object value, long expire) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return;
        }
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }

    }

    public void expire(String key, long expire) {
        try {
            Boolean result = redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(result)) {
                expire(key, expire);
            }
        } catch (Exception e) {
            expire(key, expire);
        }
    }

    public void hDel(String key, Object... fields) {
        try {
            redisTemplate.opsForHash().delete(key, fields);
        } catch (Exception e) {
            log.error("redis服务异常", e);
        }
    }

    public void leftPush(String key, Object value) {
        leftPush(key, value, DEFAULT_EXPIRE);
    }

    public void leftPush(String key, Object value, long expire) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return;
        }
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public Object rightPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return null;
        }
    }

    public Boolean zSet(String key, String value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return false;
        }
    }

    public Long zDel(String key, Object... value) {
        try {
            return redisTemplate.opsForZSet().remove(key, value);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return 0L;
        }
    }

    public Set<Object> zRangeByScore(String key, long min, long max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return new HashSet<>();
        }
    }

    /**
     * 加分布式锁
     */
    public Boolean setNx(String track, String sector, long timeout) {
        String key;
        Boolean flag;
        try {
            key = track + sector;
            flag = redisTemplate.opsForValue().setIfAbsent(key, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("redis服务异常", e);
            return false;
        }
        // 如果成功设置超时时间, 防止超时
        if (Boolean.TRUE.equals(flag)) {
            expire(key, timeout);
        }
        return flag;
    }

    /**
     * 发布消息
     *
     * @param channel 通道
     * @param message 消息
     */
    public void convertAndSend(String channel, Object message) {
        try {
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("redis服务异常", e);
        }
    }

    /**
     * 删除锁
     */
    public void delete(String track, String sector) {
        try {
            redisTemplate.delete(track + sector);
        } catch (Exception e) {
            log.error("redis服务异常", e);
        }
    }

    /**
     * 计算两坐标的距离
     *
     * @param sourceLng 纬度
     * @param sourceLat 经度
     * @param targetLng 纬度
     * @param targetLat 经度
     * @return distance
     */
    public Integer distance(double sourceLng, double sourceLat, double targetLng, double targetLat) {
        Distance distance;
        try {
            GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
            //首先存入客户端上传的经纬度和指定地点的经纬度
            Map<Object, Point> map = new HashMap<>();
            map.put("ZB", new Point(sourceLat, sourceLng));
            map.put("GZ", new Point(targetLat, targetLng));
            // 将这些地址数据保存到redis中
            geoOperations.add("GET_DISTANCE", map);
            // 调用方法,计算之间的距离;
            distance = geoOperations.distance("GET_DISTANCE", "ZB", "GZ", RedisGeoCommands.DistanceUnit.METERS);
        } catch (Exception e) {
            return -1;
        }
        assert distance != null;
        return (int) distance.getValue();
    }

    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
}