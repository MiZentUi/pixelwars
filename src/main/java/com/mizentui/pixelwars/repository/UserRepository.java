package com.mizentui.pixelwars.repository;

import com.mizentui.pixelwars.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByStatus(String status);

    User findByEmailAndName(String email, String name);

}
