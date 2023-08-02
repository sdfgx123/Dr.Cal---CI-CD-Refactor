package com.fc.mini3server._core.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fc.mini3server._core.handler.exception.Exception401;
import com.fc.mini3server._core.utils.FilterResponse;
import com.fc.mini3server.domain.AuthEnum;
import com.fc.mini3server.domain.StatusEnum;
import com.fc.mini3server.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String prefixJwt = request.getHeader(JwtTokenProvider.HEADER);

        if (prefixJwt == null) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = prefixJwt.replace(JwtTokenProvider.TOKEN_PREFIX, "");

        try {
            DecodedJWT decodedJWT = JwtTokenProvider.verify(jwt);

            String username = decodedJWT.getClaim("username").asString();
            Long id = decodedJWT.getClaim("id").asLong();
            String status = decodedJWT.getClaim("status").asString();
            StatusEnum statusEnum = StatusEnum.valueOf(status);
            String auth = decodedJWT.getClaim("auth").asString();
            AuthEnum authEnum = AuthEnum.valueOf(auth);
            String password = decodedJWT.getClaim("password").asString();

            User user = User.builder().name(username)
                    .id(id)
                    .status(statusEnum)
                    .auth(authEnum)
                    .password(password)
                    .build();

            PrincipalUserDetail myUserDetails = new PrincipalUserDetail(user);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            myUserDetails,
                            myUserDetails.getPassword(),
                            myUserDetails.getAuthorities()
                    );
            PrincipalUserDetail userDetail = (PrincipalUserDetail) authentication.getPrincipal();

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (SignatureVerificationException sve) {
            FilterResponse.unAuthorized(response, new Exception401("권한이 없는 사용자 입니다."));
        } catch (TokenExpiredException tee) {
            FilterResponse.unAuthorized(response, new Exception401("토큰이 만료되었습니다."));
        } catch (JWTDecodeException exception) {
            FilterResponse.unAuthorized(response, new Exception401("토큰 검증에 실패 하였습니다."));
        }
    }
}
