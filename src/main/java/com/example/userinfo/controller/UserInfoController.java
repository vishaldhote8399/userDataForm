package com.example.userinfo.controller;

import com.example.userinfo.model.UserInfo;
import com.example.userinfo.repository.UserInfoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserInfoController {

    private final UserInfoRepository repository;

    public UserInfoController(UserInfoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String form(Model model) {
        model.addAttribute("userInfo", new UserInfo());
        model.addAttribute("users", repository.findAll());
        return "index";
    }

    @PostMapping("/users")
    public String save(@Valid @ModelAttribute("userInfo") UserInfo userInfo,
                       BindingResult result,
                       Model model) {
        if (!result.hasErrors() && repository.existsByEmail(userInfo.getEmail())) {
            result.rejectValue("email", "duplicate", "Email already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("users", repository.findAll());
            return "index";
        }

        repository.save(userInfo);
        return "redirect:/?saved=true";
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "UP";
    }
}
