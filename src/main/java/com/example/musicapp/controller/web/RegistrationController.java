package com.example.musicapp.controller.web;

import com.example.musicapp.dto.UserRegistrationDTO;
import com.example.musicapp.exception.DuplicateNameException;
import com.example.musicapp.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final IUserService userService;

    public RegistrationController(IUserService userService) {
        this.userService = userService;
    }

    // Hiển thị form đăng ký
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDTO());
        return "register"; // Trả về templates/register.html
    }

    // Xử lý đăng ký
    @PostMapping
    public String registerUser(
            @Valid @ModelAttribute("userDto") UserRegistrationDTO userDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. Kiểm tra lỗi validation cơ bản (NotBlank, Size)
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // 2. Gọi service để đăng ký
            userService.registerNewUser(userDto);

            // 3. Đăng ký thành công -> Chuyển hướng về login
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login"; // (Spring Security cung cấp /login)

        } catch (DuplicateNameException | IllegalArgumentException e) {
            // 4. Bắt lỗi (trùng tên, hoặc mật khẩu không khớp)
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
