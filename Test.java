import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import java.lang.reflect.Field;
public class Test {
    public static void main(String[] args) throws Exception {
        for(Field f : Ingredient.class.getFields()) {
            System.out.println("Ingredient." + f.getName());
        }
    }
}
