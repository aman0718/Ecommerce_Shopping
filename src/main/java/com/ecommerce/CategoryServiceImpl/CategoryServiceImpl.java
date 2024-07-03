package com.ecommerce.CategoryServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;



    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }


    @Override
    public List<Category> getAllCategory() {
         return categoryRepository.findAll();
    }

    @Override
    public boolean existCategory(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }
    

    @Override
    public Boolean deleteCategory(int id) {
        Category category = categoryRepository.findById(id).orElse(null);

        if(!ObjectUtils.isEmpty(category)){
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryRepository.findById(id).orElse(null);

    }

    @Override
    public Category updateCategory(int id) {
        // categoryRepository.set
        return null;
    }


    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories;
    }
    
}
