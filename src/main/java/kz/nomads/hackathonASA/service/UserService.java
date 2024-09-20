package kz.nomads.hackathonASA.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kz.nomads.hackathonASA.model.User;
import kz.nomads.hackathonASA.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public boolean login(User user, HttpServletRequest req) {
        User userCheck = userRepository.findByUsername(user.getUsername());
        if(userCheck!=null && userCheck.getPassword().equals(user.getPassword())){
            HttpSession session = req.getSession();
            session.setAttribute("currentUser", userCheck);
            return true;
        }
        return false;
    }

    public boolean isUnique(String username) {
        if(userRepository.findByUsername(username)==null){
            return true;
        }
        return false;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
