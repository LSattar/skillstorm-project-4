package com.skillstorm.animalshelter.services;

import java.util.Optional;

public interface EmbeddingProvider {

    Optional<double[]> embed(String cacheKey, String text);

    boolean isReady();
}
