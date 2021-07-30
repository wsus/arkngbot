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

public class ESOHubServiceGoldenTest {

    private static final String ESO_HUB_GOLDEN_VENDOR_URL = "https://eso-hub.com/en/golden-vendor";
    private static final String GOLDEN_ITEMS_MESSAGE = "**This week's golden vendor items are:**%n%nSilver Bed Nekles for 1000 Gold or 2000 AP%n";
    private static final String DIV_TAG = "div";
    private static final LocalDateTime MONDAY_MORNING = LocalDateTime.of(2021, 7, 26, 10, 0, 0);
    private static final LocalDateTime FRIDAY_NOON = LocalDateTime.of(2021, 7, 30, 12, 0, 0);
    private static final String CURRENT_WEEK = "Week 30, 2021";
    private static final String SET_1 = "Silver Bed";
    private static final String ITEM_1 = "Nekles";
    private static final String PRICE_1_GOLD = "1000";
    private static final String PRICE_1_AP = "2000";
    private static final String GOLDEN_MERCHANT_NOT_PRESENT = "The Golden is not currently present.";
    private static final String TD_TAG = "td";
    private static final String TR_TAG = "tr";
    private static final String SPACE = " ";

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
    public void shouldReturnGoldenItems() throws Exception {
        Document page = mock(Document.class);
        mockFlow(page);
        mockTime(MONDAY_MORNING);
        when(jsoupDocumentRetrievalServiceMock.retrieve(ESO_HUB_GOLDEN_VENDOR_URL)).thenReturn(page);

        String pledgesInfo = esoHubService.checkGoldenItems();

        assertThat(pledgesInfo, is(String.format(GOLDEN_ITEMS_MESSAGE)));
    }

    @Test
    public void shouldNotReturnGoldenItemsNotOnWeekend() throws Exception {
        Document page = mock(Document.class);
        mockFlow(page);
        mockTime(FRIDAY_NOON);
        when(jsoupDocumentRetrievalServiceMock.retrieve(ESO_HUB_GOLDEN_VENDOR_URL)).thenReturn(page);

        String pledgesInfo = esoHubService.checkGoldenItems();

        assertThat(pledgesInfo, is(GOLDEN_MERCHANT_NOT_PRESENT));
    }

    private void mockFlow(Document mainPage) {
        Element currentWeekCardHeader = new Element(DIV_TAG);
        Element parent = new Element(DIV_TAG);
        Element grandparent = new Element(DIV_TAG);

        when(mainPage.getElementsContainingOwnText(CURRENT_WEEK)).thenReturn(new Elements(currentWeekCardHeader));
        grandparent.appendChild(parent);
        parent.appendChild(currentWeekCardHeader);
        appendItem(currentWeekCardHeader);
    }

    private void appendItem(Element currentWeekCardHeader) {
        Element itemRow = new Element(TR_TAG);

        Element itemSetCell = new Element(TD_TAG);
        itemSetCell.text(SET_1);

        Element itemNameCell = new Element(TD_TAG);
        itemNameCell.text(ITEM_1);

        Element itemPriceCell = new Element(TD_TAG);
        itemPriceCell.text(PRICE_1_GOLD + SPACE + PRICE_1_AP);

        itemRow.appendChild(new Element(TD_TAG));
        itemRow.appendChild(itemSetCell);
        itemRow.appendChild(itemNameCell);
        itemRow.appendChild(itemPriceCell);

        currentWeekCardHeader.appendChild(itemRow);
    }

    private void mockTime(LocalDateTime time) {
        when(timeSupportMock.getCurrentTimeInUTC()).thenReturn(time);
        when(timeSupportMock.getCurrentDateInUTC()).thenReturn(time.toLocalDate());
    }
}
