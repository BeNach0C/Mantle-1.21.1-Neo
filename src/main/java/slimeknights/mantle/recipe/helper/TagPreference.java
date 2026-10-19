package slimeknights.mantle.recipe.helper;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.config.Config;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.mantle.util.RegistryHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Utility that helps get the preferred item from a tag based on mod ID.
 */
public class TagPreference {
  /** Just an alphabetically late RL to simplify null checks */
  private static final ResourceLocation DEFAULT_ID = ResourceLocation.parse("zzzzz:zzzzz"); // simplfies null checks

  /** Cache from any tag key to its value */
  private static final Map<TagKey<?>, Optional<?>> PREFERENCE_CACHE = new ConcurrentHashMap<>();
  /** Cache of comparator instances, concurrent as hash map optimizations means the whole compute if absent method is not entirely synchronized */
  private static final Map<ResourceKey<?>, Comparator<?>> COMPARATOR_CACHE = new ConcurrentHashMap<>();
  static {
    COMPARATOR_CACHE.put(Registries.FLUID, FluidComparator.INSTANCE);
  }

  /** Registers the listener with the event bus */
  public static void init() {
    net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.NORMAL, false, net.neoforged.neoforge.event.TagsUpdatedEvent.class, e -> PREFERENCE_CACHE.clear());
  }

  /** Gets the comparator for the given registry */
  @SuppressWarnings("unchecked")
  private static <T> Comparator<T> getComparator(Registry<T> registry) {
    return (Comparator<T>)COMPARATOR_CACHE.computeIfAbsent(registry.key(), k -> new RegistryComparator<>(registry));
  }

  /** Gets the preference from a tag without going through the cache, internal logic behind {@link #getPreference(TagKey)} */
  private static <T> Optional<T> getUncachedPreference(TagKey<T> tag) {
    Registry<T> registry = RegistryHelper.getRegistry(tag.registry());
    if (registry == null) {
      return Optional.empty();
    }
    // streams have a lovely function to get the minimum element based on a comparator
    // if the tag is empty, stream is empty so returns empty
    Optional<T> result = RegistryHelper.getTagValueStream(registry, tag).min(getComparator(registry));
    if (result.isEmpty()) {
      if ("forge".equals(tag.location().getNamespace())) {
        TagKey<T> cTag = TagKey.create(tag.registry(), net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", tag.location().getPath()));
        result = RegistryHelper.getTagValueStream(registry, cTag).min(getComparator(registry));
      } else if ("c".equals(tag.location().getNamespace())) {
        TagKey<T> forgeTag = TagKey.create(tag.registry(), net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("forge", tag.location().getPath()));
        result = RegistryHelper.getTagValueStream(registry, forgeTag).min(getComparator(registry));
      }
    }
    return result;
  }

  /**
   * Gets the preferred value from a tag based on mod ID
   * @param tag    Tag to fetch
   * @return  Preferred value from the tag, or empty optional if the tag is empty
   */
  @SuppressWarnings("unchecked")
  public static <T> Optional<T> getPreference(TagKey<T> tag) {
    Optional<?> cached = PREFERENCE_CACHE.get(tag);
    if (cached != null && cached.isPresent()) {
      return (Optional<T>) cached;
    }
    Optional<T> preference = getUncachedPreference(tag);
    if (preference.isPresent()) {
      PREFERENCE_CACHE.put(tag, preference);
    }
    return preference;
  }

  /** Logic to compare two registry values */
  private record RegistryComparator<T>(Registry<T> registry) implements Comparator<T> {
    @Override
    public int compare(T a, T b) {
      // first get registry names, use default ID if null (unlikely)
      ResourceLocation idA = Objects.requireNonNullElse(registry.getKey(a), DEFAULT_ID);
      ResourceLocation idB = Objects.requireNonNullElse(registry.getKey(b), DEFAULT_ID);
      // first compare preferences
      List<? extends String> entries;
      try {
        if (Config.SERVER_SPEC != null && Config.SERVER_SPEC.isLoaded()) {
          entries = Config.TAG_PREFERENCES.get();
        } else {
          entries = List.of("minecraft", "tconstruct", "tmechworks", "metalborn", "embers", "create", "immersiveengineering", "mekanism", "thermal");
        }
      } catch (Exception e) {
        entries = List.of("minecraft", "tconstruct", "tmechworks", "metalborn", "embers", "create", "immersiveengineering", "mekanism", "thermal");
      }
      int size = entries.size();
      int indexA = LogicHelper.defaultIf(entries.indexOf(idA.getNamespace()), -1, size);
      int indexB = LogicHelper.defaultIf(entries.indexOf(idB.getNamespace()), -1, size);
      if (indexA != indexB) {
        return Integer.compare(indexA, indexB);
      }
      // for stability, fallback to registry name compare
      return idA.compareNamespaced(idB);
    }
  }

  /** Special casing comparator to ensure flowing fluids are sorted last after fluid sources. */
  private enum FluidComparator implements Comparator<Fluid> {
    INSTANCE;

    /** Base logic after comparing source */
    private final Comparator<Fluid> preference = new RegistryComparator<>(BuiltInRegistries.FLUID);

    @Override
    public int compare(Fluid fluid1, Fluid fluid2) {
      boolean isSource1 = fluid1.isSource(fluid1.defaultFluidState());
      if (isSource1 != fluid2.isSource(fluid2.defaultFluidState())) {
        return isSource1 ? -1 : 1;
      }
      return preference.compare(fluid1, fluid2);
    }
  }
}
