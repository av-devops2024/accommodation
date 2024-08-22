package com.devops.accommodation.service.interfaces;

import ftn.devops.db.User;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {
    User findById(long id);
    User getUser(HttpServletRequest request);
}
