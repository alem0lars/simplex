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

import org.nextreamlabs.simplex.middleware.Proxy;
import org.nextreamlabs.simplex.middleware.QueuedProxy;
import org.nextreamlabs.simplex.ui.Ui;
import org.nextreamlabs.simplex.ui.gui.MainFrame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * System configurator without any persistence layer
 */
public class VolatileSystemConfigurator implements SystemConfigurator {

  private static final Logger logger;

  private Proxy proxy;
  private final List<Ui> uiList;

  static {
    logger = Logger.getLogger("org.nextreamlabs.simplex.systemConfigurator");
  }

  // { Constructors and Factories

  private VolatileSystemConfigurator() throws ConfigurationException {
    this.uiList = new ArrayList<Ui>();
    if (!this.configure()) {
      throw new ConfigurationException("System configuration failed");
    }
  }

  public static SystemConfigurator create() throws ConfigurationException {
    return new VolatileSystemConfigurator();
  }

  // }

  @Override
  public Boolean configure() {
    MainFrame gui = MainFrame.create(this.getSystemName()); // Create the GUI
    this.uiList.add(gui);
    this.proxy = QueuedProxy.create(this.uiList); // Create the proxy
    gui.register(proxy); // The proxy is a GUI listener

    gui.initialize();

    return Boolean.TRUE;
  }

  @Override
  public Boolean startSystem() {
    Boolean status = Boolean.TRUE;
    try {
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          for(Ui ui : uiList) { // Start all UI
            if (!ui.start()) {
              logger.severe("Cannot start the UI: " + ui);
            }
          }
        }
      });
    } catch (Exception exc) {
      status = Boolean.FALSE;
      StringBuilder msg = new StringBuilder();
      msg.append("Unexpected error on Simplex:\n");
      msg.append("\t");
      msg.append(exc.getMessage());
      msg.append("\n");
      logger.severe(msg.toString());
    }
    return status;
  }

  @Override
  public String getSystemName() {
    return "Simplex";
  }
}
