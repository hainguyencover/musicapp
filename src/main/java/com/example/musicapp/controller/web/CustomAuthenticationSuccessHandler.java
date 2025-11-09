package com.example.musicapp.controller.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lớp này tùy chỉnh logic chuyển hướng SAU KHI đăng nhập thành công.
 * Nó sẽ kiểm tra vai trò (Role) của người dùng và chuyển đến dashboard tương ứng.
 */
@Component // Đánh dấu đây là một Spring Bean
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    /**
     * Quyết định URL mục tiêu dựa trên vai trò (Role)
     */
    protected String determineTargetUrl(Authentication authentication) {
        // Lấy danh sách các vai trò (ví dụ: "ROLE_ADMIN", "ROLE_USER")
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Ưu tiên ADMIN: Nếu có vai trò ADMIN, luôn về trang admin
        if (roles.contains("ROLE_ADMIN")) {
            return "/admin/dashboard"; // Đường dẫn đến Admin Dashboard
        }
        // Nếu không phải Admin nhưng có vai trò USER
        else if (roles.contains("ROLE_USER")) {
            return "/dashboard"; // Đường dẫn đến User Dashboard
        }

        // Mặc định (nếu user không có vai trò hợp lệ)
        return "/login?error=true";
    }
}
