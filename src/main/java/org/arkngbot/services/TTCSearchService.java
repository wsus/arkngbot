package org.arkngbot.services;

import org.springframework.lang.NonNull;

import java.io.IOException;

public interface TTCSearchService {

    @NonNull
    String search(@NonNull String query);

    @NonNull
    String checkPrice(@NonNull String query) throws IOException;
}
