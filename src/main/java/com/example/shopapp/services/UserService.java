package com.example.shopapp.services;

import com.example.shopapp.component.JwtTokenUtil;
import com.example.shopapp.dto.UpdateUserDTO;
import com.example.shopapp.dto.UserDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.PermissionDenyException;
import com.example.shopapp.model.Role;
import com.example.shopapp.model.User;
import com.example.shopapp.repositories.RoleRepository;
import com.example.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public User createUser(UserDTO userDTO) throws Exception {
        String userIdentifier = userDTO.getUserIdentifier();
        if(userRepository.existsByUserIdentifier(userIdentifier))
        {
            throw new DataIntegrityViolationException("Phone number already exists!");
        }
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .userIdentifier(userDTO.getUserIdentifier())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        Role role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(()->new DataNotFoundException("role not found"));
        //Don't allow register account with role admin
        if(role.getName().toUpperCase().equals("ADMIN"))
        {
            throw new PermissionDenyException("You can't not register account with role admin");
        }
        newUser.setRole(role);
        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);
        return userRepository.save(newUser);
    }


    public String login(String userIdentifier, String password,Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserIdentifier(userIdentifier);
        if(optionalUser.isEmpty())
        {
            throw new DataNotFoundException("Invalid phone number / password");
        }
        User existedUser = optionalUser.get();
        //check password
        if(existedUser.getGoogleAccountId()==0)
        {
            if(!passwordEncoder.matches(password,existedUser.getPassword()))
            {
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existedUser.getRole().getId())) {
            throw new DataNotFoundException("Consider your role !");
        }
        //check is active account
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userIdentifier,password,existedUser.getAuthorities());
        //authenticate with Java Spring Security
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return jwtTokenUtil.generateToken(existedUser); //return token
    }

    public Optional<User> getUserByUserIdentifier(String userIdentifier) throws Exception {
        return userRepository.findByUserIdentifier(userIdentifier);
    }

    public User getUserDetailFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token))
        {
            throw new Exception("Token is expired");
        }
        String id = jwtTokenUtil.extractIdUser(token);
        Optional<User> user = userRepository.findById(Long.valueOf(id));
        if(user.isPresent())
        {
            return user.get();
        }
        else {
            throw new Exception("User not found");
        }
    }
    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the phone number is being changed and if it already exists for another user
        String newUserIdentifier = updatedUserDTO.getUserIdentifier();
        if (!existingUser.getUserIdentifier().equals(newUserIdentifier) &&
                userRepository.existsByUserIdentifier(newUserIdentifier)) {
            throw new DataIntegrityViolationException("User identity already exists");
        }

        // Update user information based on the DTO
        if (updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (newUserIdentifier != null) {
            existingUser.setUserIdentifier(newUserIdentifier);
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }
        // Update the password if it is provided in the DTO
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser);
    }
}
