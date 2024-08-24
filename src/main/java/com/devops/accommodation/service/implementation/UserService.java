package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.UserRepository;
import com.devops.accommodation.service.interfaces.IUserService;
import com.devops.accommodation.utils.Constants;
import ftn.devops.db.User;
import ftn.devops.log.LogType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Enumeration;

@Service
public class UserService implements IUserService {
    @Autowired
    private LogClientService logClientService;
    @Autowired
    private UserRepository hostRepository;

    @Value("${devops.auth.url}")
    private String authUrl;

    @Override
    public User findById(long id) {
        return hostRepository.findById(id)
                .orElseThrow(() -> {
                    logClientService.sendLog(LogType.WARN, "Host not found", id);
                    throw new EntityNotFoundException(Constants.HOST_NOT_FOUND);
                });
    }

    @Override
    public User getUser(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        // Extract the token from the request (assuming it's stored in a header or session)
        String authToken = request.getHeader("Authorization");

        // Set up headers with the token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        // Create HttpEntity with headers (body can be null if not needed)
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Perform the HTTP request
        ResponseEntity<User> result =
                restTemplate.exchange(authUrl + "/logged-user", HttpMethod.GET, entity, User.class);

        // Return the User object from the response
        return result.getBody();
    }
}
