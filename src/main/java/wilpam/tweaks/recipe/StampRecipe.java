package wilpam.tweaks.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.content.ModCustomRecipes;
import wilpam.tweaks.content.ModItems;
import wilpam.tweaks.mixin_helpers.CraftingInputMixinInterface;

import java.util.List;

public class StampRecipe extends CustomRecipe {
    public static final MapCodec<StampRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Ingredient.CODEC.fieldOf("stamp").forGetter(o -> o.stamp)
                    )
                    .apply(i, StampRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, StampRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            o -> o.stamp,
            StampRecipe::new
    );
    private final Ingredient stamp;

    public StampRecipe(final Ingredient stamp) {
        this.stamp = stamp;
    }

    public boolean matches(CraftingInput craftingInput, @NonNull Level level) {
        if (craftingInput.ingredientCount() == 2) {
            ItemStack itemStack = null;

            for (int i = 0; i < craftingInput.size(); i++) {
                ItemStack itemStack2 = craftingInput.getItem(i);
                if (!itemStack2.isEmpty()) {
                    if (itemStack != null) {
                        if (itemStack.getItem() == ModItems.STAMP.item() && !(itemStack2.getItem() == ModItems.STAMP.item())) {
                            return true;
                        } else if (itemStack2.getItem() == ModItems.STAMP.item()) {
                            return true;
                        }
                    }

                    itemStack = itemStack2;
                }
            }
        }
        return false;
    }

    public @NonNull ItemStack assemble(CraftingInput craftingInput) {
        //noinspection unused
        ItemStack stamp = ItemStack.EMPTY;
        ItemStack other = ItemStack.EMPTY;
        for (int i = 0; i < craftingInput.size(); i++) {
            ItemStack itemStack = craftingInput.getItem(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() == ModItems.STAMP.item()) {
                    //noinspection UnusedAssignment
                    stamp = itemStack;
                } else {
                    other = itemStack;
                }
            }
        }
        ItemStack stamped_item = other.copyWithCount(1);
        String playerName = ((CraftingInputMixinInterface) craftingInput).getPlayerName();
        Component stamp_text = Component.translatable("item.wilpam_tweaks.stamp.signature", Component.literal(playerName))
                .withStyle(ChatFormatting.RED);
        stamped_item.set(DataComponents.LORE, new ItemLore(List.of(stamp_text)));
        return stamped_item;
    }

    @Override
    public @NonNull NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
        NonNullList<ItemStack> nonNullList = NonNullList.withSize(craftingInput.size(), ItemStack.EMPTY);

        for (int i = 0; i < nonNullList.size(); i++) {
            ItemStack itemStack = craftingInput.getItem(i);
            if (itemStack.getItem() == ModItems.STAMP.item() && !itemStack.nextDamageWillBreak()) {
                ItemStack copy = itemStack.copyWithCount(1);
                copy.setDamageValue(itemStack.getDamageValue() + 1);
                nonNullList.set(i, copy);
            }
        }

        return nonNullList;
    }

    @Override
    public @NonNull RecipeSerializer<StampRecipe> getSerializer() { return ModCustomRecipes.STAMP_RECIPE_SERIALIZER; }
}