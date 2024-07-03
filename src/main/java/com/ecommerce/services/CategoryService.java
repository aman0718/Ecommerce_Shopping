package com.ecommerce.services;

import java.util.List;

import com.ecommerce.model.Category;

public interface CategoryService {

    public Category saveCategory(Category category);

    public boolean existCategory(String categoryName);

    public List<Category> getAllCategory();

    public Boolean deleteCategory(int id);

    public Category getCategoryById(int id);

    public Category updateCategory(int id);

    public List<Category> getAllActiveCategory();
    
    
}
