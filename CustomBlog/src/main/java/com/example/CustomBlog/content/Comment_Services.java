package com.example.CustomBlog.content;

import com.example.CustomBlog.PutRequest;
import com.example.CustomBlog.user.Role;
import com.example.CustomBlog.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

@Service
public class Comment_Services {
    final private Comment_Repository commentRepository;

    public Comment_Services(Comment_Repository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Content addComment_to_Content(User user,Content content,Comment newComment)
    {
        user.getComments().add(newComment);
        content.getComments().add(newComment);
        newComment.setPost(content);
        newComment.setUser(user);
        commentRepository.save(newComment);
        return content;
    }
    @Transactional
    public Comment UpdateCommentByID(User user, PutRequest putRequest) throws Exception
    {
        String property=putRequest.getProperty();
        long id=putRequest.getItemId();
        Comment comment= authorize_UserComment(
                user,
                id,
                ExceptionalPropertiesList(user,property));
        if(putRequest.getValue()!=null) {
            Class valueClass=putRequest.getValue().getClass();
            Method setter = comment.getClass().getMethod(property, valueClass);
            setter.invoke(comment, putRequest.getValue());
        }
        return comment;
    }
    @Transactional
    public void DeleteCommentById(User user,long id)
    {
        Comment comment=authorize_UserComment(user,id,false);
        comment.getPost().getComments().remove(comment);
        comment.getUser().getComments().remove(comment);
        comment.setPost(null);
        comment.setUser(null);
        commentRepository.deleteById(id);
    }
    private boolean ExceptionalPropertiesList(User user,String property)
    {
        List<String> ExceptionalProperties=List.of("setId","setPost","setUser");
        return ExceptionalProperties.contains(property);
    }
    private Comment authorize_UserComment(User user, long comment_id, boolean AdminOverride)
    {
        Comment comment=commentRepository
                .findById(comment_id)
                .orElseThrow(()->new IllegalStateException("Comment with id: "+ comment_id +" does not exist."));
        if(AdminOverride && hasAdminPrivileges(user)){
            return comment;
        }
        if(user==null||!user.getComments().contains(comment))
        {
            if(user.getRole()==Role.ADMIN) return comment;
            throw new IllegalStateException("Unauthorized User");
        }
        return comment;
    }
    public boolean hasAdminPrivileges(User user)
    {
        if(user.getRole()!= Role.ADMIN) throw new IllegalStateException("Unauthorized User!");
        return true;
    }
}
