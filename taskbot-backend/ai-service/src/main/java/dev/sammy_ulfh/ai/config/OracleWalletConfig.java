// src/main/java/dev/sammy_ulfh/ai/config/OracleWalletConfig.java
package dev.sammy_ulfh.ai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OracleWalletConfig {

    @Value("${oracle.wallet.path}")
    private String walletPath;

    @PostConstruct
    public void init() {
        System.setProperty("oracle.net.tns_admin", walletPath);
        System.setProperty("TNS_ADMIN", walletPath);
    }
}