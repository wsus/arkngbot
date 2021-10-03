package org.arkngbot.services;

import org.springframework.lang.NonNull;

public interface TTCSearchService {

    @NonNull
    String search(@NonNull String query);

    @NonNull
    String checkPrice(@NonNull String query);
}
