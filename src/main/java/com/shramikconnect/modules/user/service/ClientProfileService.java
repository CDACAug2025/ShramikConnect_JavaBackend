package com.shramikconnect.modules.user.service;

import com.shramikconnect.common.enums.District;
import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.entity.Client;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.user.dto.ClientProfileResponse;
import com.shramikconnect.modules.user.dto.ClientProfileUpdateRequest;
import com.shramikconnect.modules.user.repository.ClientRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientProfileService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    // ===============================
    // GET PROFILE (AUTO CREATE)
    // ===============================
    public ClientProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setUser(user);
                    newClient.setAddress("");
                    newClient.setDistrict(District.valueOf("PUNE")); // default
                    return clientRepository.save(newClient);
                });

        ClientProfileResponse response = new ClientProfileResponse();
        response.setName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setMobile(user.getPhone());
        response.setDistrict(client.getDistrict().name());
        response.setAddress(client.getAddress());
        response.setKycVerified(true);

        return response;
    }

    // ===============================
    // UPDATE PROFILE (SAFE)
    // ===============================
    public void updateProfile(String email, ClientProfileUpdateRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setUser(user);
                    return newClient;
                });

        user.setFullName(request.getName());
        user.setPhone(request.getMobile());

        client.setAddress(request.getAddress());
        client.setDistrict(District.valueOf(request.getDistrict()));

        userRepository.save(user);
        clientRepository.save(client);
    }

    // ===============================
    // BLOCK ACCOUNT (SOFT DELETE)
    // ===============================
    public void blockAccount(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.BLOCKED);
        userRepository.save(user);
    }
}
