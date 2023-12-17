package com.example.CustomBlog.user;

import com.example.CustomBlog.PutRequest;
import com.example.CustomBlog.content.Comment;
import com.example.CustomBlog.content.Content;
import com.example.CustomBlog.feedback.Feedback;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service
public class User_Services {
    User_Repository userRepository;

    public User_Services(User_Repository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> GetFullUser()
    {
        return userRepository.findAll();
    }

    public Optional<User> GetUserByID(long id)
    {
        return userRepository.findById(id);
    }
    public Optional<User> GetUserByUsername(String username)
    {

        return userRepository.findByUsername(username);
    }

    public List<Content> GetUserContents(User user)
    {
        User selectedUser=userRepository.findById(user.getId()).orElseThrow(()->new IllegalStateException("User with id: "+user.getId()+" does not exist."));
        return selectedUser.getPosts();
    }

    @Transactional
    public User UpdateUserByID(User requestUser, PutRequest putRequest) throws Exception
    {
        String property=putRequest.getProperty();
        long id=putRequest.getItemId();
        User user = authorize_UserPrivileges(
                requestUser,
                id,
                ExceptionalPropertiesList(property));
        if(putRequest.getValue()!=null) {
            Class valueClass=putRequest.getValue().getClass();
            Method setter = user.getClass().getMethod(property, valueClass);
            setter.invoke(user, putRequest.getValue());
        }
        return user;
    }

    @Transactional
    public void DeleteUserByID(User requestUser,long delUser_id)
    {
        User user=authorize_UserPrivileges(
                requestUser,
                delUser_id,
                false
        );
        for(Feedback feedback:user.getFeedbacks())
        {
            feedback.setUser(null);
        }
        userRepository.deleteById(delUser_id);

    }
    private boolean ExceptionalPropertiesList(String property)
    {
        List<String> ExceptionalProperties=List.of("setId","setPassword");
        return ExceptionalProperties.contains(property);
    }
    private User authorize_UserPrivileges(User requestUser, long user_id, boolean AdminOverride)
    {
        User user=userRepository
                .findById(user_id)
                .orElseThrow(()->new IllegalStateException("User with id: "+ user_id +" does not exist."));
        if(AdminOverride && hasAdminPrivileges(requestUser)){
            return user;
        }
        if(requestUser==null||requestUser.getId()!=user.getId())
        {
            if(requestUser.getRole()==Role.ADMIN) return user;
            throw new IllegalStateException("Unauthorized User");
        }
        return user;
    }
    public void Cleartable(User user)
    {
        hasAdminPrivileges(user);
        userRepository.deleteAll();
    }
    public boolean hasAdminPrivileges(User user)
    {
        if(user.getRole()!= Role.ADMIN) throw new IllegalStateException("Unauthorized User!");
        return true;
    }
}
