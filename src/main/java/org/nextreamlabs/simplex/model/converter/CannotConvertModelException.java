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

import org.nextreamlabs.simplex.model.LinearModel;

/**
 * Represents a failure on a model conversion
 */
public class CannotConvertModelException extends Exception {
  private final String reason;
  private final LinearModel sourceModel;

  // { Constructors and Factories

  public CannotConvertModelException(LinearModel sourceModel, String reason) {
    super("Cannot convert the model " + sourceModel);
    this.sourceModel = sourceModel;
    this.reason = reason;
  }

  // }

  // { Getters and Setters

  public String getReason() { return this.reason; }
  public LinearModel getSourceModel() { return this.sourceModel; }

  // }

}
