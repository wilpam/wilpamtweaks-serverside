package wilpam.tweaks.content;

import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import wilpam.tweaks.item.BasicPolymerItem;

public final class ModItems {
    public static final String ID = "wilpam_tweaks";

    public static final Identifier DOUGH_ID = Identifier.fromNamespaceAndPath(ID, "dough");
    public static final Identifier WILPAM_ICON_ID = Identifier.fromNamespaceAndPath(ID, "wilpam_icon");
    public static final Identifier SLIMEBALL_SUBSTRATE_ID = Identifier.fromNamespaceAndPath(ID, "slimeball_substrate");
    public static final Identifier CHEESE_ID = Identifier.fromNamespaceAndPath(ID, "cheese");
    public static final Identifier OIL_ID = Identifier.fromNamespaceAndPath(ID, "oil");
    public static final Identifier STAMP_ID = Identifier.fromNamespaceAndPath(ID, "stamp");

    public static final BasicPolymerItem DOUGH = new BasicPolymerItem(
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(2).saturationModifier(1.2f).build()
            ).setId(ResourceKey.create(Registries.ITEM, DOUGH_ID)),
            Identifier.fromNamespaceAndPath(ID, "-/item/dough"),
            Items.CLAY_BALL);

    public static final Consumable SLIMEBALL_SUBSTRATE_EFFECT = Consumables.defaultFood()
            // The duration is in ticks, 20 ticks = 1 second
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.OOZING, 60 * 20, 1), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 6 * 20, 1), 1.0f))
            .build();

    public static final BasicPolymerItem SLIMEBALL_SUBSTRATE = new BasicPolymerItem(
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(1).saturationModifier(0).build(),
                    SLIMEBALL_SUBSTRATE_EFFECT
            ).setId(ResourceKey.create(Registries.ITEM, SLIMEBALL_SUBSTRATE_ID)),
            Identifier.fromNamespaceAndPath(ID, "-/item/slimeball_substrate"),
            Items.SLIME_BALL);

    public static final BasicPolymerItem CHEESE = new BasicPolymerItem(
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(3).saturationModifier(1.5f).build()
            ).setId(ResourceKey.create(Registries.ITEM, CHEESE_ID)),
            Identifier.fromNamespaceAndPath(ID, "-/item/cheese"),
            Items.MILK_BUCKET);

    public static final BasicPolymerItem OIL = new BasicPolymerItem(
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, OIL_ID)),
            Identifier.fromNamespaceAndPath(ID, "-/item/wilpam_icon"),
            Items.DRAGON_BREATH);

    public static final BasicPolymerItem STAMP = new BasicPolymerItem(
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, STAMP_ID)),
            Identifier.fromNamespaceAndPath(ID, "-/item/wilpam_icon"),
            Items.CLOCK);

    public static final BasicPolymerItem WILPAM_ICON = new BasicPolymerItem(
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, WILPAM_ICON_ID)),
            Identifier.fromNamespaceAndPath(ID, "-/item/wilpam_icon"),
            Items.FIREWORK_STAR);

    private ModItems() {
    }

    public static void register() {
        registerSingle(DOUGH_ID, DOUGH);
        registerSingle(SLIMEBALL_SUBSTRATE_ID, SLIMEBALL_SUBSTRATE);
        registerSingle(WILPAM_ICON_ID, WILPAM_ICON);
        registerSingle(CHEESE_ID, CHEESE);
        registerSingle(OIL_ID, OIL);
        registerSingle(STAMP_ID, STAMP);
        FuelValueEvents.BUILD.register((builder, _) -> builder.add(ModItems.OIL, 200 * 8));
    }

    public static void registerSingle(Identifier id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }
}