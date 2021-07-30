package org.arkngbot.services;

import org.arkngbot.services.impl.ESOHubServiceImpl;
import org.arkngbot.services.impl.TimeSupport;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ESOHubServiceServersTest {

    private static final String ESO_HUB_URL = "https://eso-hub.com/";
    private static final String DIV_TAG = "div";
    private static final String TD_TAG = "td";
    private static final String MEGASERVERS_BOX = "Megaservers";
    private static final String STYLE_ATTR = "style";
    private static final String SERVER_STATUS_PATTERN = "**Current status of the megaservers:**%nServer 1 is up%nServer 2 is down%n";
    private static final String GREEN_DOT_CSS = "background-color: #559525";
    private static final String SPAN_TAG = "span";
    private static final String SERVER_1 = "Server 1";
    private static final String SERVER_2 = "Server 2";

    ESOHubService esoHubService;

    JsoupDocumentRetrievalService jsoupDocumentRetrievalServiceMock;

    @BeforeEach
    public void setUp() {
        jsoupDocumentRetrievalServiceMock = mock(JsoupDocumentRetrievalService.class);
        esoHubService = new ESOHubServiceImpl(jsoupDocumentRetrievalServiceMock, mock(TimeSupport.class));
    }

    @Test
    public void shouldReturnServerInfo() throws Exception {
        Document page = mock(Document.class);
        mockFlow(page);
        when(jsoupDocumentRetrievalServiceMock.retrieve(ESO_HUB_URL)).thenReturn(page);

        String pledgesInfo = esoHubService.checkServers();

        assertThat(pledgesInfo, is(String.format(SERVER_STATUS_PATTERN)));
    }

    private void mockFlow(Document mainPage) {
        Element megaserversBoxHeader = new Element(DIV_TAG);
        Element parent = new Element(DIV_TAG);
        Element grandparent = new Element(DIV_TAG);

        when(mainPage.getElementsContainingOwnText(MEGASERVERS_BOX)).thenReturn(new Elements(megaserversBoxHeader));
        grandparent.appendChild(parent);
        parent.appendChild(megaserversBoxHeader);
        appendItem(megaserversBoxHeader, SERVER_1, true);
        appendItem(megaserversBoxHeader, SERVER_2, false);
    }

    private void appendItem(Element currentWeekCardHeader, String serverName, boolean up) {
        Element dotSpan = new Element(SPAN_TAG);
        if (up) {
            dotSpan.attr(STYLE_ATTR, GREEN_DOT_CSS);
        }
        Element serverCell = new Element(TD_TAG);
        serverCell.text(serverName);
        serverCell.appendChild(dotSpan);

        currentWeekCardHeader.appendChild(serverCell);
    }
}
