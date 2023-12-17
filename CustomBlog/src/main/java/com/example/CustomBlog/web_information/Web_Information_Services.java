package com.example.CustomBlog.web_information;

import com.example.CustomBlog.PutRequest;
import com.example.CustomBlog.asset.Asset;
import com.example.CustomBlog.category.Category;
import com.example.CustomBlog.user.Role;
import com.example.CustomBlog.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service
public class Web_Information_Services {
    Web_Information_Repository webInformationRepository;

    public Web_Information_Services(Web_Information_Repository webInformationRepository) {
        this.webInformationRepository = webInformationRepository;
    }
    public List<Web_Information> GetFullWeb_Information(User user)
    {
        hasAdminPrivileges(user);
        return webInformationRepository.findAll();
    }

    public Web_Information addWeb_Information(User user,Web_Information newWI) {
        hasAdminPrivileges(user);
        if (newWI.getAssetList() != null && !newWI.getAssetList().isEmpty()) {
            for (Asset asset : newWI.getAssetList()) {
                asset.setFrom_webinfo(newWI);
            }
        }
        webInformationRepository.save(newWI);
        return  webInformationRepository.findByName(newWI.getName())
                .orElseThrow(
                        ()->new IllegalStateException("Unexpected Error!")
                );
    }
    @Transactional
    public Web_Information UpdateWeb_InformationByID(User user, PutRequest putRequest) throws Exception
    {
        hasAdminPrivileges(user);
        String property=putRequest.getProperty();
        long id=putRequest.getItemId();
        Web_Information webInformation = webInformationRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalStateException("Web_Information with id " + id + " does not exist in database!")
                );
        if(putRequest.getValue()!=null) {
            Class valueClass=putRequest.getValue().getClass();
            Method setter = webInformation.getClass().getMethod(property, valueClass);
            setter.invoke(webInformation, putRequest.getValue());
        }
        return webInformation;
    }
    public void Delete_Web_InformationByID(User user,long id)
    {
        hasAdminPrivileges(user);
        Web_Information webInformation = webInformationRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalStateException("Web_Information with id " + id + " does not exist in database!")
                );
        webInformationRepository.deleteById(id);
    }
    public Web_Information GetWeb_InformationByID(User user,long id)
    {
        hasAdminPrivileges(user);
        return  webInformationRepository
                .findById(id)
                .orElseThrow(
                        ()->new IllegalStateException("Web information with id "+id+" does not exist in database!")
                );
    }
    public Web_Information GetWeb_InformationByName(User user,String name)
    {
        hasAdminPrivileges(user);
        return  webInformationRepository
                .findByName(name)
                .orElseThrow(
                        ()->new IllegalStateException("Web information with name "+name+" does not exist in database!")
                );
    }
    public void Cleartable(User user)
    {
        hasAdminPrivileges(user);
        webInformationRepository.deleteAll();
    }
    public boolean hasAdminPrivileges(User user)
    {
        if(user.getRole()!= Role.ADMIN) throw new IllegalStateException("Unauthorized User!");
        return true;
    }
}
