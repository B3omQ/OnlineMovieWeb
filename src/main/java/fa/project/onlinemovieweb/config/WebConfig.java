package fa.project.onlinemovieweb.config;

import fa.project.onlinemovieweb.entities.Role;
import fa.project.onlinemovieweb.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/assets/avatars/**")
                .addResourceLocations("file:resources/", "file:assets/avatars/")
                .setCachePeriod(0);

        registry
                .addResourceHandler("/assets/banners/**")
                .addResourceLocations("file:assets/banners/")
                .setCachePeriod(0);

        registry
                .addResourceHandler("/assets/posters/**")
                .addResourceLocations("file:assets/posters/")
                .setCachePeriod(0);
    }


    //Highlight this part out to allow admin access regardless of role
//    @Autowired
//    private AdminInterceptor adminInterceptor;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry){
//        registry
//                .addInterceptor(adminInterceptor)
//                .addPathPatterns("/admin/**");
//    }
    //End highlight

}

//Intercepts url access and checks if role is admin
@Component
class AdminInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if(request.getRequestURI().startsWith("/admin")){
            if(!isAdmin(request.getSession())){
                response.sendRedirect("/home");
                return false;
            }
        }
        return true;
    }

    private boolean isAdmin(HttpSession session){
        try{
            User user = (User) session.getAttribute("user");
            return user.getRole().equals(Role.ADMIN);
        }
        catch(Exception e){
            System.out.println("Can't access user");
        }
        return false;
    }
}
