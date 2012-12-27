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

import java.util.*;
import java.util.logging.Logger;

/**
 * A command invoker based on priority queues
 * @param <T> The command receiver
 */
public class QueuedCommandInvoker<T> implements CommandInvoker<T> {
  private static final Logger logger;

  static {
    logger = Logger.getLogger("org.nextreamlabs.simplex.QueuedCommandInvoker");
  }

  // The commandsPriorities queue with priorities. It's used as a FIFO data structure.
  // The priorities are kept ordered (TreeMap implementation)
  private final Map<Integer, List<Command<T>>> commandsPriorities;

  private QueuedCommandInvoker() {
    this.commandsPriorities = new TreeMap<Integer, List<Command<T>>>();
  }

  /**
   * Create a new CommandInvoker
   * @return The new CommandInvoker
   */
  public static <T> CommandInvoker<T> create() {
    return new QueuedCommandInvoker<T>();
  }

  /**
   * Enqueue the command
   * @param command The command to be enqueued
   */
  @Override
  public void addCommand(Command<T> command) {
    Integer priority = command.getPriority();
    if (this.commandsPriorities.get(priority) == null) {
      this.commandsPriorities.put(priority, new ArrayList<Command<T>>());
    }

    this.commandsPriorities.get(priority).add(command);
  }

  /**
   * Remove all completed commands from the queue
   */
  @Override
  public void clear() {
    List<Integer> indexes = new LinkedList<Integer>();

    for (Integer priority : this.commandsPriorities.keySet()) {
      List<Command<T>> commands = this.commandsPriorities.get(priority);
      indexes.clear();
      for (Integer j = 0; j < commands.size(); j++) {
        if (commands.get(j).isCompleted()) {
          indexes.add(0, j);
        }
      }
      for (int index : indexes) {
        commands.remove(index);
      }
    }
  }

  /**
   * Execute all 'commandsPriorities' in the queue
   */
  @Override
  public Boolean executeAll(T target) {
    Boolean status = Boolean.TRUE;
    for (Map.Entry<Integer, List<Command<T>>> entry : this.commandsPriorities.entrySet()) {
      logger.info("Executing commands for: [priority=" + entry.getKey() + "]");
      for (Command<T> command : entry.getValue()) {
        if (!command.execute(target)) {
          status = Boolean.FALSE;
          logger.warning("Failed to execute the command: " + command.getName());
          break; // INF: If a command has failed, don't execute the others
        }
      }
    }
    return status;
  }
}
