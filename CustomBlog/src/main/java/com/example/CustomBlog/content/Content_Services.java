package com.example.CustomBlog.content;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

import com.example.CustomBlog.PutRequest;
import com.example.CustomBlog.asset.Asset;
import com.example.CustomBlog.category.Category;
import com.example.CustomBlog.user.Role;
import com.example.CustomBlog.user.User;
import com.example.CustomBlog.user.User_Repository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class Content_Services {
    private final Content_Repository content_Repository;
    private final User_Repository userRepository;

    public Content_Services(Content_Repository content_Repository, User_Repository userRepository) {
        this.content_Repository = content_Repository;
        this.userRepository = userRepository;
    }

    public List<Content> GetFullContent ()
    {
        return content_Repository.findAll();
    }

    public List<Content> SearchContentTitleContaining(String search)
    {
        return content_Repository.findByTitleContainingIgnoreCaseAndPendingIsFalse(search);
    }
    public List<Content> GetTop30ByViews()
    {
        return content_Repository.findTop30ByPendingFalseOrderByViewsDesc();
    }
    public List<Content> GetTop30ByDate()
    {
        return content_Repository.findTop30ByPendingFalseOrderByLastUploadDesc();
    }
    public List<Content> GetAllPendingContent(User user)
    {
        hasAdminPrivileges(user);
        return content_Repository.findByPendingIsTrue();
    }
    public List<Content> GetAllApprovedContent()
    {
        return content_Repository.findByPendingIsFalse();
    }
    public void Cleartable(User user)
    {
        hasAdminPrivileges(user);
        content_Repository.deleteAll();
    }
    public Content addContent(User Author,Content newContent) throws Exception
    {
        if(newContent.getAssetList()!=null && !newContent.getAssetList().isEmpty())
        {
            for(Asset asset:newContent.getAssetList())
            {
                asset.setFrom_content(newContent);
            }
        }
        newContent.setPending(true);
        newContent.setViews(0);
        newContent.setLastUpload(GetFormatCurrentDateTime());
        newContent.setAuthor(Author);
        Author.addPost(newContent);
        userRepository.save(Author);
        return newContent;
    }
    @Transactional
    public Content GetContentByID(long id,boolean viewCount)
    {
        Content content=content_Repository
                .findById(id)
                .orElseThrow(
                        ()-> new IllegalStateException("Content with id "+ id +" does not exist in database!")
                );
        if(viewCount) content.setViews(content.getViews()+1);
        return content;
    }
    @Transactional
    public Content UpdateContentByID(User owner,PutRequest putRequest) throws Exception
    {
        String property=putRequest.getProperty();
        long id=putRequest.getItemId();
        Content content= authorize_UserContent(
                owner,
                id,
                ExceptionalPropertiesList(owner,property)
        );

        content.setLastUpload(GetFormatCurrentDateTime());

        if(putRequest.getValue()!=null) {
            Class valueClass=putRequest.getValue().getClass();
            Method setter = content.getClass().getMethod(property, valueClass);
            setter.invoke(content, putRequest.getValue());
        }
        return content;
    }

    @Transactional
    public List<Content> DeleteContentByID(User owner,long id)
    {
        Content content= authorize_UserContent(owner,id,false);
        for(Category category:content.getCategories())
        {
            category.getIncludedContents().remove(content);
        }
        content.getAuthor().getPosts().remove(content);
        content_Repository.deleteById(id);
        return userRepository.findById(owner.getId()).get().getPosts();
    }
    private boolean ExceptionalPropertiesList(User user,String property)
    {
        List<String> ExceptionalProperties=List.of("setViews","setPending","setAuthor","setId","setLastUpload");
        return ExceptionalProperties.contains(property);
    }

    public Content authorize_UserContent(User user, long content_id,boolean AdminOverride)
    {
        Content content=content_Repository.findById(content_id).orElseThrow(()->new IllegalStateException("Content with id: "+content_id+" does not exist."));
        if(AdminOverride && hasAdminPrivileges(user))
            return content;
        if(user==null||!user.getPosts().contains(content))
        {
            if(user.getRole()==Role.ADMIN) return content;
            throw new IllegalStateException("Unauthorized User");
        }
        return content;
    }

    public Date GetFormatCurrentDateTime()
    {
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        return simpleDateFormat.format(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance(timeZone);
        return calendar.getTime();
    }
    public boolean hasAdminPrivileges(User user)
    {
        if(user.getRole()!= Role.ADMIN) throw new IllegalStateException("Unauthorized User!");
        return true;
    }
}
