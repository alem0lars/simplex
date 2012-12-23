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

package org.nextreamlabs.simplex;

import java.util.logging.Logger;

/**
 * Entry point
 */
public class Main {

  private static final Logger logger;

  static {
    logger = Logger.getLogger("org.nextreamlabs.simplex.Main");
  }

  private Main() {}

  public static void main(String[] args) {
    StringBuilder sb = new StringBuilder();

    try { // Create the system configurator

      SystemConfigurator configurator = VolatileSystemConfigurator.create();
      sb.append("The system has been created");
      logger.info(sb.toString()); sb = new StringBuilder();

      sb.append("The system ").append(configurator.getSystemName());
      if (configurator.startSystem()) {
        sb.append(" has been started");
      } else {
        sb.append(" could not be started");
      }
      logger.info(sb.toString()); sb = new StringBuilder();

    } catch (ConfigurationException e) {
      sb.append("The system could not be created. Reason: ").append(e.getMessage());
      logger.warning(sb.toString());
    }

  }

}
