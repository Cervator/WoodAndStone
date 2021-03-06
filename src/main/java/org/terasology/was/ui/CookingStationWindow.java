/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.was.ui;

import org.terasology.crafting.ui.workstation.StationAvailableRecipesWidget;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.fluid.component.FluidComponent;
import org.terasology.fluid.component.FluidInventoryComponent;
import org.terasology.fluid.system.FluidRegistry;
import org.terasology.heat.component.HeatProducerComponent;
import org.terasology.heat.ui.ThermometerWidget;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.TeraMath;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryGrid;
import org.terasology.rendering.nui.widgets.UILoadBar;
import org.terasology.was.WoodAndStone;
import org.terasology.workstation.component.WorkstationInventoryComponent;
import org.terasology.workstation.component.WorkstationProcessingComponent;
import org.terasology.workstation.ui.BaseWorkstationScreen;

import java.util.List;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class CookingStationWindow extends BaseWorkstationScreen {

    private InventoryGrid fluidContainerInput;
    private InventoryGrid fluidContainerOutput;
    private FluidContainerWidget fluidContainer;
    private InventoryGrid ingredientsInventory;
    private InventoryGrid toolsInventory;
    private ThermometerWidget temperature;
    private VerticalTextureProgressWidget burn;
    private InventoryGrid fuelInput;
    private StationAvailableRecipesWidget availableRecipes;
    private InventoryGrid resultInventory;
    private UILoadBar craftingProgress;

    @Override
    public void initialise() {
        ingredientsInventory = find("ingredientsInventory", InventoryGrid.class);
        toolsInventory = find("toolsInventory", InventoryGrid.class);

        fluidContainerInput = find("fluidContainerInput", InventoryGrid.class);
        fluidContainer = find("fluidContainer", FluidContainerWidget.class);
        fluidContainerOutput = find("fluidContainerOutput", InventoryGrid.class);

        temperature = find("temperature", ThermometerWidget.class);

        burn = find("burn", VerticalTextureProgressWidget.class);
        burn.setMinY(76);
        burn.setMaxY(4);

        fuelInput = find("fuelInput", InventoryGrid.class);

        availableRecipes = find("availableRecipes", StationAvailableRecipesWidget.class);

        craftingProgress = find("craftingProgress", UILoadBar.class);

        resultInventory = find("resultInventory", InventoryGrid.class);

        InventoryGrid playerInventory = find("playerInventory", InventoryGrid.class);

        playerInventory.setTargetEntity(CoreRegistry.get(LocalPlayer.class).getCharacterEntity());
        playerInventory.setCellOffset(10);
        playerInventory.setMaxCellCount(30);
    }

    @Override
    public void initializeWorkstation(final EntityRef station) {
        WorkstationInventoryComponent workstationInventory = station.getComponent(WorkstationInventoryComponent.class);

        WorkstationInventoryComponent.SlotAssignment fluidInputAssignments = workstationInventory.slotAssignments.get("FLUID_INPUT");

        WorkstationScreenUtils.setupInventoryGrid(station, ingredientsInventory, "INPUT");
        WorkstationScreenUtils.setupInventoryGrid(station, toolsInventory, "TOOL");
        WorkstationScreenUtils.setupInventoryGrid(station, fluidContainerInput, "FLUID_CONTAINER_INPUT");
        WorkstationScreenUtils.setupInventoryGrid(station, fluidContainerOutput, "FLUID_CONTAINER_OUTPUT");
        WorkstationScreenUtils.setupInventoryGrid(station, fuelInput, "FUEL");
        WorkstationScreenUtils.setupInventoryGrid(station, resultInventory, "OUTPUT");

        fluidContainer.setMinX(4);
        fluidContainer.setMaxX(45);

        fluidContainer.setMinY(145);
        fluidContainer.setMaxY(4);

        fluidContainer.setEntity(station);

        final int waterSlot = fluidInputAssignments.slotStart;
        fluidContainer.setSlotNo(waterSlot);

        fluidContainer.bindTooltipString(
                new ReadOnlyBinding<String>() {
                    @Override
                    public String get() {
                        FluidInventoryComponent fluidInventory = station.getComponent(FluidInventoryComponent.class);
                        final FluidComponent fluid = fluidInventory.fluidSlots.get(waterSlot).getComponent(FluidComponent.class);
                        if (fluid == null) {
                            return "0ml";
                        } else {
                            FluidRegistry fluidRegistry = CoreRegistry.get(FluidRegistry.class);
                            return TeraMath.floorToInt(fluid.volume * 1000) + "ml of " + fluidRegistry.getFluidRenderer(fluid.fluidType).getFluidName();
                        }
                    }
                });

        WorkstationScreenUtils.setupTemperatureWidget(station, temperature, 20f);

        burn.bindValue(
                new Binding<Float>() {
                    @Override
                    public Float get() {
                        HeatProducerComponent heatProducer = station.getComponent(HeatProducerComponent.class);
                        List<HeatProducerComponent.FuelSourceConsume> consumedFuel = heatProducer.fuelConsumed;
                        if (consumedFuel.size() == 0) {
                            return 0f;
                        }
                        long gameTime = CoreRegistry.get(Time.class).getGameTimeInMs();

                        HeatProducerComponent.FuelSourceConsume lastConsumed = consumedFuel.get(consumedFuel.size() - 1);
                        if (gameTime > lastConsumed.startTime + lastConsumed.burnLength) {
                            return 0f;
                        }
                        return 1f - (1f * (gameTime - lastConsumed.startTime) / lastConsumed.burnLength);
                    }

                    @Override
                    public void set(Float value) {
                    }
                });

        availableRecipes.setStation(station);

        craftingProgress.bindVisible(
                new Binding<Boolean>() {
                    @Override
                    public Boolean get() {
                        WorkstationProcessingComponent processing = station.getComponent(WorkstationProcessingComponent.class);
                        if (processing == null) {
                            return false;
                        }
                        WorkstationProcessingComponent.ProcessDef cookingProcess = processing.processes.get(WoodAndStone.COOKING_PROCESS_TYPE);
                        return cookingProcess != null;
                    }

                    @Override
                    public void set(Boolean value) {
                    }
                }
        );
        craftingProgress.bindValue(
                new Binding<Float>() {
                    @Override
                    public Float get() {
                        WorkstationProcessingComponent processing = station.getComponent(WorkstationProcessingComponent.class);
                        if (processing == null) {
                            return 1f;
                        }
                        WorkstationProcessingComponent.ProcessDef cookingProcess = processing.processes.get(WoodAndStone.COOKING_PROCESS_TYPE);
                        if (cookingProcess == null) {
                            return 1f;
                        }

                        long gameTime = CoreRegistry.get(Time.class).getGameTimeInMs();

                        return 1f * (gameTime - cookingProcess.processingStartTime) / (cookingProcess.processingFinishTime - cookingProcess.processingStartTime);
                    }

                    @Override
                    public void set(Float value) {
                    }
                }
        );
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
