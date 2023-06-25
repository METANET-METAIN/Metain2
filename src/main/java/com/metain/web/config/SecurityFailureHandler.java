package com.metain.web.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Component
public class SecurityFailureHandler extends SimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler  {


//로그인 실패 시 오류메시지 출력
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage;


        if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "존재하지 않는 사번입니다.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof LockedException) { //인증 거부된 계정
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        } else {
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다. 관리자에게 문의하세요.";
        }
        saveException(request, exception);

        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");

        String loginUrl = "/loginEmp?error=" + errorMessage;

        response.sendRedirect(request.getContextPath() + loginUrl);

    }





}
