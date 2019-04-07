package com.ks.facade.impl;

import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.SecurityDirectives;
import com.ks.exceptions.InconsistentHashCodeException;
import com.ks.facade.MobileFacade;
import com.ks.service.HashService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static akka.http.javadsl.server.Directives.authenticateBasicAsync;
import static akka.http.javadsl.server.Directives.complete;
import static akka.http.javadsl.server.Directives.path;
import static akka.http.javadsl.server.PathMatchers.segment;
import static java.util.regex.Pattern.compile;

public class MobileFacadeImpl implements MobileFacade {

    private HashService hashService;
    private final Function<Optional<SecurityDirectives.ProvidedCredentials>, CompletionStage<Optional<String>>>
            basicAuthenticator = opt -> {
        if (opt.filter(credentials -> credentials.identifier().equals("user")
                && credentials.verify("password")).isPresent()) {
            return CompletableFuture.completedFuture(Optional.of(opt.get().identifier()));
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    };


    public MobileFacadeImpl(HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public Route getMobileRoute() {

        return path(segment("hash").slash(segment(compile("\\d+"))),
                (hash) -> authenticateBasicAsync("secure",
                        basicAuthenticator,
                        userName -> complete(StatusCodes.OK, hashService.getMobile(hash).toString())));
    }

    @Override
    public Route getHashRoute() {
        return path(segment("mobile").slash(segment(compile(".*"))),
                (mobile) -> authenticateBasicAsync("secure",
                        basicAuthenticator,
                        userName -> complete(StatusCodes.OK, calculateHash(mobile))));
    }

    private String calculateHash(String mobile) {
        Long mobileNumber = Long.valueOf(mobile);
        String hash = hashService.getHash(mobileNumber);

        if (hashService.isHashExists(hash)) {
            if (isHashCodeCollision(mobileNumber, hash)) {
                return hash;
            } else {
                throw new InconsistentHashCodeException("Duplication during hashing is defined");
            }
        }

        processAddNewMobile(mobileNumber, hash);

        return hash;
    }

    private void processAddNewMobile(Long mobileNumber, String hash) {
        hashService.addMobile(mobileNumber);
        if (hashService.isMobileExists(mobileNumber)) {
            hashService.addHash(hash, mobileNumber);
        }
    }

    private boolean isHashCodeCollision(Long mobileNumber, String hash) {
        return hashService.getMobile(hash).equals(mobileNumber);
    }
}
