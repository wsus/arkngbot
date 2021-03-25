package org.arkngbot.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.arkngbot.services.JsoupDocumentRetrievalService;
import org.arkngbot.services.UESPRandomLorebookParagraphExtractorService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UESPRandomLorebookParagraphExtractorServiceImpl implements UESPRandomLorebookParagraphExtractorService {

    private static final String UESP_URL = "https://en.uesp.net";
    private static final String ATTRIBUTE_HREF = "href";
    private static final String WHAT_LINKS_HERE_LIST = "mw-whatlinkshere-list";
    private static final String BOOK_CLASS = "book";
    private static final String P_TAG = "p";
    private static final String UESP_BOOKS_URL = "https://en.uesp.net/w/index.php?title=Special%3AWhatLinksHere&limit=9999&target=Template%3ALore+Book&namespace=130";
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final int BEGIN_INDEX = 0;

    private final JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    @Autowired
    public UESPRandomLorebookParagraphExtractorServiceImpl(JsoupDocumentRetrievalService jsoupDocumentRetrievalService) {
        this.jsoupDocumentRetrievalService = jsoupDocumentRetrievalService;
    }

    @Override
    public String extractRandomLorebookParagraph() throws Exception {

        Document bookListPage = jsoupDocumentRetrievalService.retrieve(UESP_BOOKS_URL);

        Element bookList = bookListPage.getElementById(WHAT_LINKS_HERE_LIST);
        Elements entries = bookList.children();

        Random randomizer = new Random();
        Element entry = getRandomElement(entries, randomizer);
        String relativeUrl = entry.child(0).attr(ATTRIBUTE_HREF);

        Document book = jsoupDocumentRetrievalService.retrieve(UESP_URL + relativeUrl);
        Element bookBody = book.getElementsByClass(BOOK_CLASS).first();
        List<Element> paragraphs = extractParagraphs(bookBody);

        if (paragraphs.isEmpty()) {
            // try another book if this one has no extractable tags
            return extractRandomLorebookParagraph();
        }

        Element randomParagraph = getRandomElement(paragraphs, randomizer);

        return truncateIfNeeded(randomParagraph.text());
    }

    private List<Element> extractParagraphs(Element bookBody) {
        return Optional.ofNullable(bookBody)
                .map(bb -> bb.getElementsByTag(P_TAG))
                .map(bb -> bb.stream()
                        .filter(p -> !StringUtils.isBlank(p.text()))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    private <T> T getRandomElement(List<T> collection, Random randomizer) {
        return collection.get(randomizer.nextInt(collection.size()));
    }

    private String truncateIfNeeded(String text) {
        if (text.length() > MAX_MESSAGE_LENGTH) {
            return text.substring(BEGIN_INDEX, MAX_MESSAGE_LENGTH);
        }

        return text;
    }
}
