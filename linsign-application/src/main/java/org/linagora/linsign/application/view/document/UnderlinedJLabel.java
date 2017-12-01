/**
 *  LinSign - Electronic signature application
 *  
 *  Copyright © 2008--2017 LINAGORA, www.linagora.com
 *  
 *  SPDX-License-Identifier: AGPL-3.0
 *  
 *  This file is part of LinSign.
 *  
 *  LinSign is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of
 *  the License, or (at your option) any later version.
 *  
 *  LinSign is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with LinSign.  If not, see <http://www.gnu.org/licenses/agpl.html>.
 */
package org.linagora.linsign.application.view.document;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class UnderlinedJLabel extends JLabel {
	public UnderlinedJLabel() {
	}

	public UnderlinedJLabel(String text) {
		super(text);
	}

	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		Font f = new Font(this.getFont().getFamily(), Font.PLAIN, this.getFont().getSize() - 1);
		this.setFont(f);
		super.paint(g);
		underline(g);
	}

	protected void underline(Graphics g) {
		g.setColor(Color.BLUE);
		Insets insets = getInsets();
		FontMetrics fm = g.getFontMetrics();
		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(insets.left + 4, insets.top, getWidth() - (insets.right + insets.left),
				getHeight() - (insets.bottom + insets.top));

		// compute and return location of the icons origin,
		// the location of the text baseline, and a possibly clipped
		// version of the compound label string. Locations are computed
		// relative to the viewR rectangle.
		String text = SwingUtilities.layoutCompoundLabel(this, // this JLabel
				fm, // current FontMetrics
				getText(), // text
				getIcon(), // icon
				getVerticalAlignment(), getHorizontalAlignment(), getVerticalTextPosition(),
				getHorizontalTextPosition(), viewR, new Rectangle(), // don't
																		// care
																		// about
																		// icon
																		// rectangle
				textR, // resulting text locations
				getText() == null ? 0 : ((Integer) UIManager.get("Button.textIconGap")).intValue());

		// draw line
		int textShiftOffset = ((Integer) UIManager.get("Button.textShiftOffset")).intValue();
		g.fillRect(textR.x + textShiftOffset - 4, textR.y + fm.getAscent() + textShiftOffset + 2, textR.width, 1);
	}
}
