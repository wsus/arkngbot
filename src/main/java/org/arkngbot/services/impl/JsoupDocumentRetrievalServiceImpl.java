package org.arkngbot.services.impl;

import org.arkngbot.services.JsoupDocumentRetrievalService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsoupDocumentRetrievalServiceImpl implements JsoupDocumentRetrievalService {

    @Override
    public Document retrieve(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
