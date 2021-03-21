package org.arkngbot.services;

import java.io.IOException;

public interface TTCSearchService {

    String search(String query);

    String checkPrice(String query) throws IOException;
}
