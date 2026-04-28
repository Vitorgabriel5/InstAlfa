package tinterPJ.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.UpdateUserRequest;
import tinterPJ.demo.dto.UserResponseDTO;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.FollowRepository;
import tinterPJ.demo.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    final FollowRepository followRepository;
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

    public long getFollowersCount(UUID userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long getFollowingCount(UUID userId) {
        return followRepository.countByFollowerId(userId);
    }

    public User create(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public UserResponseDTO toResponse(User user, UUID currentUserId) {

        boolean isFollowing = false;

        if (currentUserId != null) {
            isFollowing = followRepository
                    .existsByFollowerIdAndFollowingId(currentUserId, user.getId());
        }

        return new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getUsername(),
                user.getBio(),
                user.getProfilePicture(),
                followRepository.countByFollowingId(user.getId()),
                followRepository.countByFollowerId(user.getId()),
                isFollowing
        );
    }
    public List<UserResponseDTO> toResponseList(List<User> users, UUID currentUserId) {
        return users.stream()
                .map(user -> toResponse(user, currentUserId))
                .toList();
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public UserResponseDTO update(UUID id, UpdateUserRequest request) {
        User existing = findById(id);

        if (request.getNome() != null) {
            existing.setNome(request.getNome());
        }

        if (request.getEmail() != null &&
                !existing.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }

        if (request.getUsername() != null &&
                !existing.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (request.getUsername() != null) {
            existing.setUsername(request.getUsername());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getBio() != null) {
            existing.setBio(request.getBio());
        }

        if (request.getProfilePicture() != null) {
            existing.setProfilePicture(request.getProfilePicture());
        }

        return toResponse(userRepository.save(existing), null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}