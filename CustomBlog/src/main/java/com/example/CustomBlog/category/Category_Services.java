package com.example.CustomBlog.category;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.CustomBlog.PutRequest;
import com.example.CustomBlog.content.Content;
import com.example.CustomBlog.user.Role;
import com.example.CustomBlog.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class Category_Services {
    private final Category_Repository category_Repository;

    public Category_Services(Category_Repository category_Repository) {
        this.category_Repository = category_Repository;
    }

    public List<Category> getFullCategory()
    {
        return category_Repository.findAll();
    }
    public Category GetCategoryByID(long id)
    {
        return category_Repository
                .findById(id)
                .orElseThrow(
                        ()->new IllegalStateException("Category with id "+id+" does not exist in database!")
                );
    }
    public void addCategories(List<Category> newCategories)
    {
        category_Repository.saveAll(newCategories);
    }
    public Category addCategory(User user, Category newCategory) {
        hasAdminPrivileges(user);
        category_Repository.save(newCategory);
        return category_Repository
                .findByName(newCategory.getName())
                .orElseThrow(
                        () -> new IllegalStateException("Unexpected error !")
                );
    }
    @Transactional
    public Category UpdateCategoryByID(User user,PutRequest putRequest) throws Exception
    {
        hasAdminPrivileges(user);
        String property=putRequest.getProperty();
        long id=putRequest.getItemId();
        Category category = category_Repository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalStateException("Category with id " + id + " does not exist in database!")
                );
        if(putRequest.getValue()!=null) {
            Class valueClass=putRequest.getValue().getClass();
            Method setter = category.getClass().getMethod(property, valueClass);
            setter.invoke(category, putRequest.getValue());
        }
        return category;
    }
    public void DeleteCategoryByID(User user,long id) {
        hasAdminPrivileges(user);
        Category delCategory = category_Repository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalStateException("Category with id " + id + " does not exist in database!")
                );
        for (Content content : delCategory.getIncludedContents()) {
            content.getCategories().remove(delCategory);
        }
        delCategory.getIncludedContents().clear();
        category_Repository.deleteById(delCategory.getId());
    }
    @Transactional
    public Content addContentToCategories(Content content,List<LinkedHashMap> objectList)
    {
        List<Long> categories_id=new ArrayList<>();
        for(LinkedHashMap obj : objectList)
        {
            Integer temp=(Integer)obj.get("id");
            categories_id.add(temp.longValue());
        }
        List<Category> categories=new ArrayList<>();

        for (long id : categories_id)
        {
            categories.add(
                    category_Repository
                            .findById(id)
                            .orElseThrow(
                                    ()->new IllegalStateException("Category with id "+id+" does not exist in database!")
                            ));
        }
        for(Category category : categories)
        {
            category.getIncludedContents().add(content);
        }
        content.getCategories().addAll(categories);
        return content;
    }

    public void Cleartable(User user)
    {
        hasAdminPrivileges(user);
        category_Repository.deleteAll();
    }
    public void hasAdminPrivileges(User user)
    {
        if(user.getRole()!=Role.ADMIN) throw new IllegalStateException("Unauthorized User!");
    }
}
