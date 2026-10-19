package slimeknights.mantle.recipe.condition;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.loot.MantleLoot;

/** Inverted form of {@link TagEmptyCondition} as filled is way more common a desire than empty. */
public class TagFilledCondition<T> extends TagCondition<T> implements LootItemCondition {
  public static final com.mojang.serialization.MapCodec<TagFilledCondition<?>> CODEC = TagCondition.makeCodec(TagFilledCondition::new);

  public TagFilledCondition(TagKey<T> tag) {
    super(tag);
  }

  public TagFilledCondition(ResourceKey<? extends Registry<T>> registry, ResourceLocation name) {
    this(TagKey.create(registry, name));
  }

  @Override
  public com.mojang.serialization.MapCodec<? extends ICondition> codec() {
    return CODEC;
  }

  @Override
  public LootItemConditionType getType() {
    return MantleLoot.TAG_FILLED;
  }

  @Override
  public boolean test(IContext context) {
    if (!context.getTag(tag).isEmpty()) {
      return true;
    }
    if ("forge".equals(tag.location().getNamespace())) {
      TagKey<T> cTag = TagKey.create(tag.registry(), ResourceLocation.fromNamespaceAndPath("c", tag.location().getPath()));
      return !context.getTag(cTag).isEmpty();
    }
    return false;
  }

  @Override
  public boolean test(LootContext context) {
    Registry<T> registry = registry(context);
    if (registry == null) {
      return false;
    }
    if (registry.getTagOrEmpty(tag).iterator().hasNext()) {
      return true;
    }
    if ("forge".equals(tag.location().getNamespace())) {
      TagKey<T> cTag = TagKey.create(tag.registry(), ResourceLocation.fromNamespaceAndPath("c", tag.location().getPath()));
      return registry.getTagOrEmpty(cTag).iterator().hasNext();
    }
    return false;
  }
}
