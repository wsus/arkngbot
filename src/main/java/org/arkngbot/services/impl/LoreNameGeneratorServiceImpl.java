package org.arkngbot.services.impl;

import org.arkngbot.datastructures.enums.TESRace;
import org.arkngbot.datastructures.enums.TESSex;
import org.arkngbot.services.JsoupDocumentRetrievalService;
import org.arkngbot.services.LoreNameGeneratorService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LoreNameGeneratorServiceImpl implements LoreNameGeneratorService {

    private static final String UESP_NAME_LINK = "https://en.uesp.net/wiki/Lore:%s_Names";
    private static final List<String> DISALLOWED_STRINGS = new ArrayList<>();
    private static final String SPACE = " ";
    private static final String NORD_SECTION_ID = "Clan_Names_and_Titles";
    private static final String FAMILY_NAMES = "_Family_Names";
    private static final String MALE_ORC_SURNAME_PREFIX = "gro-";
    private static final String FEMALE_ORC_SURNAME_PREFIX = "gra-";
    private static final String HEADER_LEVEL_3 = "h3";
    private static final String HEADER_LEVEL_2 = "h2";
    private static final String PARAGRAPH_TAG = "p";
    private static final String SUPER_NAME_EXTRACTING_REGEX = "(the |at-|af-|al-)*[A-Z][a-zï']*([ -][A-Z][a-zï']*)*(?=,|\\n| \\(| \\d|$)";
    private static final String COMMA_AND_SPACE = ", ";
    private static final String PHOOM = "Phoom";
    private static final String HAMMERDEATH = "Hammerdeath";
    private static final String REDGUARD_SECTION_ID = "Redguard_Family_Names_and_Derivation_Names";
    private static final String FIRST_NAMES_SECTION_PATTERN = "%s_%s_Names";

    static {
        DISALLOWED_STRINGS.add("Therefore, ");
        DISALLOWED_STRINGS.add("No explanation is given for this");
    }

    private final JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    @Autowired
    public LoreNameGeneratorServiceImpl(JsoupDocumentRetrievalService jsoupDocumentRetrievalService) {
        this.jsoupDocumentRetrievalService = jsoupDocumentRetrievalService;
    }

    @Override
    public String generateLoreName(TESRace race, TESSex sex) throws Exception {

        Document page = jsoupDocumentRetrievalService.retrieve(retrievePageUrl(race));
        Random randomizer = new Random();
        String name = generateFirstName(race, sex, page, randomizer);
        if (race.isHasSurname()) {
            String familyName = generateFamilyName(race, sex, page, randomizer);
            name = name + SPACE + familyName;
        }
        return name;
    }

    private String retrievePageUrl(TESRace race) {
        TESRace raceToUse;
        if (race == TESRace.ASHLANDER) {
            raceToUse = TESRace.DUNMER;
        }
        else {
            raceToUse = race;
        }

        return String.format(UESP_NAME_LINK, raceToUse.getName());
    }

    private String generateFirstName(TESRace race, TESSex sex, Document page, Random randomizer) {
        String sectionId = String.format(FIRST_NAMES_SECTION_PATTERN, sex.getName(), race.getName());
        return doGenerate(race, sectionId, page, randomizer);
    }

    private String generateFamilyName(TESRace race, TESSex sex, Document page, Random randomizer) {
        String sectionId = determineSectionId(race);
        String familyName;
        if (race == TESRace.ORC) {
            familyName = generateOrcishFamilyName(sex, sectionId, page, randomizer);
        }
        else {
            familyName = doGenerate(race, sectionId, page, randomizer);
        }

        return familyName;
    }

    private String adjustFamilyNameForOrcs(TESSex sex, String familyName) {
        if (sex == TESSex.MALE) {
            familyName = MALE_ORC_SURNAME_PREFIX + familyName;
        }
        else {
            familyName = FEMALE_ORC_SURNAME_PREFIX + familyName;
        }
        return familyName;
    }

    private String determineSectionId(TESRace race) {
        String sectionId;
        if (race == TESRace.NORD) {
            sectionId = NORD_SECTION_ID;
        }
        else if (race == TESRace.REDGUARD) {
            sectionId = REDGUARD_SECTION_ID;
        }
        else {
            sectionId = race.getName() + FAMILY_NAMES;
        }
        return sectionId;
    }

    private String doGenerate(TESRace race, String sectionId, Document page, Random randomizer) {
        Elements section = page.getElementById(sectionId).parent().nextElementSiblings();
        String sectionDelimiterTag = determineSectionDelimiter(race);

        List<Element> interestingParagraphs = collectInterestingParagraphs(section, sectionDelimiterTag);

        List<String> names = extractNames(interestingParagraphs);

        return getRandomElement(names, randomizer);
    }

    private String generateOrcishFamilyName(TESSex sex, String sectionId, Document page, Random randomizer) {
        Elements section = page.getElementById(sectionId).parent().nextElementSiblings();
        String sectionDelimiterTag = determineSectionDelimiter(TESRace.ORC);

        List<Element> interestingParagraphs = collectInterestingParagraphs(section, sectionDelimiterTag);

        List<String> names = extractOrcishFamilyNames(interestingParagraphs, sex);

        return getRandomElement(names, randomizer);
    }

    private List<Element> collectInterestingParagraphs(Elements section, String sectionDelimiterTag) {
        List<Element> interestingParagraphs = new ArrayList<>();

        for (Element e : section) {
            if (e.tagName().equals(PARAGRAPH_TAG)) {
                interestingParagraphs.add(e);
            }
            if (e.tagName().equals(sectionDelimiterTag)) {
                break;
            }
        }
        return interestingParagraphs;
    }

    private String determineSectionDelimiter(TESRace race) {
        String sectionDelimiterTag;
        // Reachmen and Ashlanders are subpages and use different header levels
        if (race == TESRace.REACHMAN || race == TESRace.ASHLANDER) {
            sectionDelimiterTag = HEADER_LEVEL_3;
        }
        else {
            sectionDelimiterTag = HEADER_LEVEL_2;
        }
        return sectionDelimiterTag;
    }

    private List<String> extractNames(List<Element> paragraphs) {
        Pattern pattern = Pattern.compile(SUPER_NAME_EXTRACTING_REGEX);

        String namesAsString = paragraphs.stream()
                .map(Element::text)
                .filter(p -> DISALLOWED_STRINGS.stream().noneMatch(p::contains))
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(Matcher::reset)
                .flatMap(m -> matchAllNames(m).stream())
                .distinct()
                .sorted()
                .collect(Collectors.joining(COMMA_AND_SPACE));

        return Arrays.asList(namesAsString.split(COMMA_AND_SPACE));
    }

    private List<String> extractOrcishFamilyNames(List<Element> paragraphs, TESSex sex) {
        Pattern pattern = Pattern.compile(SUPER_NAME_EXTRACTING_REGEX);
        Map<Boolean, List<String>> paragraphContents = paragraphs.stream()
                .map(Element::text)
                .filter(p -> DISALLOWED_STRINGS.stream().noneMatch(p::contains))
                .collect(Collectors.groupingBy(this::isParagraphWithOrcClanNames));

        String clanNames = extractOrcishNames(pattern, paragraphContents.get(true));
        String otherNames = extractOrcishNames(pattern, paragraphContents.get(false));

        List<String> clanNamesList = Arrays.stream(clanNames.split(COMMA_AND_SPACE))
                .map(n -> adjustFamilyNameForOrcs(sex, n))
                .collect(Collectors.toList());

        List<String> otherNamesList = Arrays.asList(otherNames.split(COMMA_AND_SPACE));

        return Stream.concat(clanNamesList.stream(), otherNamesList.stream())
                .collect(Collectors.toList());
    }

    private boolean isParagraphWithOrcClanNames(String paragraphContent) {
        return !paragraphContent.contains(PHOOM) && !paragraphContent.contains(HAMMERDEATH);
    }

    private String extractOrcishNames(Pattern pattern, List<String> paragraphContents) {
        return paragraphContents.stream()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(Matcher::reset)
                .flatMap(m -> matchAllNames(m).stream())
                .distinct()
                .sorted()
                .collect(Collectors.joining(COMMA_AND_SPACE));
    }

    private List<String> matchAllNames(Matcher matcher) {
        List<String> names = new ArrayList<>();
        while(matcher.find()) {
            names.add(matcher.group());
        }

        return names;
    }

    private <T> T getRandomElement(List<T> collection, Random randomizer) {
        return collection.get(randomizer.nextInt(collection.size()));
    }
}
