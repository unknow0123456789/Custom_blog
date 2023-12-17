package com.example.CustomBlog.feedback;

import com.example.CustomBlog.user.Role;
import com.example.CustomBlog.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Feedback_Services {
    Feedback_Repository feedbackRepository;

    public Feedback_Services(Feedback_Repository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }
    public List<Feedback> GetFullFeedback(User user)
    {
        hasAdminPrivileges(user);
        return feedbackRepository.findAll();
    }
    @Transactional
    public Feedback addFeedback(User user,Feedback newFeedback)
    {
        user.getFeedbacks().add(newFeedback);
        newFeedback.setUser(user);
        return  feedbackRepository.save(newFeedback);
    }
    public Feedback GetFeedbackByID(User user,long id)
    {
        hasAdminPrivileges(user);
        return feedbackRepository
                .findById(id)
                .orElseThrow(
                        ()->new IllegalStateException(
                                "Feedback with id "+id+" does not exist in database!"
                        )
                );
    }
    @Transactional
    public void DeleteFeedbackByID(User user,long id)
    {
        Feedback feedback=GetFeedbackByID(user,id);
        feedback.getUser().getFeedbacks().remove(feedback);
        feedbackRepository.deleteById(id);
    }
    public void Cleartable(User user)
    {
        hasAdminPrivileges(user);
        feedbackRepository.deleteAll();
    }
    public boolean hasAdminPrivileges(User user)
    {
        if(user.getRole()!= Role.ADMIN) throw new IllegalStateException("Unauthorized User!");
        return true;
    }
}
