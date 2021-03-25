package org.arkngbot.services;

import org.arkngbot.datastructures.enums.TESRace;
import org.arkngbot.datastructures.enums.TESSex;
import org.arkngbot.services.impl.LoreNameGeneratorServiceImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoreNameGeneratorServiceTest {

    private static final String UESP_NAME_LINK = "https://en.uesp.net/wiki/Lore:%s_Names";
    private static final String NORD_SECTION_ID = "Clan_Names_and_Titles";
    private static final String FAMILY_NAMES = "_Family_Names";
    private static final String HEADER_LEVEL_3 = "h3";
    private static final String HEADER_LEVEL_2 = "h2";
    private static final String PARAGRAPH_TAG = "p";
    private static final String REDGUARD_SECTION_ID = "Redguard_Family_Names_and_Derivation_Names";
    private static final String FIRST_NAMES_SECTION_PATTERN = "%s_%s_Names";
    private static final String JOHN_PARAGRAPH = "2x: John (1, 2)";
    private static final String JANE_PARAGRAPH = "2x: Jane (1, 2)";
    private static final String DOE_PARAGRAPH = "2x: Doe (1, 2)";
    private static final String AL_SENTINEL_PARAGRAPH = "2x: al-Sentinel (1, 2)";
    private static final String JOHN_DOE_NAME = "John Doe";
    private static final String JANE_DOE_NAME = "Jane Doe";
    private static final String JOHN_AL_SENTINEL_NAME = "John al-Sentinel";
    private static final String JOHN_NAME = "John";
    private static final String FHARUN_NAME = "Fharun";
    private static final String PHOOM_NAME = "Phoom";
    private static final String JOHN_GRO_FHARUN_NAME = "John gro-Fharun";
    private static final String JANE_GRA_FHARUN_NAME = "Jane gra-Fharun";
    private static final String JOHN_PHOOM_NAME = "John Phoom";
    private static final String JANE_PHOOM_NAME = "Jane Phoom";
    private static final String TAG_DIV = "div";

    private LoreNameGeneratorService loreNameGeneratorService;

    private JsoupDocumentRetrievalService jsoupDocumentRetrievalService;

    @BeforeEach
    public void setUp() {
        jsoupDocumentRetrievalService = mock(JsoupDocumentRetrievalService.class);
        loreNameGeneratorService = new LoreNameGeneratorServiceImpl(jsoupDocumentRetrievalService);
    }

    @Test
    public void shouldGenerateBretonMaleName() throws Exception {
        mockFlow(TESRace.BRETON, TESSex.MALE, JOHN_PARAGRAPH, DOE_PARAGRAPH, HEADER_LEVEL_2);

        String name = loreNameGeneratorService.generateLoreName(TESRace.BRETON, TESSex.MALE);

        assertThat(name, is(JOHN_DOE_NAME));
    }

    @Test
    public void shouldGenerateAshlanderFemaleName() throws Exception {
        mockFlow(TESRace.ASHLANDER, TESSex.FEMALE, JANE_PARAGRAPH, DOE_PARAGRAPH, HEADER_LEVEL_3);

        String name = loreNameGeneratorService.generateLoreName(TESRace.ASHLANDER, TESSex.FEMALE);

        assertThat(name, is(JANE_DOE_NAME));
    }

    @Test
    public void shouldGenerateRedguardMaleName() throws Exception {
        mockFlow(TESRace.REDGUARD, TESSex.MALE, JOHN_PARAGRAPH, AL_SENTINEL_PARAGRAPH, HEADER_LEVEL_2);

        String name = loreNameGeneratorService.generateLoreName(TESRace.REDGUARD, TESSex.MALE);

        assertThat(name, is(JOHN_AL_SENTINEL_NAME));
    }

    @Test
    public void shouldGenerateReachmanMaleName() throws Exception {
        mockFlow(TESRace.REACHMAN, TESSex.MALE, JOHN_PARAGRAPH, DOE_PARAGRAPH, HEADER_LEVEL_3);

        String name = loreNameGeneratorService.generateLoreName(TESRace.REACHMAN, TESSex.MALE);

        assertThat(name, is(JOHN_NAME));
    }

    @Test
    public void shouldGenerateNordFemaleName() throws Exception {
        mockFlow(TESRace.NORD, TESSex.FEMALE, JANE_PARAGRAPH, DOE_PARAGRAPH, HEADER_LEVEL_2);

        String name = loreNameGeneratorService.generateLoreName(TESRace.NORD, TESSex.FEMALE);

        assertThat(name, is(JANE_DOE_NAME));
    }

    @Test
    public void shouldGenerateOrcMaleName() throws Exception {
        mockFlow(TESRace.ORC, TESSex.MALE, JOHN_PARAGRAPH, FHARUN_NAME, HEADER_LEVEL_2, PHOOM_NAME);

        String name = loreNameGeneratorService.generateLoreName(TESRace.ORC, TESSex.MALE);

        assertThat(name, anyOf(is(JOHN_GRO_FHARUN_NAME), is(JOHN_PHOOM_NAME)));
    }

    @Test
    public void shouldGenerateOrcFemaleName() throws Exception {
        mockFlow(TESRace.ORC, TESSex.FEMALE, JANE_PARAGRAPH, FHARUN_NAME, HEADER_LEVEL_2, PHOOM_NAME);

        String name = loreNameGeneratorService.generateLoreName(TESRace.ORC, TESSex.FEMALE);

        assertThat(name, anyOf(is(JANE_GRA_FHARUN_NAME), is(JANE_PHOOM_NAME)));
    }

    private void mockFlow(TESRace race, TESSex sex, String firstNameParagraph, String familyNameParagraph, String delimiterTagName) throws Exception {
        mockFlow(race, sex, firstNameParagraph, familyNameParagraph, delimiterTagName, null);
    }

    private void mockFlow(TESRace race, TESSex sex, String firstNameParagraph, String familyNameParagraph, String delimiterTagName, String alternativeFamilyNameParagraph) throws Exception {
        Document page = mock(Document.class);
        Element firstNameSection = new Element(TAG_DIV);
        Element familyNameSection = new Element(TAG_DIV);

        mockMainPage(race, page);
        when(page.getElementById(String.format(FIRST_NAMES_SECTION_PATTERN, sex.getName(), race.getName()))).thenReturn(firstNameSection);
        mockFamilyNameSection(race, page, familyNameSection);

        mockFlowCommon(firstNameSection, firstNameParagraph, null, delimiterTagName);
        mockFlowCommon(familyNameSection, familyNameParagraph, alternativeFamilyNameParagraph, delimiterTagName);
    }

    private void mockFlowCommon(Element section, String paragraphText, String alternativeParagraphText, String delimiterTagName) {
        Element paragraphTag = new Element(PARAGRAPH_TAG);
        Element delimiterTag = new Element(delimiterTagName);

        Element grandparent = new Element(TAG_DIV);
        Element parent = new Element(TAG_DIV);
        parent.appendChild(section);
        grandparent.appendChild(parent);
        grandparent.appendChild(paragraphTag);

        if (alternativeParagraphText != null) {
            Element alternativeParagraphTag = new Element(PARAGRAPH_TAG);
            alternativeParagraphTag.text(alternativeParagraphText);
            grandparent.appendChild(alternativeParagraphTag);
        }

        grandparent.appendChild(delimiterTag);
        paragraphTag.text(paragraphText);
    }

    private void mockMainPage(TESRace race, Document page) throws Exception {
        if (race == TESRace.ASHLANDER) {
            race = TESRace.DUNMER;
        }
        when(jsoupDocumentRetrievalService.retrieve(String.format(UESP_NAME_LINK, race.getName()))).thenReturn(page);
    }

    private void mockFamilyNameSection(TESRace race, Document page, Element familyNameSection) {
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

        when(page.getElementById(sectionId)).thenReturn(familyNameSection);
    }
}
