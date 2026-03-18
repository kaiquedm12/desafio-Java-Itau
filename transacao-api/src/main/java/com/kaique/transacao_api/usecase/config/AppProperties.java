package com.kaique.transacao_api.usecase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final User user = new User();
    private final Statistics statistics = new Statistics();

    public Jwt getJwt() {
        return jwt;
    }

    public User getUser() {
        return user;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public static class Jwt {
        private String issuer;
        private String secret;
        private long expirationSeconds;

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationSeconds() {
            return expirationSeconds;
        }

        public void setExpirationSeconds(long expirationSeconds) {
            this.expirationSeconds = expirationSeconds;
        }
    }

    public static class User {
        private final Registration registration = new Registration();

        public Registration getRegistration() {
            return registration;
        }
    }

    public static class Registration {
        private boolean allowTemporaryPassword;

        public boolean isAllowTemporaryPassword() {
            return allowTemporaryPassword;
        }

        public void setAllowTemporaryPassword(boolean allowTemporaryPassword) {
            this.allowTemporaryPassword = allowTemporaryPassword;
        }
    }

    public static class Statistics {
        private long windowSeconds;

        public long getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(long windowSeconds) {
            this.windowSeconds = windowSeconds;
        }
    }
}
