package com.zlead.mall.component;

import com.zlead.mall.dto.CommonResult;
import com.zlead.mall.util.DateUtil;
import com.zlead.mall.util.JsonUtil;
import com.zlead.mall.util.JwtTokenUtil;
import com.zlead.mall.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录授权过滤器
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring("Bearer ".length());
            if (redisUtil.hasToken(authToken)) {
                String username = jwtTokenUtil.getUserNameFromToken(authToken);
                logger.info("checking username:{}", username);
                String expirationTime = redisUtil.getExpirationTimeByToken(authToken).toString();
                String currentTime = DateUtil.getTime();
                if (DateUtil.compareDate(currentTime, expirationTime)) {//当前时间比过期时间小，token失效
                    //获得redis中用户的token刷新时效
                    String tokenValidTime = (String) redisUtil.getTokenValidTimeByToken(authToken);
                    if (DateUtil.compareDate(currentTime, tokenValidTime)) {
                        //超过有效期，不予刷新
                        logger.info("{}已超过有效期，不予刷新", authToken);
                        response.getWriter().println(JsonUtil.objectToJson(new CommonResult().unauthorized("token已失效")));
                        return;
                    } else {//仍在刷新时间内，则刷新token，放入请求头中
                        username = (String) redisUtil.getUsernameByToken(authToken);
                        String jwtToken = jwtTokenUtil.generateToken(username);
                        //更新redis
                        redisUtil.setTokenRefresh(jwtToken, username);
                        //删除旧token
                        redisUtil.deleteToken(authToken);
                        logger.info("redis已删除旧token：{},新token：{}已更新redis", authToken, jwtToken);
                        authToken = jwtToken;//更新token，为了后面
                        response.setHeader("Authorization", "Bearer " + jwtToken);
                    }
                }
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        logger.info("authenticated user:{}", username);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
