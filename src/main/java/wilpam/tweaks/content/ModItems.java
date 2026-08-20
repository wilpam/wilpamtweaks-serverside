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
    public record ItemEntry<T extends Item>(Identifier id, T item){
        /// This also handles setting the ID property
        public static ItemEntry<BasicPolymerItem> createBasicItemOf(Identifier id, Item fallback){
            return createBasicItemOf(id, fallback, new Item.Properties());
        }

        /// This also handles setting the ID property
        public static ItemEntry<BasicPolymerItem> createBasicItemOf(Identifier id, Item fallback, Item.Properties properties){
            properties = properties.setId(ResourceKey.create(Registries.ITEM, id));
            var item = new BasicPolymerItem(properties, Identifier.fromNamespaceAndPath(ID, "-/item/" + id.getPath()), fallback);
            return new ItemEntry<>(id, item);
        }
    }

    public static final String ID = "wilpam_tweaks";

    public static final Consumable SLIMEBALL_SUBSTRATE_EFFECT = Consumables.defaultFood()
            // The duration is in ticks, 20 ticks = 1 second
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.OOZING, 60 * 20, 1), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 6 * 20, 1), 1.0f))
            .build();

    public static final ItemEntry<BasicPolymerItem> DOUGH = ItemEntry.createBasicItemOf(
            Identifier.fromNamespaceAndPath(ID, "dough"),
            Items.CLAY_BALL,
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(2).saturationModifier(1.2f).build()
            ));

    public static final ItemEntry<BasicPolymerItem> SLIMEBALL_SUBSTRATE = ItemEntry.createBasicItemOf(
            Identifier.fromNamespaceAndPath(ID, "slimeball_substrate"),
            Items.SLIME_BALL,
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(1).saturationModifier(0).build(),
                    SLIMEBALL_SUBSTRATE_EFFECT
            ));


    public static final ItemEntry<BasicPolymerItem> CHEESE = ItemEntry.createBasicItemOf(
            Identifier.fromNamespaceAndPath(ID, "cheese"),
            Items.MILK_BUCKET,
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(3).saturationModifier(1.5f).build()
            ));

    public static final ItemEntry<BasicPolymerItem> OIL = ItemEntry.createBasicItemOf(
            Identifier.fromNamespaceAndPath(ID, "oil"),
            Items.DRAGON_BREATH,
            new Item.Properties().component(ModFuelSpeeds.FUEL_SPEED, 2.0));

    public static final ItemEntry<BasicPolymerItem> STAMP = ItemEntry.createBasicItemOf(
            Identifier.fromNamespaceAndPath(ID, "stamp"),
            Items.CLOCK);

    public static final ItemEntry<BasicPolymerItem> WILPAM_ICON = ItemEntry.createBasicItemOf(
            Identifier.fromNamespaceAndPath(ID, "wilpam_icon"),
            Items.FIREWORK_STAR);

    private ModItems() {
    }

    public static void register() {
        registerSingle(DOUGH);
        registerSingle(SLIMEBALL_SUBSTRATE);
        registerSingle(WILPAM_ICON);
        registerSingle(CHEESE);
        registerSingle(OIL);
        registerSingle(STAMP);
        FuelValueEvents.BUILD.register((builder, _) -> builder.add(ModItems.OIL.item, 200 * 12));
    }

    public static void registerSingle(ItemEntry<?> entry) {
        Registry.register(BuiltInRegistries.ITEM, entry.id, entry.item);
    }
}