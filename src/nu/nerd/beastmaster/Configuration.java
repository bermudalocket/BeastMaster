package nu.nerd.beastmaster;

import java.util.HashSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import nu.nerd.beastmaster.mobs.MobType;
import nu.nerd.beastmaster.objectives.ObjectiveType;
import nu.nerd.beastmaster.zones.Zone;

// ----------------------------------------------------------------------------
/**
 * Reads and exposes the plugin configuration.
 */
public class Configuration {
    // ------------------------------------------------------------------------
    /**
     * If true, log the configuration on reload.
     */
    public boolean DEBUG_CONFIG;

    /**
     * If true, log replacement of skeletons by wither skeletons.
     */
    public boolean DEBUG_REPLACE;

    /**
     * If true, log elapsed times for BlockStore API calls.
     */
    public boolean DEBUG_BLOCKSTORE;

    /**
     * Probability, in the range [0.0,1.0] that a plains biome skeleton spawn in
     * the nether environment will be replaced by a wither skeleton.
     */
    public double CHANCE_WITHER_SKELETON;

    /**
     * EntityTypes that cannot be used in custom mob types.
     */
    public HashSet<EntityType> EXCLUDED_ENTITY_TYPES = new HashSet<>();

    // ------------------------------------------------------------------------
    /**
     * Load the plugin configuration.
     */
    public void reload() {
        BeastMaster.PLUGIN.reloadConfig();
        Logger logger = BeastMaster.PLUGIN.getLogger();
        FileConfiguration config = BeastMaster.PLUGIN.getConfig();
        DEBUG_CONFIG = config.getBoolean("debug.config");
        DEBUG_REPLACE = config.getBoolean("debug.replace");
        DEBUG_BLOCKSTORE = config.getBoolean("debug.blockstore");
        CHANCE_WITHER_SKELETON = config.getDouble("chance.wither-skeleton");

        EXCLUDED_ENTITY_TYPES.clear();
        for (String excluded : config.getStringList("excluded-entity-types")) {
            try {
                EXCLUDED_ENTITY_TYPES.add(EntityType.valueOf(excluded));
            } catch (IllegalArgumentException ex) {
                logger.info("Invalid excluded entity type: " + excluded);
            }
        }

        BeastMaster.ITEMS.load(config, logger);
        BeastMaster.ZONES.load(config, logger);
        BeastMaster.MOBS.load(config, logger);
        BeastMaster.LOOTS.load(config, logger);
        BeastMaster.OBJECTIVE_TYPES.load(config, logger);

        if (DEBUG_CONFIG) {
            logger.info("Configuration:");
            logger.info("DEBUG_REPLACE: " + DEBUG_REPLACE);
            logger.info("DEBUG_BLOCKSTORE: " + DEBUG_BLOCKSTORE);
            logger.info("CHANCE_WITHER_SKELETON: " + CHANCE_WITHER_SKELETON);

            logger.info("EXCLUDED_ENTITY_TYPES: " + EXCLUDED_ENTITY_TYPES.stream()
            .map(EntityType::toString).collect(Collectors.joining(", ")));

            logger.info("ZONES: " + BeastMaster.ZONES.getZones().stream()
            .map(Zone::getDescription).collect(Collectors.joining(", ")));

            logger.info("ITEMS: ");
            for (Item item : BeastMaster.ITEMS.getAllItems()) {
                logger.info(item.getId() + ": " + Util.getItemDescription(item.getItemStack()));
            }

            logger.info("LOOTS: ");
            for (DropSet drops : BeastMaster.LOOTS.getDropSets()) {
                logger.info(drops.getDescription());
            }

            logger.info("MOBS: ");
            for (MobType mobType : BeastMaster.MOBS.getAllMobTypes()) {
                logger.info(mobType.getShortDescription());
            }

            logger.info("OBJECTIVE_TYPES: ");
            for (ObjectiveType objectiveType : BeastMaster.OBJECTIVE_TYPES.getObjectiveTypes()) {
                logger.info(objectiveType.getDescription());
            }
        }
    } // reload

    // ------------------------------------------------------------------------
    /**
     * Save updated configuration.
     */
    public void save() {
        FileConfiguration config = BeastMaster.PLUGIN.getConfig();
        Logger logger = BeastMaster.PLUGIN.getLogger();

        BeastMaster.ZONES.save(config, logger);
        BeastMaster.ITEMS.save(config, logger);
        BeastMaster.LOOTS.save(config, logger);
        BeastMaster.MOBS.save(config, logger);
        BeastMaster.OBJECTIVE_TYPES.save(config, logger);
        BeastMaster.PLUGIN.saveConfig();
    }

    // ------------------------------------------------------------------------
} // class Configuration