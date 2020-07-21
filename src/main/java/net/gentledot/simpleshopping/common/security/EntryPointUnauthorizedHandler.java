package net.gentledot.simpleshopping.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.simpleshopping.models.response.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResult unauthorizedError = ApiResult.error("Authentication error : unauthorized", HttpStatus.UNAUTHORIZED);

        ServletOutputStream outputStream = response.getOutputStream();

        // 응답 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("content-type", MediaType.APPLICATION_JSON_VALUE);

        // output에 ApiResult.error 객체 출력
        objectMapper.writeValue(outputStream, unauthorizedError);

        outputStream.flush();
        outputStream.close();
    }
}
