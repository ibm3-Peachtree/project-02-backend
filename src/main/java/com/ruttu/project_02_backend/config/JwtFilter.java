package com.ruttu.project_02_backend.config;

import com.ruttu.project_02_backend.service.auth.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Auth: " + request.getHeader("Authorization"));
        System.out.println("filter 실행 확인");

        String uri = request.getRequestURI();

        if(uri.equals("/auth/google")){
            filterChain.doFilter(request, response);
            return;
        }

        //System.out.println("header : " + request.getHeader(HttpHeaders.AUTHORIZATION));
        //Authorization 헤더에서 JWT 꺼내기
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("header: " + authHeader);
        String token = null;
        //Bearer로 시작하는 토큰인지 확인
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            //실제 JWT 문자열만 추출(7글자 뒤부터)
            token = authHeader.substring(7);
            //System.out.println(jwtUtil.validateToken(token));
            //토큰 유효성 검사
            if(jwtUtil.validateToken(token)) {
                //토큰에서 email을 꺼내기
                Long uId = jwtUtil.getUserIdFromToken(token);
                System.out.println("userId : " + uId);
                if (uId != null) {
                    //사용자 정보 로드(DB조회)
                    UserDetails userDetails = authService.loadUserById(uId);
                    System.out.println("role : " + userDetails.getAuthorities());
                    // 이미 인증된 사용자 표현용 토큰 생성
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    //지금 이 HTTP 요청은 authToken 사용자로 인증된 상태다, SecurityContext에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("FILTER FINAL AUTH: " + auth);
                System.out.println("IS AUTHENTICATED: " + auth.isAuthenticated());
            }

        }

        //현재 필터 처리를 끝내고, 다음 필터(또는 컨트롤러)로 요청을 전달
        //“이 요청을 다음 필터/컨트롤러로 계속 진행시켜라”
        filterChain.doFilter(request, response);

    }
}
