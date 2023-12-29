package com.example.CustomBlog.Controllers;
import com.example.CustomBlog.Authentication.*;
import com.example.CustomBlog.Mail_Services;
import com.example.CustomBlog.PutRequest;
import com.example.CustomBlog.category.Category;
import com.example.CustomBlog.category.Category_Services;
import com.example.CustomBlog.content.Comment;
import com.example.CustomBlog.content.Comment_Services;
import com.example.CustomBlog.content.Content;
import com.example.CustomBlog.content.Content_Services;
import com.example.CustomBlog.feedback.Feedback;
import com.example.CustomBlog.feedback.Feedback_Services;
import com.example.CustomBlog.user.User;
import com.example.CustomBlog.user.User_Services;
import com.example.CustomBlog.web_information.Web_Information;
import com.example.CustomBlog.web_information.Web_Information_Services;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = ("/api"))
public class Rest_Controller {
    private final Authentication_Services authenticationServices;
    private final Content_Services contentServices;
    private final Category_Services categoryServices;
    private final User_Services userServices;
    private final Comment_Services commentServices;

    private final Web_Information_Services webInformationServices;
    private final Feedback_Services feedbackServices;

    private final EntityManager entityManager;
    private final Mail_Services mailServices;


    public Rest_Controller(Authentication_Services authenticationServices,
                           Content_Services contentServices,
                           Category_Services categoryServices,
                           User_Services userServices,
                           Comment_Services commentServices,
                           Web_Information_Services webInformationServices,
                           Feedback_Services feedbackServices,
                           EntityManager entityManager,
                           Mail_Services mailServices
    ) {
        this.authenticationServices = authenticationServices;
        this.contentServices = contentServices;
        this.categoryServices = categoryServices;
        this.userServices = userServices;
        this.commentServices = commentServices;
        this.webInformationServices = webInformationServices;
        this.feedbackServices = feedbackServices;
        this.entityManager=entityManager;
        this.mailServices=mailServices;
    }

    @GetMapping("/testing")
    public String test()
    {
        return "Server public is running";
    }

    @GetMapping("/test_token")
    public String test_token_valid()
    {
        return Get_CurrentSession_User().getUsername()+", YES this token can still be used !";
    }

    //Todo: Authenticate------
    @PostMapping(path = "/auth/register")
    public ResponseEntity<Object> register(
            @RequestBody RegisterRequest registerRequest
    )
    {
        try
        {
            return ResponseEntity.ok(authenticationServices.register(registerRequest));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(path = "/auth/adminRegister")
    public ResponseEntity<Object> adminRegister(
            @RequestBody RegisterRequest registerRequest,
            @RequestHeader(name = "Secret") String Secret
    )
    {
        try
        {
            String password="vjpPro@102";
            if(Secret.equals(password))
                return ResponseEntity.ok(authenticationServices.adminRegister(registerRequest));
            else
                return ResponseEntity.badRequest().body("Wrong Secret input");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/auth/authenticate")
    public ResponseEntity<Object> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    )
    {
        try {
            return ResponseEntity.ok(authenticationServices.authenticate(authenticationRequest));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping(path = "/auth/changePassword")
    public ResponseEntity<Object> ChangeUserPassword(
            @RequestBody ChangePasswordRequest changePasswordRequest
    )
    {
        try
        {
            return ResponseEntity.ok(authenticationServices.changeUserPassword(changePasswordRequest));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/auth/ForgotPassword")
    public ResponseEntity<Object> ForgetPassword(
            @RequestParam(name = "email") String email
    )
    {
        try
        {
            authenticationServices.forgotPasswordRequest(userServices.GetUserByEmail(email));
            return ResponseEntity.ok("A reset email have been sent to "+email+".");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/auth/ResetPassword")
    public ResponseEntity<Object> ResetForgottenPassword(
            @RequestBody ChangePasswordRequest changePasswordRequest
    )
    {
        try
        {
            authenticationServices.resetForgottenPassword(changePasswordRequest);
            return ResponseEntity.ok("Password changed");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Authenticate------

    //Todo: Content------
    @GetMapping(path = "/guest/Content")
    public ResponseEntity<Object> GetContentByID(
            @RequestParam(name = "id") Long id
    )
    {
        try
        {
            return ResponseEntity.ok(contentServices.GetContentByID(id,true));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "/guest/Content/Search")
    public ResponseEntity<Object> GetContentBySearch(
            @RequestParam(name = "search") String search
    )
    {
        try
        {
            Optional<Category> category=categoryServices.SearchByName(search);
            if(category.isPresent()) return ResponseEntity.ok(category);
            return ResponseEntity.ok(contentServices.SearchContentTitleContaining(search));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "/guest/Content/TopViews")
    public ResponseEntity<Object> GetContentsByViews()
    {
        try {
            return ResponseEntity.ok(contentServices.GetTop30ByViews());
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "guest/Content/TopDate")
    public ResponseEntity<Object> GetContentsByDate()
    {
        try
        {
            return ResponseEntity.ok(contentServices.GetTop30ByDate());
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "/guest/Content/All")
    public ResponseEntity<Object> GetAllApprovedContents()
    {
        try
        {
            return ResponseEntity.ok(contentServices.GetAllApprovedContent());
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "/Content/Pending")
    public ResponseEntity<Object> GetAllPendingContents()
    {
        try
        {
            return ResponseEntity.ok(contentServices.GetAllPendingContent(Get_CurrentSession_User()));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(path = "/Content")
    public ResponseEntity<Object> PostContent(
            @RequestBody Content content
            )
    {
        try{
            return ResponseEntity.ok(contentServices.addContent(Get_CurrentSession_User(),content));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "/Content")
    public List<Content> GetUserContent()
    {
        return userServices.GetUserContents(Get_CurrentSession_User());
    }
    @PutMapping(path = "/Content")
    public ResponseEntity<Object> PutContent(
            @RequestBody PutRequest putRequest
            )
    {
        try
        {
            return ResponseEntity.ok(contentServices.UpdateContentByID(Get_CurrentSession_User(),putRequest));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping(path = "/Content/Categories")
    public ResponseEntity<Object> PutContentCategories(
            @RequestBody PutRequest putRequest
    )
    {
        try
        {
            Content content=contentServices
                    .authorize_UserContent(
                            Get_CurrentSession_User(),
                            putRequest.getItemId(),
                            false);
            return  ResponseEntity.ok(categoryServices.addContentToCategories(content,(List<LinkedHashMap>)putRequest.getValue()));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @DeleteMapping(path = "/Content")
    public ResponseEntity<Object> DeleteContent(
            @RequestParam(required = true,name = "id") long id
    )
    {
        try{
            return ResponseEntity.ok(contentServices.DeleteContentByID(Get_CurrentSession_User(),id));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Content------

    //Todo: Category-----
    @PostMapping(path = "/Category")
    public ResponseEntity<Object> PostCategory(
            @RequestBody Category category
            )
    {
        try{
            return ResponseEntity.ok(categoryServices.addCategory(Get_CurrentSession_User(),category));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping(path = "/Category")
    public ResponseEntity<Object> PutCategory(
            @RequestBody PutRequest putRequest
    )
    {
        try
        {
            System.out.println("\n\n\tDEBUG LOGGING  "+putRequest+"\n\n\n");
            return ResponseEntity.ok(categoryServices.UpdateCategoryByID(Get_CurrentSession_User(),putRequest));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping(path = "/Category")
    public ResponseEntity<Object> DeleteCategory(
            @RequestParam(required = true,name = "id") long id
    )
    {
        try
        {
            categoryServices.DeleteCategoryByID(Get_CurrentSession_User(),id);
            return ResponseEntity.ok("Deleted");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "/guest/Categories")
    public List<Category> GetCategories()
    {
        return categoryServices.getFullCategory();
    }
    @GetMapping(path = "/guest/Category")
    public ResponseEntity<Object> GetCategoryByID(
            @RequestParam(name = "categoryId",required = true) long id
    )
    {
        try
        {
            return ResponseEntity.ok(categoryServices.GetCategoryByID(id));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Category-----

    //Todo: Comment------
    @PostMapping(path = "/Comment")
    public ResponseEntity<Object> PostComment(
            @RequestBody Comment comment
            )
    {
        try
        {
            return ResponseEntity.ok(commentServices.addComment_to_Content(Get_CurrentSession_User(),contentServices.GetContentByID(comment.getPost().getId(),false),comment));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping(path = "/Comment")
    public ResponseEntity<Object> PutComment(
            @RequestBody PutRequest putRequest
    )
    {
        try
        {
            return ResponseEntity.ok(commentServices.UpdateCommentByID(Get_CurrentSession_User(),putRequest));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping(path = "/Comment")
    public ResponseEntity<Object> DeleteComment(
            @RequestParam(name = "id") long id
    )
    {
        try
        {
            commentServices.DeleteCommentById(Get_CurrentSession_User(),id);
            return ResponseEntity.ok().body("Deleted");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Comment------


    //Todo: User--------
    @GetMapping(path = "/User")
    public User Get_CurrentSession_User()
    {
        return userServices.GetUserByUsername(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        )
                .orElseThrow(()->new IllegalStateException("Unauthorized User!"));
    }

    @GetMapping(path = "/guest/User")
    public ResponseEntity<Object> Get_User_By_Username(
            @RequestParam(name = "username",required = true) String username
    )
    {
        try
        {
            return ResponseEntity.ok(userServices.GetUserByUsername(username));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/User/All")
    public ResponseEntity<Object> GetAllUser()
    {
        try
        {
            return ResponseEntity.ok(userServices.GetAllUser(Get_CurrentSession_User()));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(path = "/User")
    public ResponseEntity<Object> PutUser(
            @RequestBody PutRequest putRequest
    )
    {
        try
        {
            return ResponseEntity.ok(userServices.UpdateUserByID(
                    Get_CurrentSession_User(),
                    putRequest
            ));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(path = "/User")
    public ResponseEntity<Object> DeleteUser(
            @RequestParam(name = "id") long id
    )
    {
        try
        {
            userServices.DeleteUserByID(Get_CurrentSession_User(),id);
            return ResponseEntity.ok().body("Deleted");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //User--------

    //Todo: WebInformation-------------
    @GetMapping(path = "/guest/WI/all")
    public ResponseEntity<Object> GetFullWebInformation()
    {
        return ResponseEntity.ok(
                webInformationServices.GetFullWeb_Information()
        );
    }

    @GetMapping(path = "/guest/WI")
    public ResponseEntity<Object> GetWebInformation(
            @RequestParam(name = "name",required = false) String name,
            @RequestParam(name = "id",required = false) Long id
    )
    {
        try
        {
            if(id!=null)
                return ResponseEntity.ok(
                        webInformationServices.GetWeb_InformationByID(
                                id
                        )
                );
            else if(name!=null)
                return ResponseEntity.ok(
                        webInformationServices.GetWeb_InformationByName(
                                name
                        )
                );
            else
                return ResponseEntity.badRequest().body("Missing Required Parameter (name/id)");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(path = "/WI")
    public ResponseEntity<Object> PostWeb_Information(
            @RequestBody Web_Information webInformation
            )
    {
        try
        {
            return ResponseEntity.ok(webInformationServices.addWeb_Information(
                    Get_CurrentSession_User(),
                    webInformation
            ));
        }
        catch (Exception e)
        {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(path = "/WI")
    public ResponseEntity<Object> PutWeb_Information(
            @RequestBody PutRequest putRequest
    )
    {
        try
        {
            return ResponseEntity.ok(
                    webInformationServices
                            .UpdateWeb_InformationByID(
                                    Get_CurrentSession_User(),
                                    putRequest
                            )
            );
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping(path = "/WI")
    public ResponseEntity<Object> DeleteWebInformation(
            @RequestParam(required = true,name = "id") Long id
    )
    {
        try
        {
            webInformationServices.Delete_Web_InformationByID(Get_CurrentSession_User(),id);
            return ResponseEntity.ok().body("Deleted");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //WebInformation-------------

    //Todo: Feedback---------------------
    @GetMapping(path = "/Feedback/all")
    public ResponseEntity<Object> GetFeedbacks()
    {
        try
        {
            return ResponseEntity.ok(feedbackServices.GetFullFeedback(Get_CurrentSession_User()));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping(path = "/Feedback")
    public ResponseEntity<Object> GetFeedbackById(
            @RequestParam(name = "id",required = true) Long id
    )
    {
        try
        {
            return ResponseEntity.ok(feedbackServices.GetFeedbackByID(Get_CurrentSession_User(),id));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(path = "/Feedback")
    public ResponseEntity<Object> PostFeedback(
            @RequestBody Feedback feedback
            )
    {
        try
        {
            return ResponseEntity.ok(feedbackServices.addFeedback(Get_CurrentSession_User(),feedback));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping(path = "/Feedback")
    public ResponseEntity<Object> DeleteFeedbackByID(
            @RequestParam(name = "id",required = true) Long id
    )
    {
        try
        {
            feedbackServices.DeleteFeedbackByID(Get_CurrentSession_User(),id);
            return ResponseEntity.ok("Deleted");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Feedback---------------------

    @DeleteMapping(path = "/All")
    @Transactional
    public ResponseEntity<Object> ClearDatabase()
    {
        try
        {
            User user=Get_CurrentSession_User();
            contentServices.Cleartable(user);
            categoryServices.Cleartable(user);
            webInformationServices.Cleartable(user);
            feedbackServices.Cleartable(user);
            userServices.Cleartable(user);
            dropAllTables();
            return ResponseEntity.ok().body("Clear");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    public void dropAllTables() {
        List<String> tableNames = List.of(
                "asset",
                "web_information",
                "content_category",
                "feedback",
                "category",
                "comment",
                "content",
                "_users"
        );

        for (String tableName : tableNames) {
            String sql = "DROP TABLE IF EXISTS " + tableName+" CASCADE";
            entityManager.createNativeQuery(sql).executeUpdate();
        }
    }
}
