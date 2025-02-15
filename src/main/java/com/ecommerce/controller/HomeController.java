package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.User_Details;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // Whenever "home Controller" will be called, then this method will be executed
    // automatically coz of @ModelAttribute
    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {

        if (principal != null) {
            String email = principal.getName();
            User_Details userDetails = userService.getUserByEmail(email);
            model.addAttribute("user", userDetails);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("category", allActiveCategory);

    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("cat", categoryService.getAllActiveCategory());
        model.addAttribute("pro", productService.getAllProducts());
        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category) {

        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("paramValue", category);

        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model model) {

        Product productById = productService.getProductById(id);
        model.addAttribute("product", productById);

        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User_Details user, @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {

        String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

        user.setProfileImage(image);
        User_Details saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {

            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath()
                        + File.separator
                        + "profile_img"
                        + File.separator
                        + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Saved Successfully");
        } else
            session.setAttribute("errorMsg", "Something wrong on server");

        return "redirect:/register";
    }

}
