package org.arkngbot.services;

import org.arkngbot.services.impl.ESOHubServiceImpl;
import org.arkngbot.services.impl.TimeSupport;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ESOHubServicePledgesTest {

    private static final String ESO_HUB_PLEDGES_URL = "https://eso-hub.com/en/daily-undaunted-pledges";
    private static final String H6_TAG = "h6";
    private static final String PLEDGE_1 = "Fungal Grotto I";
    private static final String PLEDGE_2 = "Spindleclutch II";
    private static final String PLEDGE_3 = "White-Gold Tower";
    private static final String PLEDGES_MESSAGE = "**Today's Undaunted Pledges:**%n%nMaj al-Ragath: Fungal Grotto I%nGlirion the Redbeard: Spindleclutch II%nUrgarlag Chief-bane: White-Gold Tower";
    private static final String TABLE_HEADER = "Today";
    private static final String TH_TAG = "th";
    private static final String TR_TAG = "tr";
    private static final String TABLE_TAG = "table";

    ESOHubService esoHubService;

    JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    @BeforeEach
    public void setUp() {
        jsoupDocumentRetrievalService = mock(JsoupDocumentRetrievalService.class);
        esoHubService = new ESOHubServiceImpl(jsoupDocumentRetrievalService, mock(TimeSupport.class));
    }

    @Test
    public void shouldReturnPledges() throws Exception {
        Document pledgesPage = mock(Document.class);
        mockFlow(pledgesPage);
        when(jsoupDocumentRetrievalService.retrieve(ESO_HUB_PLEDGES_URL)).thenReturn(pledgesPage);

        String pledgesInfo = esoHubService.checkPledges();

        assertThat(pledgesInfo, is(String.format(PLEDGES_MESSAGE)));
    }

    private void mockFlow(Document pledgesPage) {
        Element tableHeader = new Element(TH_TAG);
        Element tableRow = new Element(TR_TAG);
        Element table = new Element(TABLE_TAG);

        when(pledgesPage.getElementsContainingOwnText(TABLE_HEADER)).thenReturn(new Elements(tableHeader));
        table.appendChild(tableRow);
        tableRow.appendChild(tableHeader);
        appendPledges(table, PLEDGE_1, PLEDGE_2, PLEDGE_3);
    }

    private void appendPledges(Element table, String... pledgeDungeons) {
        Arrays.stream(pledgeDungeons)
                .forEach(pd -> appendPledgeHeader(table, pd));
    }

    private void appendPledgeHeader(Element table, String pledgeDungeon) {
        Element h6Text = new Element(H6_TAG);
        h6Text.text(pledgeDungeon);
        table.appendChild(h6Text);
    }
}
