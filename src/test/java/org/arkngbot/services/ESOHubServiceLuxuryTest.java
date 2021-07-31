package org.arkngbot.services;

import org.arkngbot.services.impl.ESOHubServiceImpl;
import org.arkngbot.services.impl.TimeSupport;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ESOHubServiceLuxuryTest {

    private static final String ESO_HUB_LUXURY_FURNISHER_URL = "https://eso-hub.com/en/luxury-furnisher";
    private static final String LUXURY_FURNISHINGS_MESSAGE = "**This week's luxury furnishings are:**%n%nitem1 for 100 Gold%nitem2 for 1000 Gold%n";
    private static final String DIV_TAG = "div";
    private static final String ITEM_CLASS = "set-tooltip-center";
    private static final String A_TAG = "a";
    private static final String CLASS_ATTR = "class";
    private static final LocalDateTime MONDAY_MORNING = LocalDateTime.of(2021, 7, 26, 10, 0, 0);
    private static final LocalDateTime FRIDAY_NOON = LocalDateTime.of(2021, 7, 30, 12, 0, 0);
    private static final String CURRENT_WEEK = "Week 30, 2021";
    private static final String ITEM_1 = "item1";
    private static final String ITEM_2 = "item2";
    private static final String PRICE_1 = "100 Gold";
    private static final String PRICE_2 = "1000 Gold";
    private static final String FURNISHER_NOT_PRESENT = "The Luxury Furnisher is not currently present.";
    private static final int WEEK_30 = 30;
    private static final String NO_DATA_FOR_CURRENT_WEEK = "Could not find data for the current week. Please try again later.";


    ESOHubService esoHubService;

    JsoupDocumentRetrievalService jsoupDocumentRetrievalServiceMock;
    TimeSupport timeSupportMock;

    @BeforeEach
    public void setUp() {
        jsoupDocumentRetrievalServiceMock = mock(JsoupDocumentRetrievalService.class);
        timeSupportMock = mock(TimeSupport.class);
        esoHubService = new ESOHubServiceImpl(jsoupDocumentRetrievalServiceMock, timeSupportMock);
    }

    @Test
    public void shouldReturnLuxuryFurnishings() throws Exception {
        Document page = mock(Document.class);
        mockFlow(page);
        mockTime(MONDAY_MORNING);
        when(jsoupDocumentRetrievalServiceMock.retrieve(ESO_HUB_LUXURY_FURNISHER_URL)).thenReturn(page);

        String pledgesInfo = esoHubService.checkLuxuryFurnishings();

        assertThat(pledgesInfo, is(String.format(LUXURY_FURNISHINGS_MESSAGE)));
    }

    @Test
    public void shouldNotReturnLuxuryFurnishingsNotOnWeekend() throws Exception {
        Document page = mock(Document.class);
        mockFlow(page);
        mockTime(FRIDAY_NOON);
        when(jsoupDocumentRetrievalServiceMock.retrieve(ESO_HUB_LUXURY_FURNISHER_URL)).thenReturn(page);

        String pledgesInfo = esoHubService.checkLuxuryFurnishings();

        assertThat(pledgesInfo, is(FURNISHER_NOT_PRESENT));
    }

    @Test
    public void shouldNotReturnLuxuryFurnishingsNoData() throws Exception {
        Document page = mock(Document.class);
        when(page.getElementsContainingOwnText(CURRENT_WEEK)).thenReturn(new Elements());;
        mockTime(MONDAY_MORNING);
        when(jsoupDocumentRetrievalServiceMock.retrieve(ESO_HUB_LUXURY_FURNISHER_URL)).thenReturn(page);

        String pledgesInfo = esoHubService.checkLuxuryFurnishings();

        assertThat(pledgesInfo, is(NO_DATA_FOR_CURRENT_WEEK));
    }

    private void mockFlow(Document mainPage) {
        Element currentWeekCardHeader = new Element(DIV_TAG);
        Element parent = new Element(DIV_TAG);
        Element grandparent = new Element(DIV_TAG);

        when(mainPage.getElementsContainingOwnText(CURRENT_WEEK)).thenReturn(new Elements(currentWeekCardHeader));
        grandparent.appendChild(parent);
        parent.appendChild(currentWeekCardHeader);
        appendItem(currentWeekCardHeader, ITEM_1, PRICE_1);
        appendItem(currentWeekCardHeader, ITEM_2, PRICE_2);
    }

    private void appendItem(Element currentWeekCardHeader, String name, String price) {
        Element itemContainer = new Element(DIV_TAG);
        itemContainer.attr(CLASS_ATTR, ITEM_CLASS);

        Element itemNameWrapper = new Element(A_TAG);
        itemNameWrapper.text(name);

        Element goldWrapper = new Element(DIV_TAG);
        goldWrapper.text(price);

        itemContainer.appendChild(itemNameWrapper);
        itemContainer.appendChild(goldWrapper);

        currentWeekCardHeader.appendChild(itemContainer);
    }

    private void mockTime(LocalDateTime time) {
        when(timeSupportMock.getCurrentTimeInUTC()).thenReturn(time);
        when(timeSupportMock.getCurrentDateInUTC()).thenReturn(time.toLocalDate());
        when(timeSupportMock.calculateWeekNumber(time.toLocalDate())).thenReturn(WEEK_30);
    }
}
