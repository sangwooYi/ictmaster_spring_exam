package com.example.demo.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    /**
     *  쿠키 생성
     */
    public Cookie makeCookie(String cookieName, String cookieVal, String path, Integer maxAge) {

        Cookie cookie = new Cookie(cookieName, cookieVal);

        cookie.setPath(path);
        cookie.setMaxAge(maxAge);

        return cookie;
    }

    /**
     *
     * 쿠키 값 가져오기
     *
     */
    public String getCookieValue(HttpServletRequest request, String cookieName) {

        String cookieValue = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(cookieName)) {
                cookieValue = cookie.getValue();
                break;
            }
        }
        return cookieValue;
    }
    /**
     * 쿠키 존재여부 파악
     * @param request
     * @param cookieName
     * @return
     */
    public boolean isHasCookies(HttpServletRequest request, String cookieName) {

        boolean isExist = false;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(cookieName)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * 값 일치까지 확인
     * @param request
     * @param cookieName
     * @param cookieValue
     * @return
     */
    public boolean isHasCookies(HttpServletRequest request, String cookieName, String cookieValue) {

        boolean isExist = false;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            // 실제 유저 ID와 동일한지도 체크 , 아니면 로그인화면으로 리다이렉트
            if (cookie.getName().equals(cookieName) && cookie.getValue().equals(cookieValue)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

}
