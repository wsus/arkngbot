package org.arkngbot.services;

import org.arkngbot.datastructures.enums.TESRace;
import org.arkngbot.datastructures.enums.TESSex;
import org.springframework.lang.NonNull;

/**
 * A service for generating lore-friendly TES character names.
 */
public interface LoreNameGeneratorService {

    /**
     * Generates a random lore name for the given race and sex.
     *
     * @param race The race to generate the name for. Can be any of the main 10 TES Races;
     *             Ashlander and Reachman are also available separately since they have different naming patterns.
     * @param sex  The sex to generate the name for (name or female).
     * @return The generated name.
     * @throws Exception if anything goes wrong.
     */
    @NonNull
    String generateLoreName(@NonNull TESRace race, @NonNull TESSex sex) throws Exception;
}
