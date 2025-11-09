package com.example.musicapp.controller.admin;

import com.example.musicapp.dto.UserDTO;
import com.example.musicapp.entity.User;
import com.example.musicapp.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model, @PageableDefault(page = 0, size = 5, sort = "username") Pageable pageable) {
        logger.info("Admin request: Lấy danh sách người dùng.");

        // 1. Lấy Page<User> từ service
        Page<User> userPageEntity = userService.findAll(pageable);

        // 2. Chuyển đổi Page<User> sang Page<UserViewDTO> (Quan trọng: để ẩn password)
        List<UserDTO> dtoList = userPageEntity.getContent().stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEnabled(user.isEnabled());
                    dto.setRoles(user.getRoles());
                    return dto;
                })
                .collect(Collectors.toList());

        Page<UserDTO> userPageDTO = new PageImpl<>(dtoList, pageable, userPageEntity.getTotalElements());

        // 3. Đưa Page DTO ra view
        model.addAttribute("userPage", userPageDTO);

        return "admin/user/list"; // -> templates/admin/user/list.html
    }
}
