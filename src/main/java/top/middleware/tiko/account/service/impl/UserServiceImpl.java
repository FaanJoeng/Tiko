package top.middleware.tiko.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.middleware.tiko.account.dto.User;
import top.middleware.tiko.account.mapper.UserMapper;
import top.middleware.tiko.account.repository.UserRepository;
import top.middleware.tiko.account.service.UserService;
import top.middleware.tiko.security.TikoUserDetails;

import java.util.HashSet;
import java.util.Set;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Set<GrantedAuthority> authoritySet = new HashSet<>();
        User user = userMapper.selectUserByName(username);

        for (String roleCode : user.getRoles()) {
            authoritySet.add(new SimpleGrantedAuthority(roleCode));
        }

        authoritySet.add(new SimpleGrantedAuthority("USER"));

        return new TikoUserDetails(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), authoritySet, true, true, true, true);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(new User());
    }

    @Override
    public User save(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }
    }
}
