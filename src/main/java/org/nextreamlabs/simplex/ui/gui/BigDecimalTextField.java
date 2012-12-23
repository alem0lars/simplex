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

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Represents a JTextField that accepts only BigDecimal compliant values
 */
class BigDecimalTextField extends JTextField {

  BigDecimalTextField(BigDecimal defVal) {
    super(defVal.toString());
  }

  @Override
  protected Document createDefaultModel() {
    return new BigDecimalTextDocument();
  }

  /**
   * @return The text of the BigDecimalTextField is valid (it can be converted to BigDecimal)
   */
  @Override
  public boolean isValid() {
    try {
      new BigDecimal(getText());
      return true;
    } catch (Exception exc) {
      return false;
    }
  }

  /**
   * @return The current BigDecimal value. If it can't be converted into BigDecimal, it returns null
   */
  public BigDecimal getValue() {
    if (isValid()) {
      return new BigDecimal(getText());
    } else {
      return null;
    }
  }

  @Override
  public Dimension getMinimumSize() {
    // This override is to FIX the bad behaviour of the JTextField in some layout managers
    // (e.g. GridBagLayout):
    // http://bugs.sun.com/view_bug.do?bug_id=4238932
    return this.getPreferredSize();
  }

  /**
   * Class that represents a document for a BigDecimalTextField. It's used to check if the
   * content that will be inserted into the document can be converted into a BigDecimal
   */
  private class BigDecimalTextDocument extends PlainDocument {
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str == null) {
        return;
      }
      // Get the string prior to the modification
      String oldString = getText(0, getLength());
      // The new string is the old string plus the portion of the new text ('str')
      String newString = oldString.substring(0, offs) + str + oldString.substring(offs);
      try {
        // Try to convert the String into a BigDecimal to check if the inserted text is well
        // formatted for a BigDecimal
        new BigDecimal(newString + "0");
        // Insert the new string into the JTextField, using the parent method
        super.insertString(offs, str, a);
      } catch (NumberFormatException ignored) {
      }
    }
  }

}
