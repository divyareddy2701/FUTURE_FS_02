package com.divya.minicrm.config;

import com.divya.minicrm.entity.AdminUser;
import com.divya.minicrm.entity.Lead;
import com.divya.minicrm.enums.LeadStatus;
import com.divya.minicrm.repository.AdminUserRepository;
import com.divya.minicrm.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final LeadRepository leadRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdminUserRepository adminUserRepository,
                      LeadRepository leadRepository,
                      PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.leadRepository = leadRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedSampleLeads();
    }

    private void seedAdmin() {
        if (adminUserRepository.findByUsername(adminUsername).isEmpty()) {

            AdminUser admin = AdminUser.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role("ROLE_ADMIN")
                    .build();

            adminUserRepository.save(admin);

            System.out.println("Seeded admin user: " + adminUsername);
        }
    }

    private void seedSampleLeads() {

        if (leadRepository.count() == 0) {

            leadRepository.save(Lead.builder()
                    .name("Ananya Rao")
                    .email("ananya.rao@example.com")
                    .phone("9876543210")
                    .source("Website Contact Form")
                    .status(LeadStatus.NEW)
                    .build());


            leadRepository.save(Lead.builder()
                    .name("Vikram Shetty")
                    .email("vikram.shetty@example.com")
                    .phone("9123456780")
                    .source("Referral")
                    .status(LeadStatus.CONTACTED)
                    .build());

            System.out.println("Seeded sample leads.");
        }
    }
}