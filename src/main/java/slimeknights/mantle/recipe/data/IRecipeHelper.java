package slimeknights.mantle.recipe.data;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.registration.object.IdAwareObject;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.data.recipes.RecipeOutput;

/**
 * Interface for common resource location and condition methods
 */
@SuppressWarnings("unused")
public interface IRecipeHelper {
  /** Gets the ID of the item */
  default ResourceLocation id(net.minecraft.world.level.ItemLike item) {
    return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem());
  }
  /** Gets the ID of the mod adding recipes */
  String getModId();

  /**
   * Gets a resource location for the mod
   * @param name  Location path
   * @return  Location for the mod
   */
  default ResourceLocation modResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(getModId(), name);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param loc     Name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  default ResourceLocation wrap(ResourceLocation loc, String prefix, String suffix) {
    return modResource(prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param location  Entry registry name to use
   * @param prefix    Prefix value
   * @return  Resource location path
   */
  default ResourceLocation prefix(ResourceLocation location, String prefix) {
    return modResource(prefix + location.getPath());
  }

  /**
   * Suffixes the resource location path with the given value
   * @param location  Entry registry name to use
   * @param suffix    Suffix value
   * @return  Resource location path
   */
  default ResourceLocation suffix(ResourceLocation location, String suffix) {
    return modResource(location.getPath() + suffix);
  }  /* Other named object location helpers */

  /**
   * Wraps the registry object ID in the given prefix and suffix
   * @param location  Object to use for location
   * @param prefix    Path prefix
   * @param suffix    Path suffix
   * @return  Location with the given prefix and suffix
   */
  default ResourceLocation wrap(IdAwareObject location, String prefix, String suffix) {
    return wrap(location.getId(), prefix, suffix);
  }

  /**
   * Prefixes the registry object ID
   * @param location  Object to use for location
   * @param prefix    Path prefix
   * @return  Location with the given prefix
   */
  default ResourceLocation prefix(IdAwareObject location, String prefix) {
    return prefix(location.getId(), prefix);
  }

  /**
   * Suffixes the registry object ID
   * @param location  Object to use for location
   * @param suffix    Path suffix
   * @return  Location with the given suffix
   */
  default ResourceLocation suffix(IdAwareObject location, String suffix) {
    return suffix(location.getId(), suffix);
  }


  /* Tags and conditions */

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  default TagKey<Item> getItemTag(String modId, String name) {
    return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(modId, name));
  }

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  default TagKey<Fluid> getFluidTag(String modId, String name) {
    return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(modId, name));
  }

  /**
   * Creates a condition for a tag existing
   * @param name  Forge tag name
   * @return  Condition for tag existing
   */
  default ICondition tagCondition(String name) {
    return new TagFilledCondition<>(ItemTags.create(Mantle.commonResource(name)));
  }

  /**
   * Creates a consumer instance with the added conditions
   * @param consumer    Base consumer
   * @param conditions  Extra conditions
   * @return  Wrapped consumer
   */
  default RecipeOutput withCondition(RecipeOutput consumer, ICondition... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap();
    for (ICondition condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(consumer);
  }
}



