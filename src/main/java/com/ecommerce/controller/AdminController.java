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
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

     @Autowired
    private UserService userService; 



    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {

        if (principal != null) {
            String email = principal.getName();
            User_Details userDetails = userService.getUserByEmail(email);
            model.addAttribute("user", userDetails);
        }
    }

    
    @GetMapping("")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/addProduct")
    public String addProduct(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/addCategory")
    public String addCategory(Model model) {
        model.addAttribute("category", categoryService.getAllCategory());
        return "admin/category";
    }

    @SuppressWarnings("null")
    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
            HttpSession httpSession) throws IOException {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existCategory = categoryService.existCategory(category.getName());

        if (existCategory) {
            httpSession.setAttribute("errorMsg", "category name already exist");
        } else {
            Category saveCategory = categoryService.saveCategory(category);

            if (ObjectUtils.isEmpty(saveCategory)) {
                httpSession.setAttribute("errorMsg", "Not saved!");
            } else {

                File saveFile = new ClassPathResource("static/img/category_img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()
                        + File.separator
                        + imageName);

                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                httpSession.setAttribute("succMsg", "saved scccessfully");
            }
        }
        return "redirect:/admin/addCategory";
    }

    @GetMapping("/deleteCategory/{id}")
    public String removeCategory(@PathVariable int id, HttpSession session) {

        Boolean categoryDeleted = categoryService.deleteCategory(id);

        if (categoryDeleted)
            session.setAttribute("succMsg", "Category Removed Successfully !");
        else
            session.setAttribute("errorMsg", "Something Wrong !");

        return "redirect:/admin/addCategory";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {

        Category oldCategory = categoryService.getCategoryById(category.getId());
        String fileName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

        if (!ObjectUtils.isEmpty(oldCategory)) {
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(fileName);
        }
        Category updateCategory = categoryService.saveCategory(oldCategory);

        if (!ObjectUtils.isEmpty(updateCategory)) {

            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img/category_img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()
                        + File.separator
                        + fileName);

                // System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Category update successfully!");
        } else
            session.setAttribute("errorMsg", "Category update unsuccessfull!");

        return "redirect:/admin/addCategory";
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRODUCT MODULE //

    @PostMapping("/saveProduct")
    public String SaveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
            HttpSession session) throws IOException {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product saveProduct = productService.saveProduct(product);

        if (saveProduct != null) {
            File saveFile = new ClassPathResource("static/img/product").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath()
                    + File.separator
                    + imageName);

            // System.out.println("Destination path: " + path);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("succMsg", "Product saved successfully");

            // return "redirect:/admin/viewProduct";

        } else {
            session.setAttribute("errorMsg", "Something Wrong!");
        }

        return "redirect:/admin/addProduct";
    }

    @GetMapping("/viewProduct")
    public String viewProduct(Model model) {

        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String removeProduct(@PathVariable int id, HttpSession session) {
        Boolean deleteProd = productService.deleteProduct(id);

        if (deleteProd) {
            session.setAttribute("succMsg", "Product removed successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong");
        }

        return "redirect:/admin/viewProduct";

    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
            HttpSession session) {

        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "Invalid Discount");
        } else {
            Product updateProduct = productService.updateProduct(product, image);

            if (!ObjectUtils.isEmpty(updateProduct)) {
                session.setAttribute("succMsg", "Product update successfully");
            } else {
                session.setAttribute("errorMsg", "something wrong on server");
            }
        }
        return "redirect:/admin/viewProduct";

    }

}
