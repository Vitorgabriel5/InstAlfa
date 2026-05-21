package AlfaInsta.demo.service;

import AlfaInsta.demo.model.User;
import AlfaInsta.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public String authenticateWithGoogle(String accessToken) {
        try {
            // ✅ Validar o token com o Google
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = restTemplate.getForObject(url, Map.class);

            if (userInfo == null) {
                throw new RuntimeException("Não foi possível obter informações do Google");
            }

            // ✅ Extrair dados
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            String picture = (String) userInfo.get("picture");
            String googleId = (String) userInfo.get("id");

            if (email == null || googleId == null) {
                throw new RuntimeException("Email ou ID não fornecido pelo Google");
            }

            // ✅ Buscar ou criar usuário
            User user = findOrCreateOAuthUser(email, name, picture, googleId, "google");

            // ✅ Gerar JWT
            return jwtService.generateToken(user.getId(), user.getUsername());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao autenticar com Google: " + e.getMessage(), e);
        }
    }

    private User findOrCreateOAuthUser(String email, String name, String picture,
                                       String oauthId, String provider) {
        // Buscar por OAuth ID e provider
        Optional<User> existingOAuthUser = userRepository.findByOauthIdAndOauthProvider(oauthId, provider);

        if (existingOAuthUser.isPresent()) {
            return existingOAuthUser.get();
        }

        // Buscar por email
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            user.setOauthId(oauthId);
            user.setOauthProvider(provider);
            if (picture != null && user.getProfilePicture() == null) {
                user.setProfilePicture(picture);
            }
            return userRepository.save(user);
        }

        // Criar novo usuário
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setNome(name != null ? name : "Usuário Google");
        newUser.setUsername(generateUniqueUsername(email));
        newUser.setProfilePicture(picture);
        newUser.setOauthId(oauthId);
        newUser.setOauthProvider(provider);
        newUser.setPassword("");

        return userRepository.save(newUser);
    }

    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0]
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase();

        if (baseUsername.length() < 3) {
            baseUsername = "user" + baseUsername;
        }

        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
}