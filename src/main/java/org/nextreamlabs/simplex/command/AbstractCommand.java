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

package org.nextreamlabs.simplex.command;

import java.util.Map;
import java.util.logging.Logger;

/**
 * A generic command
 */
public abstract class AbstractCommand<T> implements Command<T> {
  private static final Logger logger;

  private boolean completed;

  static {
    logger = Logger.getLogger("org.nextreamlabs.simplex.AbstractCommand");
  }

  protected AbstractCommand() {
    this.completed = Boolean.FALSE;
  }

  @SuppressWarnings("FinalMethod") // A subclass cannot override this method
  @Override
  public final Boolean execute(T target) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getName()).append(":");
    Map<String, String> loggingInfo = getLoggingInfo();
    for (String key : loggingInfo.keySet()) {
      sb.append("[").append(key).append("=").append(loggingInfo.get(key)).append("]");
    }
    getLogger().info(sb.toString());

    boolean result = this.run(target);
    if (result) {
      this.completed = Boolean.TRUE;
    }
    return result;
  }

  protected Logger getLogger() { return logger; }

  /**
   * Command implementation
   * @param target The target to be used
   * @return Success or failure
   */
  protected abstract Boolean run(T target);

  protected abstract Map<String, String> getLoggingInfo();

  @Override
  public abstract String getName();

  @Override
  public Boolean isCompleted() {
    return this.completed;
  }
}
