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
package org.linagora.linsign.application.langue;

public class SelectorLanguage {

	private Language languageSelect;
	private final Language defaultLanguage = Language.EN;

	public SelectorLanguage(Language langueSelect) {
		super();
		if (langueSelect == null)
			this.languageSelect = defaultLanguage;
		else
			this.languageSelect = langueSelect;
	}

	public Language getLangueSelect() {
		return languageSelect;
	}

	public String printText(Text text) {
		if (this.languageSelect.equals(Language.EN))
			return text.getEnglish();
		else if (this.languageSelect.equals(Language.FR))
			return text.getFrench();

		return text.getEnglish();
	}

}
