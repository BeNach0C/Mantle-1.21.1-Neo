package slimeknights.mantle.data.loadable.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.Map;

/** Loadable for ingredients, handling Forge ingredients */
public enum IngredientLoadable implements Loadable<Ingredient> {
  ALLOW_EMPTY,
  DISALLOW_EMPTY;

  public static JsonElement normalizeIngredientJson(JsonElement element) {
    if (element == null || element.isJsonNull()) {
      return element;
    }
    if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      if (array.isEmpty()) {
        return element;
      }
      JsonArray newArray = new JsonArray();
      for (JsonElement child : array) {
        newArray.add(normalizeIngredientJson(child));
      }
      JsonObject compound = new JsonObject();
      compound.addProperty("type", "neoforge:compound");
      compound.add("children", newArray);
      return compound;
    }
    if (element.isJsonObject()) {
      JsonObject obj = element.getAsJsonObject();
      JsonObject newObj = new JsonObject();
      for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
        String k = entry.getKey();
        JsonElement v = entry.getValue();
        if ("tag".equals(k) && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
          String tagStr = v.getAsString();
          if (tagStr.startsWith("forge:")) {
            newObj.addProperty(k, "c:" + tagStr.substring("forge:".length()));
            continue;
          } else if (tagStr.startsWith("#forge:")) {
            newObj.addProperty(k, "#c:" + tagStr.substring("#forge:".length()));
            continue;
          }
        }
        if ("item".equals(k) && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
          String itemStr = v.getAsString();
          if ("minecraft:scute".equals(itemStr)) {
            newObj.addProperty(k, "minecraft:turtle_scute");
            continue;
          }
        }
        if ("type".equals(k) && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
          String typeStr = v.getAsString();
          if (typeStr.startsWith("forge:")) {
            newObj.addProperty(k, "neoforge:" + typeStr.substring("forge:".length()));
            continue;
          }
        }
        if (("children".equals(k) || "ingredients".equals(k)) && v.isJsonArray()) {
          JsonArray children = v.getAsJsonArray();
          JsonArray newChildren = new JsonArray();
          for (JsonElement child : children) {
            newChildren.add(normalizeIngredientJson(child));
          }
          newObj.add(k, newChildren);
          continue;
        }
        if (("base".equals(k) || "subtracted".equals(k)) && (v.isJsonObject() || v.isJsonArray())) {
          newObj.add(k, normalizeIngredientJson(v));
          continue;
        }
        newObj.add(k, normalizeNonIngredient(v));
      }
      return newObj;
    }
    return element;
  }

  public static JsonElement normalizeNonIngredient(JsonElement element) {
    if (element == null || element.isJsonNull()) {
      return element;
    }
    if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      JsonArray newArray = new JsonArray();
      for (JsonElement child : array) {
        newArray.add(normalizeNonIngredient(child));
      }
      return newArray;
    }
    if (element.isJsonObject()) {
      JsonObject obj = element.getAsJsonObject();
      JsonObject newObj = new JsonObject();
      for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
        String k = entry.getKey();
        JsonElement v = entry.getValue();
        if ("tag".equals(k) && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
          String tagStr = v.getAsString();
          if (tagStr.startsWith("forge:")) {
            newObj.addProperty(k, "c:" + tagStr.substring("forge:".length()));
            continue;
          } else if (tagStr.startsWith("#forge:")) {
            newObj.addProperty(k, "#c:" + tagStr.substring("#forge:".length()));
            continue;
          }
        }
        if ("item".equals(k) && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
          String itemStr = v.getAsString();
          if ("minecraft:scute".equals(itemStr)) {
            newObj.addProperty(k, "minecraft:turtle_scute");
            continue;
          }
        }
        if ("type".equals(k) && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
          String typeStr = v.getAsString();
          if (typeStr.startsWith("forge:")) {
            newObj.addProperty(k, "neoforge:" + typeStr.substring("forge:".length()));
            continue;
          }
        }
        newObj.add(k, normalizeNonIngredient(v));
      }
      return newObj;
    }
    return element;
  }

  @Override
  public Ingredient convert(JsonElement element, String key, TypedMap context) {
    JsonElement normalized = normalizeIngredientJson(element);
    return (this == ALLOW_EMPTY ? Ingredient.CODEC : Ingredient.CODEC_NONEMPTY).parse(com.mojang.serialization.JsonOps.INSTANCE, normalized).getOrThrow(com.google.gson.JsonSyntaxException::new);
  }

  @Override
  public JsonElement serialize(Ingredient object) {
    if (object.isEmpty() && this == DISALLOW_EMPTY) {
      throw new IllegalArgumentException("Ingredient cannot be empty");
    }
    return (this == ALLOW_EMPTY ? Ingredient.CODEC : Ingredient.CODEC_NONEMPTY).encodeStart(com.mojang.serialization.JsonOps.INSTANCE, object).getOrThrow(IllegalStateException::new);
  }

  @Override
  public Ingredient decode(FriendlyByteBuf buffer, TypedMap context) {
    return Ingredient.CONTENTS_STREAM_CODEC.decode((net.minecraft.network.RegistryFriendlyByteBuf) buffer);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, Ingredient object) {
    Ingredient.CONTENTS_STREAM_CODEC.encode((net.minecraft.network.RegistryFriendlyByteBuf) buffer, object);
  }
}
