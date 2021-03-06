package com.bookwhale.auth.domain;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public final class JWT {

    private final String issuer;
    private final String clientSecret;
    private final long expiryMilliSeconds;
    private final long expiryRefreshMilliSeconds;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;

    public JWT(String issuer, String clientSecret, int expirySecond, int expiryRefreshSecond) {
        this.issuer = issuer;
        this.clientSecret = clientSecret;
        this.expiryMilliSeconds = expirySecond * 1_000L;
        this.expiryRefreshMilliSeconds = expiryRefreshSecond * 1_000L;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm).withIssuer(issuer).build();
    }

    public String createNewToken(Claims claims) {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date current = Date.from(instant);

        JWTCreator.Builder tokenBuilder = com.auth0.jwt.JWT.create();
        tokenBuilder.withIssuer(issuer);
        tokenBuilder.withIssuedAt(current);

        if (expiryMilliSeconds > 0) {
            tokenBuilder.withExpiresAt(new Date(instant.toEpochMilli() + expiryMilliSeconds));
        }

        tokenBuilder.withClaim("name", claims.name);
        tokenBuilder.withClaim("email", claims.email);
        tokenBuilder.withClaim("image", claims.image);

        return tokenBuilder.sign(algorithm);
    }

    public String createNewRefreshToken(ClaimsForRefresh claims) {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date current = Date.from(instant);

        JWTCreator.Builder tokenBuilder = com.auth0.jwt.JWT.create();
        tokenBuilder.withIssuer(issuer);
        tokenBuilder.withIssuedAt(current);

        if (expiryRefreshMilliSeconds > 0) {
            tokenBuilder.withExpiresAt(
                new Date(instant.toEpochMilli() + expiryRefreshMilliSeconds));
        }

        tokenBuilder.withClaim("rid", claims.rid);
        tokenBuilder.withClaim("email", claims.email);

        return tokenBuilder.sign(algorithm);
    }

    public String refreshApiToken(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = com.auth0.jwt.JWT.decode(token);
        Claims claims = new Claims(decodedJWT);
        claims.removeIssuedAt();
        claims.removeExpiresAt();
        return createNewToken(claims);
    }

    public Claims verify(String token) {
        DecodedJWT verify = null;
        try {
            verify = jwtVerifier.verify(token);
        } catch (AlgorithmMismatchException e) {
            log.error("not defined algorithm used.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (SignatureVerificationException e) {
            log.error("invalid signature.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (TokenExpiredException e) {
            log.error("token has expired.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (InvalidClaimException e) {
            log.error("not expected claim contained.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (JWTVerificationException e) {
            log.error("token verify failed.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return new Claims(verify);
    }

    public ClaimsForRefresh verifyForRefresh(String token) {
        DecodedJWT verify = null;
        try {
            verify = jwtVerifier.verify(token);
        } catch (AlgorithmMismatchException e) {
            log.error("not defined algorithm used.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (SignatureVerificationException e) {
            log.error("invalid signature.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (TokenExpiredException e) {
            log.error("token has expired.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (InvalidClaimException e) {
            log.error("not expected claim contained.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        } catch (JWTVerificationException e) {
            log.error("token verify failed.", e);
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return new ClaimsForRefresh(verify);
    }

    public static class Claims {

        String name;
        String email;
        String image;
        LocalDateTime issuedAt;
        LocalDateTime expiresAt;

        protected Claims() {
        }

        Claims(DecodedJWT decodedJWT) {
            Claim name = decodedJWT.getClaim("name");
            if (!name.isNull()) {
                this.name = name.asString();
            }
            Claim email = decodedJWT.getClaim("email");
            if (!email.isNull()) {
                this.email = email.asString();
            }
            Claim image = decodedJWT.getClaim("image");
            if (!image.isNull()) {
                this.image = image.asString();
            }
            this.issuedAt = LocalDateTime.ofInstant(decodedJWT.getIssuedAt().toInstant(),
                ZoneId.systemDefault());
            this.expiresAt = LocalDateTime.ofInstant(decodedJWT.getExpiresAt().toInstant(),
                ZoneId.systemDefault());
        }

        public static Claims of(String name, String email, String image) {
            Claims claims = new Claims();
            claims.name = name;
            claims.email = email;
            claims.image = image;
            return claims;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getImage() {
            return image;
        }

        public long getIssuedAt() {
            return issuedAt == null ? -1
                : issuedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        public long getExpiresAt() {
            return expiresAt == null ? -1
                : expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        void removeIssuedAt() {
            issuedAt = null;
        }

        void removeExpiresAt() {
            expiresAt = null;
        }
    }

    public static class ClaimsForRefresh {

        String rid;
        String email;
        LocalDateTime issuedAt;
        LocalDateTime expiresAt;

        protected ClaimsForRefresh() {
        }

        ClaimsForRefresh(DecodedJWT decodedJWT) {
            Claim rid = decodedJWT.getClaim("rid");
            if (!rid.isNull()) {
                this.rid = rid.asString();
            }
            Claim email = decodedJWT.getClaim("email");
            if (!email.isNull()) {
                this.email = email.asString();
            }
            this.issuedAt = LocalDateTime.ofInstant(decodedJWT.getIssuedAt().toInstant(),
                ZoneId.systemDefault());
            this.expiresAt = LocalDateTime.ofInstant(decodedJWT.getExpiresAt().toInstant(),
                ZoneId.systemDefault());
        }

        public static ClaimsForRefresh of(String rid, String email) {
            ClaimsForRefresh claims = new ClaimsForRefresh();
            claims.rid = rid;
            claims.email = email;
            return claims;
        }

        public String getRid() {
            return rid;
        }

        public String getEmail() {
            return email;
        }

        public long getIssuedAt() {
            return issuedAt == null ? -1
                : issuedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        public long getExpiresAt() {
            return expiresAt == null ? -1
                : expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }
}
