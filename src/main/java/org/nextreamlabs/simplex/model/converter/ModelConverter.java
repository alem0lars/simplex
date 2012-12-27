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

package org.nextreamlabs.simplex.model.converter;

import org.nextreamlabs.simplex.command.Command;
import org.nextreamlabs.simplex.model.LinearModel;
import org.nextreamlabs.simplex.ui.Ui;

import java.util.List;

/**
 * Convert a model type into another
 */
public interface ModelConverter {
  public List<Command<Ui>> convert() throws CannotConvertModelException;

  public LinearModel getModel();
}
