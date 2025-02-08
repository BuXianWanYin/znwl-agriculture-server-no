//package com.frog.IaAgriculture.config;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.HandlerInterceptor;
//import com.frog.IaAgriculture.dto.ErrorCodeEnum;
//import com.frog.IaAgriculture.helper.JwtHelper;
//import com.frog.IaAgriculture.vo.ResultVO;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
///**
// 权限拦截器
// */
//@Configuration
//@Slf4j
//public class AuthInterceptor implements HandlerInterceptor {
//    private static final Logger logger = LoggerFactory.getLogger(HandlerInterceptor.class);
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
//        try {
//            if (request.getMethod().equals("OPTIONS")) {
//                return true;
//            }
//
//
//            //1.获取当前用户的请求头jwt信息
//            String token = request.getHeader("token");
//            if (StringUtils.isBlank(token)) {
//                token = request.getParameter("token");
//            }
//            //验证token
//            if (StringUtils.isBlank(token)) {
//                setReturn(response, "未登录");
//                return false;
//            }
//            Boolean verify = JwtHelper.isVerify(token);
//            if (!verify) {
//                setReturn( response, "登录已过期，请重新登陆");
//                return false;
//            }
//            //验证通过将信息存放到request中
//            String userId = JwtHelper.getUserId(token);
//            String userName = JwtHelper.getUserName(token);
//            String address = JwtHelper.getAddress(token);
//            request.setAttribute("userId",userId);
//            request.setAttribute("userName",userName);
//            request.setAttribute("address",address);
//            return true;
//        } catch (Exception e) {
//            logger.error("登录异常：" + e.getMessage());
//        }
//        setReturn(response, "未知错误");
//        return false;
//    }
//    //返回错误信息
//    private static void setReturn( HttpServletResponse httpServletResponse, String msg){
//        httpServletResponse.setContentType("application/json;charset=utf-8");
//        try {
//            httpServletResponse.getWriter().write(JSON.toJSONString(new ResultVO<>(null, ErrorCodeEnum.AUTH_ERROR.getCode(), msg)));
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//    }
//}
//
