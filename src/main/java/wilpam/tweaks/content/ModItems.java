package wilpam.tweaks.content;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
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

import java.util.ArrayList;

public final class ModItems {
    public record ItemEntry<T extends Item>(Identifier id, T item){

        public static ItemEntry<BasicPolymerItem> createBasicItemOf(String id) {
            return createBasicItemOf(Identifier.fromNamespaceAndPath(ID, id), Items.CLAY_BALL, new Item.Properties());
        }

        public static ItemEntry<BasicPolymerItem> createBasicItemOf(String id, Item.Properties properties) {
            return createBasicItemOf(Identifier.fromNamespaceAndPath(ID, id), Items.CLAY_BALL, properties);
        }

        public static ItemEntry<BasicPolymerItem> createBasicItemOf(Identifier id, Item fallback, Item.Properties properties){
            properties = properties.setId(ResourceKey.create(Registries.ITEM, id));
            var item = new BasicPolymerItem(properties, Identifier.fromNamespaceAndPath(ID, "-/item/" + id.getPath()), fallback);
            return new ItemEntry<>(id, item);
        }

    }

    public static final String ID = "wilpam_tweaks";
    public static ArrayList<ItemEntry<?>> ITEMS = new ArrayList<>();

    public static final Consumable SLIMEBALL_SUBSTRATE_EFFECT = Consumables.defaultFood()
            // The duration is in ticks, 20 ticks = 1 second
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.OOZING, 60 * 20, 1), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 6 * 20, 1), 1.0f))
            .build();

    public static final ItemEntry<BasicPolymerItem> DOUGH = ItemEntry.createBasicItemOf(
            "dough",
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(2).saturationModifier(1.2f).build()
            ));

    public static final ItemEntry<BasicPolymerItem> SLIMEBALL_SUBSTRATE = ItemEntry.createBasicItemOf(
            "slimeball_substrate",
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(1).saturationModifier(0).build(),
                    SLIMEBALL_SUBSTRATE_EFFECT
            ));


    public static final ItemEntry<BasicPolymerItem> CHEESE = ItemEntry.createBasicItemOf(
            "cheese",
            new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(3).saturationModifier(1.5f).build()
            ));

    public static final ItemEntry<BasicPolymerItem> OIL = ItemEntry.createBasicItemOf(
            "oil",
            new Item.Properties().component(DataComponents.COOKING_FUEL,
                    new CookingFuel(new ResolvableInt.Constant(800), new ResolvableFloat.Constant(2.0f))));

    public static final ItemEntry<BasicPolymerItem> OIL_WAX = ItemEntry.createBasicItemOf(
            "oil_wax",
            new Item.Properties().component(DataComponents.COOKING_FUEL,
                    new CookingFuel(new ResolvableInt.Constant(600), new ResolvableFloat.Constant(4.0f))));

    public static final ItemEntry<BasicPolymerItem> PREPOLYMERIZED_PLATE = ItemEntry.createBasicItemOf("prepolymerized_plate");

    public static final ItemEntry<BasicPolymerItem> PLASTIC_PLATE = ItemEntry.createBasicItemOf("plastic_plate");

    public static final ItemEntry<BasicPolymerItem> STAMP = ItemEntry.createBasicItemOf("stamp");

    public static final ItemEntry<BasicPolymerItem> ICON = ItemEntry.createBasicItemOf("icon");

    private ModItems() {
    }

    public static void register() {
        registerSingle(DOUGH);
        registerSingle(SLIMEBALL_SUBSTRATE);
        registerSingle(ICON);
        registerSingle(CHEESE);
        registerSingle(OIL);
        registerSingle(OIL_WAX);
        registerSingle(PREPOLYMERIZED_PLATE);
        registerSingle(PLASTIC_PLATE);
        registerSingle(STAMP);
    }

    public static void registerSingle(ItemEntry<?> entry) {
        Registry.register(BuiltInRegistries.ITEM, entry.id, entry.item);
        ITEMS.add(entry);
    }
}