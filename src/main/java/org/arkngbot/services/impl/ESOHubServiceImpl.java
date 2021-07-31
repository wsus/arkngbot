package org.arkngbot.services.impl;

import org.arkngbot.services.ESOHubService;
import org.arkngbot.services.JsoupDocumentRetrievalService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
public class ESOHubServiceImpl implements ESOHubService {

    private static final String ESO_HUB_PLEDGES_URL = "https://eso-hub.com/en/daily-undaunted-pledges";
    private static final String ESO_HUB_LUXURY_FURNISHER_URL = "https://eso-hub.com/en/luxury-furnisher";
    private static final String TABLE_HEADER_TEXT = "Today";
    private static final String CANT_FIND_HEADER = "Cannot find table header";
    private static final String H6_TAG = "h6";
    private static final String MESSAGE_TEMPLATE = "**Today's Undaunted Pledges:**%n%nMaj al-Ragath: %s%nGlirion the Redbeard: %s%nUrgarlag Chief-bane: %s";
    private static final EnumSet<DayOfWeek> WEEKEND_DAYS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY);
    private static final int NOON = 12;
    private static final String TAG_A = "a";
    private static final String GOLD = "Gold";
    private static final String CANNOT_FIND_ITEM_PRICE = "Cannot find item price";
    private static final String LUXURY_ITEM_PATTERN = "%s for %s%n";
    private static final String GOLDEN_ITEM_PATTERN = "%s %s for %s Gold or %s AP%n";
    private static final String LUXURY_HEADER = "**This week's luxury furnishings are:**%n%n";
    private static final String GOLDEN_HEADER = "**This week's golden vendor items are:**%n%n";
    private static final String ITEM_CLASS = "set-tooltip-center";
    private static final String CANNOT_FIND_THE_CURRENT_WEEK_CARD = "Cannot find the current week card";
    private static final String FURNISHER_NOT_PRESENT = "The Luxury Furnisher is not currently present.";
    private static final String GOLDEN_MERCHANT_NOT_PRESENT = "The Golden is not currently present.";
    private static final String CURRENT_WEEK_PATTERN = "Week %02d, %d";
    private static final String SPACE = " ";
    private static final String TAG_TD = "td";
    private static final String TAG_TR = "tr";
    private static final String ESO_HUB_GOLDEN_VENDOR_URL = "https://eso-hub.com/en/golden-vendor";
    private static final String ESO_HUB_URL = "https://eso-hub.com/";
    private static final String MEGASERVERS_BOX = "Megaservers";
    private static final String CANNOT_FIND_THE_SERVER_STATUS_TABLE = "Cannot find the server status table";
    private static final String SERVERS_HEADER = "**Current status of the megaservers:**%n";
    private static final String STYLE_ATTR = "style";
    private static final String SERVER_STATUS_PATTERN = "%s is %s%n";
    private static final String GREEN_DOT_CSS = "background-color: #559525";
    private static final String STATUS_UP = "up";
    private static final String STATUS_DOWN = "down";
    private static final String NO_DATA_FOR_CURRENT_WEEK = "Could not find data for the current week. Please try again later.";

    private final JsoupDocumentRetrievalService jsoupDocumentRetrievalService;
    private final TimeSupport timeSupport;

    @Autowired
    public ESOHubServiceImpl(JsoupDocumentRetrievalService jsoupDocumentRetrievalService, TimeSupport timeSupport) {
        this.jsoupDocumentRetrievalService = jsoupDocumentRetrievalService;
        this.timeSupport = timeSupport;
    }

    @NonNull
    @Override
    public String checkPledges() throws Exception {
        Document pledgesPage = jsoupDocumentRetrievalService.retrieve(ESO_HUB_PLEDGES_URL);

        Element tableHeader = pledgesPage.getElementsContainingOwnText(TABLE_HEADER_TEXT).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(CANT_FIND_HEADER));

        Object[] pledges = tableHeader.parent().parent().getElementsByTag(H6_TAG).stream()
                .map(Element::text)
                .toArray();

        return String.format(MESSAGE_TEMPLATE, pledges);
    }

    @Override
    public String checkLuxuryFurnishings() throws Exception {
        if (isWeekend()) {
            return retrieveLuxuryFurnishings();
        }
        else {
            return FURNISHER_NOT_PRESENT;
        }
    }

    private boolean isWeekend() {
        LocalDateTime currentTime = timeSupport.getCurrentTimeInUTC();
        if (WEEKEND_DAYS.contains(currentTime.getDayOfWeek())) {
           if (currentTime.getDayOfWeek() == DayOfWeek.MONDAY) {
               return currentTime.getHour() < NOON;
           }
           return true;
        }
        return false;
    }

    private String getCurrentWeek() {
        LocalDate currentDate = timeSupport.getCurrentDateInUTC();
        int year = currentDate.getYear();
        int week = timeSupport.calculateWeekNumber(currentDate);
        return String.format(CURRENT_WEEK_PATTERN, week, year);
    }

    private String retrieveLuxuryFurnishings() throws IOException {
        Document luxuryFurnisherPage = jsoupDocumentRetrievalService.retrieve(ESO_HUB_LUXURY_FURNISHER_URL);

        Element currentWeekCard = findCurrentWeekCard(luxuryFurnisherPage);

        if (currentWeekCard == null) {
            return NO_DATA_FOR_CURRENT_WEEK;
        }

        StringBuilder responseBuilder = new StringBuilder(LUXURY_HEADER);
        currentWeekCard.getElementsByClass(ITEM_CLASS)
                .forEach(e -> appendLuxuryFurnishing(e, responseBuilder));

        return String.format(responseBuilder.toString());
    }

    private Element findCurrentWeekCard(Document page) {
        return page.getElementsContainingOwnText(getCurrentWeek()).stream()
                .findFirst()
                .map(Element::parent)
                .map(Element::parent)
                .orElse(null);
    }

    private void appendLuxuryFurnishing(Element item, StringBuilder responseBuilder) {
        String name = item.getElementsByTag(TAG_A).text();
        String price = item.getElementsContainingOwnText(GOLD).stream()
                .findFirst()
                .map(Element::text)
                .orElseThrow(() -> new IllegalStateException(CANNOT_FIND_ITEM_PRICE));

        responseBuilder.append(String.format(LUXURY_ITEM_PATTERN, name, price));
    }

    @NonNull
    @Override
    public String checkGoldenItems() throws Exception {
        if (isWeekend()) {
            return retrieveGoldenItems();
        }
        else {
            return GOLDEN_MERCHANT_NOT_PRESENT;
        }
    }

    private String retrieveGoldenItems() throws IOException {
        Document goldenVendorPage = jsoupDocumentRetrievalService.retrieve(ESO_HUB_GOLDEN_VENDOR_URL);

        Element currentWeekCard = findCurrentWeekCard(goldenVendorPage);

        if (currentWeekCard == null) {
            return NO_DATA_FOR_CURRENT_WEEK;
        }

        StringBuilder responseBuilder = new StringBuilder(GOLDEN_HEADER);
        currentWeekCard.getElementsByTag(TAG_TR)
                .forEach(e -> appendGoldenItem(e, responseBuilder));

        return String.format(responseBuilder.toString());
    }

    private void appendGoldenItem(Element item, StringBuilder responseBuilder) {
        Elements cells = item.getElementsByTag(TAG_TD);
        String set = cells.get(1).text();
        String itemType = cells.get(2).text();
        String[] prices = cells.get(3).text().split(SPACE);
        String priceInGold = prices[0];
        String priceInAP = prices[1];

        responseBuilder.append(String.format(GOLDEN_ITEM_PATTERN, set, itemType, priceInGold, priceInAP));
    }

    @NonNull
    @Override
    public String checkServers() throws Exception {
        Document esoHubPage = jsoupDocumentRetrievalService.retrieve(ESO_HUB_URL);

        Element serverTable = esoHubPage.getElementsContainingOwnText(MEGASERVERS_BOX).stream()
                .findFirst()
                .map(Element::parent)
                .map(Element::parent)
                .orElseThrow(() -> new IllegalStateException(CANNOT_FIND_THE_SERVER_STATUS_TABLE));

        StringBuilder responseBuilder = new StringBuilder(SERVERS_HEADER);
        serverTable.getElementsByTag(TAG_TD)
                .forEach(e -> appendServer(e, responseBuilder));

        return String.format(responseBuilder.toString());
    }

    private void appendServer(Element item, StringBuilder responseBuilder) {
        Element span = item.child(0);
        String server = item.text();
        String status = determineServerStatus(span.attr(STYLE_ATTR));

        responseBuilder.append(String.format(SERVER_STATUS_PATTERN, server, status));
    }

    private String determineServerStatus(String dotCss) {
        if (GREEN_DOT_CSS.equals(dotCss)) {
            return STATUS_UP;
        }
        else {
            return STATUS_DOWN;
        }
    }
}
