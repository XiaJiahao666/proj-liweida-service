package com.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeys {

    public String prefix() {
        return "proj_liweida_service";
    }

    public String test() {
        return this.prefix() + ":test:";
    }

    /**
     * 钉钉令牌
     *
     * @param appKey appKey
     * @return accessToken
     */
    public String dingTalkAccessToken(String appKey) {
        return prefix() + String.format(":ding_talk:access_token:%s", appKey);
    }
}
