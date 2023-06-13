package mchorse.game.domain;

import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.game.domain.properties.FloatLevelProperty;
import mchorse.game.domain.properties.IntLevelProperty;
import mchorse.game.domain.properties.LevelProperty;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class PlayerStats
{
    private static final Function<Integer, Integer> LOW_COST = (l) -> l * 25;
    private static final Function<Integer, Integer> STANDARD_COST = (l) -> l * 50;
    private static final Function<Integer, Integer> EXPENSIVE_COST = (l) -> l * 100;
    private static final Function<Integer, Integer> OUTRAGEOUS_COST = (l) -> l * 200;

    public static final Map<String, String> HUMAN_NAMES = new HashMap<String, String>();
    public static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

    /* Energy */
    public IntLevelProperty energyPerClick = new IntLevelProperty("energy_per_click", STANDARD_COST, (l) -> l);
    public IntLevelProperty energyPerSecond = new IntLevelProperty("energy_per_second", LOW_COST, (l) -> (l - 1) * 5);
    public FloatLevelProperty energyPercentage = new FloatLevelProperty("energy_percentage", LOW_COST, (l) -> 1F + (l - 1) * 0.25F).percent();

    /* Crystals */
    public FloatLevelProperty crystalDropRate = new FloatLevelProperty("crystal_drop_rate", OUTRAGEOUS_COST, (l) -> l * 0.01F).percent();

    /* Cursor */
    public IntLevelProperty cursorDamage = new IntLevelProperty("cursor_damage", LOW_COST, (l) -> l);
    public FloatLevelProperty cursorSpellsCooldown = new FloatLevelProperty("cursor_spells_cooldown", STANDARD_COST, (l) -> 1F + (l - 1) * 0.25F).percent();
    public FloatLevelProperty cursorClickRate = new FloatLevelProperty("cursor_click_rate", LOW_COST, (l) -> 1F + (l - 1) * 0.25F).percent();

    /* Core */
    public IntLevelProperty coreDamage = new IntLevelProperty("core_damage", EXPENSIVE_COST, (l) -> l);
    public IntLevelProperty coreHealth = new IntLevelProperty("core_health", LOW_COST, (l) -> 2 + l);
    public IntLevelProperty coreProjectiles = new IntLevelProperty("core_projectiles", OUTRAGEOUS_COST, (l) -> l);
    public FloatLevelProperty coreFireRate = new FloatLevelProperty("core_fire_rate", STANDARD_COST, (l) -> 1F + (l - 1) * 0.25F).percent();

    public final Map<String, LevelProperty> properties = new LinkedHashMap<String, LevelProperty>();

    static
    {
        HUMAN_NAMES.put("energy_per_click", "Energy per click");
        ICONS.put("energy_per_click", Icons.SPHERE);
        HUMAN_NAMES.put("energy_per_second", "Energy per second");
        ICONS.put("energy_per_second", Icons.SPHERE);
        HUMAN_NAMES.put("energy_percentage", "Energy %");
        ICONS.put("energy_percentage", Icons.SPHERE);

        HUMAN_NAMES.put("crystal_drop_rate", "Crystal drop %");
        ICONS.put("crystal_drop_rate", Icons.SHARD);

        HUMAN_NAMES.put("cursor_damage", "Click damage");
        ICONS.put("cursor_damage", Icons.POINTER);
        HUMAN_NAMES.put("cursor_spells_cooldown", "Spells cooldown");
        ICONS.put("cursor_spells_cooldown", Icons.POINTER);
        HUMAN_NAMES.put("cursor_click_rate", "Click rate");
        ICONS.put("cursor_click_rate", Icons.POINTER);

        HUMAN_NAMES.put("core_damage", "Core damage");
        ICONS.put("core_damage", Icons.BLOCK);
        HUMAN_NAMES.put("core_health", "Core HP");
        ICONS.put("core_health", Icons.BLOCK);
        HUMAN_NAMES.put("core_projectiles", "Core projectiles");
        ICONS.put("core_projectiles", Icons.BLOCK);
        HUMAN_NAMES.put("core_fire_rate", "Core fire rate");
        ICONS.put("core_fire_rate", Icons.BLOCK);
    }

    public PlayerStats()
    {
        this.register(this.energyPerClick);
        this.register(this.energyPerSecond);
        this.register(this.energyPercentage);

        this.register(this.crystalDropRate);

        this.register(this.cursorDamage);
        this.register(this.cursorSpellsCooldown);
        this.register(this.cursorClickRate);

        this.register(this.coreDamage);
        this.register(this.coreHealth);
        this.register(this.coreProjectiles);
        this.register(this.coreFireRate);
    }

    public void register(LevelProperty property)
    {
        this.properties.put(property.getId(), property);
    }

    public void reset()
    {
        for (LevelProperty property : this.properties.values())
        {
            property.resetValue();
        }
    }
}