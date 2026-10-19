package slimeknights.mantle.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.common.FluidStackLoadable;
import slimeknights.mantle.loot.MantleLoot;

/**
 * Loot function to set the fluid on a dropped item
 */
public class SetFluidLootFunction extends LootItemConditionalFunction {
  private static final com.mojang.serialization.Codec<FluidStack> FLUID_STACK_CODEC = com.mojang.serialization.Codec.withAlternative(
    FluidStack.CODEC,
    RecordCodecBuilder.create(inst -> inst.group(
      net.minecraft.core.registries.BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
      com.mojang.serialization.Codec.INT.fieldOf("amount").forGetter(FluidStack::getAmount)
    ).apply(inst, FluidStack::new))
  );
  public static final MapCodec<SetFluidLootFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
      commonFields(inst).and(
          FLUID_STACK_CODEC.fieldOf("fluid").forGetter(loot -> loot.fluid)
      ).apply(inst, SetFluidLootFunction::new)
  );

  /** Fluid to add to the item */
  private final FluidStack fluid;
  protected SetFluidLootFunction(List<LootItemCondition> conditionsIn, FluidStack fluid) {
    super(conditionsIn);
    this.fluid = fluid;
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    return java.util.Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM))
                .map(handler -> {
                  handler.fill(fluid.copy(), FluidAction.EXECUTE);
                  return handler.getContainer();
                }).orElse(stack);
  }

  @Override
  public LootItemFunctionType<SetFluidLootFunction> getType() {
    return MantleLoot.SET_FLUID_FUNCTION;
  }

  /**
   * Creates a new builder with the given fluid
   * @param fluid  Fluid to set
   * @return  Builder instance
   */
  public static Builder<?> builder(FluidStack fluid) {
    return simpleBuilder(conditions -> new SetFluidLootFunction(conditions, fluid));
  }
}
