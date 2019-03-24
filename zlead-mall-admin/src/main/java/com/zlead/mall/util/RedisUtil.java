package com.zlead.mall.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
@Component
public class RedisUtil {

    @Value("${jwt.expiration:1800}")
    private int expiration;

    private String REDIS_TOKEN_HEADER = "zlead:mall:admin:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询key,支持模糊查询
     *
     * @param key 传过来时key的前后端已经加入了*，或者根据具体处理
     */
    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    /**
     * 字符串获取值
     *
     * @param key
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 字符串存入值
     * 默认过期时间为2小时
     *
     * @param key
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, 7200, TimeUnit.SECONDS);
    }

    /**
     * 字符串存入值
     *
     * @param expire 过期时间（毫秒计）
     * @param key
     */
    public void set(String key, Object value, Integer expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    /**
     * 删出key
     * 这里跟下边deleteKey（）最底层实现都是一样的，应该可以通用
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    /**
     * 添加单个
     * 默认过期时间为两小时
     *
     * @param key    key
     * @param filed  filed
     * @param domain 对象
     */
    public void hset(String key, String filed, Object domain) {
        redisTemplate.opsForHash().put(key, filed, domain);
    }

    /**
     * 添加单个
     *
     * @param key    key
     * @param filed  filed
     * @param domain 对象
     * @param expire 过期时间（毫秒计）
     */
    public void hset(String key, String filed, Object domain, Integer expire) {
        redisTemplate.opsForHash().put(key, filed, domain);
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 添加HashMap
     *
     * @param key key
     * @param hm  要存入的hash表
     */
    public void hset(String key, HashMap<String, Object> hm) {
        redisTemplate.opsForHash().putAll(key, hm);
    }

    /**
     * 如果key存在就不覆盖
     *
     * @param key
     * @param filed
     * @param domain
     */
    public void hsetAbsent(String key, String filed, Object domain) {
        redisTemplate.opsForHash().putIfAbsent(key, filed, domain);
    }

    /**
     * 查询key和field所确定的值
     *
     * @param key   查询的key
     * @param field 查询的field
     * @return HV
     */
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 查询该key下所有值
     *
     * @param key 查询的key
     * @return Map
     */
    public Object hget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除key下所有值
     *
     * @param key 查询的key
     */
    public void deleteKey(String key) {
        redisTemplate.opsForHash().getOperations().delete(key);
    }

    /**
     * 判断key和field下是否有值
     *
     * @param key   判断的key
     * @param field 判断的field
     */
    public Boolean hasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 判断key下是否有值
     *
     * @param key 判断的key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.opsForHash().getOperations().hasKey(key);
    }

    /**
     * 判断是否有token
     *
     * @param token 判断的key
     */
    public Boolean hasToken(String token) {
        token = REDIS_TOKEN_HEADER + token;
        return redisTemplate.opsForHash().getOperations().hasKey(token);
    }

    /**
     * 删除token
     *
     * @param token 判断的key
     */
    public Boolean deleteToken(String token) {
        token = REDIS_TOKEN_HEADER + token;
        return redisTemplate.opsForHash().getOperations().delete(token);
    }

    /**
     * 查询token下的刷新时间
     *
     * @param token 查询的key
     * @return HV
     */
    public Object getTokenValidTimeByToken(String token) {
        token = REDIS_TOKEN_HEADER + token;
        return redisTemplate.opsForHash().get(token, "tokenValidTime");
    }

    /**
     * 查询token下的过期时间
     *
     * @param token 查询的key
     * @return HV
     */
    public Object getExpirationTimeByToken(String token) {
        token = REDIS_TOKEN_HEADER + token;
        return redisTemplate.opsForHash().get(token, "expirationTime");
    }

    /**
     * 查询token下的登录名
     *
     * @param token 查询的key
     * @return HV
     */
    public Object getUsernameByToken(String token) {
        token = REDIS_TOKEN_HEADER + token;
        return redisTemplate.opsForHash().get(token, "username");
    }

    public void setTokenRefresh(String token, String username) {
        //刷新时间
        Integer expire = 24 * 60 * 60;
        token = REDIS_TOKEN_HEADER + token;
        hset(token, "tokenValidTime", DateUtil.addSecond(expire), expire);//token刷新时间,默认1天
        hset(token, "expirationTime", DateUtil.addSecond(expiration), expire);//token过期时间,默认半小时
        hset(token, "username", username, expire);
    }
}
