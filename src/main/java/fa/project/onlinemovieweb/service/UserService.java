package fa.project.onlinemovieweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fa.project.onlinemovieweb.entities.User;
import fa.project.onlinemovieweb.repo.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public boolean verifyUser(String token) {
        User user = userRepo.findByVerificationToken(token);
        if (user != null) {
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepo.save(user);
            return true;
        }
        return false;
    }
}