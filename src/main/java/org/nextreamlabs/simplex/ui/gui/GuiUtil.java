/**************************************************************************************************
 * Copyright (C) 2012  nextreamlabs                                                               *
 * This program is free software: you can redistribute it and/or modify                           *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 * This program is distributed in the hope that it will be useful,                                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 * You should have received a copy of the GNU General Public License                              *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.                          *
 **************************************************************************************************/

package org.nextreamlabs.simplex.ui.gui;

import java.awt.*;

/**
 * Utilities for GUIs
 */
class GuiUtil {

  // { Default values
  private static final Insets defaultInsets;
  // }

  static {
    defaultInsets = new Insets(0, 0, 0, 0);
  }

  private GuiUtil() {}

  /**
   * Add the component in the container, using a GridBagConstraints
   *
   * @param container Element that will include the component
   * @param component Element that will be included inside the container
   * @param gridX Vertical position of the component in the container
   * @param gridY Horizontal position of the component in the container
   * @param gridWidth Number of horizontal cells occupied by the component
   * @param gridHeight Number of vertical cells occupied by the component
   * @param weightX Weight used when the component get resized in the x-axis
   * @param weightY Weight used when the component get resized in the y-axis
   * @param anchor A constraint that specify how the component stay attached into the container
   * @param fill How the component expands inside the container?
   */
  static void addComponent(Container container, Component component, int gridX,
                                   int gridY, int gridWidth, int gridHeight, double weightX,
                                   double weightY, int anchor, int fill) {
    GridBagConstraints gbc = new GridBagConstraints(gridX, gridY, gridWidth, gridHeight, weightX,
        weightY, anchor, fill, defaultInsets, 0, 0);
    container.add(component, gbc);
  }

  static Insets getDefaultInsets() {
    return defaultInsets;
  }
}
