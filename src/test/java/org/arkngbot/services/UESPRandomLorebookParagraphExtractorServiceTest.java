package org.arkngbot.services;

import org.arkngbot.services.impl.UESPRandomLorebookParagraphExtractorServiceImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UESPRandomLorebookParagraphExtractorServiceTest {

    private static final String UESP_URL = "https://en.uesp.net";
    private static final String ATTRIBUTE_HREF = "href";
    private static final String WHAT_LINKS_HERE_LIST = "mw-whatlinkshere-list";
    private static final String BOOK_CLASS = "book";
    private static final String P_TAG = "p";
    private static final String UESP_BOOKS_URL = "https://en.uesp.net/w/index.php?title=Special%3AWhatLinksHere&limit=9999&target=Template%3ALore+Book&namespace=130";
    private static final String FIRST_BOOK_URL = "/firstBookUrl";
    private static final String SECOND_BOOK_URL = "/secondBookUrl";
    private static final String FIRST_PARAGRAPH = "firstParagraph";
    private static final String SECOND_PARAGRAPH = "secondParagraph";
    private static final String THIRD_PARAGRAPH = "thirdParagraph";
    private static final String FOURTH_PARAGRAPH = "fourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraph";
    private static final String FOURTH_PARAGRAPH_TRUNCATED = "fourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourthParagraphfourt";


    UESPRandomLorebookParagraphExtractorService uespRandomLorebookParagraphExtractorService;

    JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    @BeforeEach
    public void setUp() {
        jsoupDocumentRetrievalService = mock(JsoupDocumentRetrievalService.class);
        uespRandomLorebookParagraphExtractorService = new UESPRandomLorebookParagraphExtractorServiceImpl(jsoupDocumentRetrievalService);
    }

    @Test
    public void shouldReturnAParagraph() throws Exception {
        Document bookListPage = mock(Document.class);
        Elements searchResults = mockTwoBooks();
        mockFlow(bookListPage, searchResults);

        String paragraph = uespRandomLorebookParagraphExtractorService.extractRandomLorebookParagraph();

        assertThat(paragraph, anyOf(is(FIRST_PARAGRAPH), is(SECOND_PARAGRAPH), is(THIRD_PARAGRAPH), is(FOURTH_PARAGRAPH_TRUNCATED)));
    }

    private Elements mockTwoBooks() throws Exception {
        Elements searchResults = new Elements();

        Element firstBook = mockBookEntry(FIRST_BOOK_URL, FIRST_PARAGRAPH, SECOND_PARAGRAPH);
        Element secondBook = mockBookEntry(SECOND_BOOK_URL, THIRD_PARAGRAPH, FOURTH_PARAGRAPH);
        searchResults.addAll(Arrays.asList(firstBook, secondBook));

        return searchResults;
    }

    private void mockFlow(Document bookListPage, Elements entries) throws Exception {
        Element bookList = mock(Element.class);

        when(jsoupDocumentRetrievalService.retrieve(UESP_BOOKS_URL)).thenReturn(bookListPage);
        when(bookListPage.getElementById(WHAT_LINKS_HERE_LIST)).thenReturn(bookList);
        when(bookList.children()).thenReturn(entries);
    }

    private Element mockBookEntry(String url, String... paragraphTexts) throws Exception {
        Element entry = mock(Element.class);
        Element a = mock(Element.class);
        Document book = mockBookPage(paragraphTexts);

        when(entry.child(0)).thenReturn(a);
        when(a.attr(ATTRIBUTE_HREF)).thenReturn(url);
        when(jsoupDocumentRetrievalService.retrieve(UESP_URL + url)).thenReturn(book);

        return entry;
    }

    private Document mockBookPage(String... paragraphsTexts) {
        Document book = mock(Document.class);
        Element bookBody = mock(Element.class);
        Elements paragraphs = new Elements();
        paragraphs.addAll(Arrays.stream(paragraphsTexts)
        .map(this::mockBookParagraph)
        .collect(Collectors.toList()));

        when(book.getElementsByClass(BOOK_CLASS)).thenReturn(new Elements(bookBody));
        when(bookBody.getElementsByTag(P_TAG)).thenReturn(paragraphs);

        return book;
    }

    private Element mockBookParagraph(String text) {
        Element paragraph = mock(Element.class);
        when(paragraph.text()).thenReturn(text);

        return paragraph;
    }
}
