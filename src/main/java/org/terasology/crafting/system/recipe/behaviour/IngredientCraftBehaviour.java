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
package org.terasology.crafting.system.recipe.behaviour;

import org.terasology.crafting.system.recipe.render.CraftIngredientRenderer;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.List;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public interface IngredientCraftBehaviour<T> {
    boolean isValidAnyAmount(T ingredient);

    List<String> getValidToCraft(EntityRef entity, int multiplier);

    boolean isValidToCraft(EntityRef entity, String parameter, int multiplier);

    int getMaxMultiplier(EntityRef entity, String parameter);

    CraftIngredientRenderer getRenderer(EntityRef entity, String parameter);

    void processIngredient(EntityRef instigator, EntityRef entity, String parameter, int multiplier);
}
