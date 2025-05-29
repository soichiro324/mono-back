package jp.co.monocrea.user.service;

import java.sql.SQLException;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jp.co.monocrea.user.entity.User;
import jp.co.monocrea.user.exception.DbRuntimeException;
import jp.co.monocrea.user.repository.UserRepository;

@ApplicationScoped
public class UserService {
    
    UserRepository userRepository;

    @Inject
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(String name) {
        try {
            Long id = userRepository.insertUser(name);
            return new User(id, name);
        } catch (SQLException e) {
            throw new DbRuntimeException("Insert failed", e);
        }
    }

    @Transactional
    public boolean updateUser(User user) {
        try {
            return userRepository.updatetUser(user);
        } catch (SQLException e) {
            throw new DbRuntimeException("Failed to update user", e);
        }
    }

    public List<User> searchUsers(
        Long id,
        String name,
        String sort,
        String order,
        int limit,
        int offset
    ) {        
        try {
            return userRepository.searchUsers(id, name, sort, order, limit, offset);
        } catch (SQLException e) {
            throw new DbRuntimeException("Query failed", e);
        } catch(Exception e) {
            throw e;
        }
    }

    public Long allUsersCount(
        Long id,
        String name
    ) {
        try {
            return userRepository.allUsersCount(id, name);
        } catch (SQLException e) {
            throw new DbRuntimeException("Query failed", e);
        }
    }

    @Transactional
    public boolean deleteUser(Long id) {
        try {
            return userRepository.deleteUser(id);
        } catch (SQLException e) {
            throw new DbRuntimeException("Failed to delete user", e);
        }
    }
}
