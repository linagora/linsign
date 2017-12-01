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
package org.linagora.linsign.application.view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.linagora.linsign.application.model.dao.Status;
import org.linagora.linsign.application.service.ServiceIss;

public class SkinLoader {

	private final static Logger LOGGER = Logger.getLogger(SkinLoader.class.getName());

	private String idSignature;
	private ServiceIss serviceIss;

	public SkinLoader(String idSignature, ServiceIss serviceIss) {
		this.idSignature = idSignature;
		this.serviceIss = serviceIss;
	}

	public BufferedImage getURLFile(String path, String nameDefault) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new URL(path));

		} catch (IOException e) {
			try {
				image = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(nameDefault));
			} catch (IOException e1) {
				serviceIss.sendStatus(idSignature, Status.ERROR_SKIN);
				LOGGER.log(Level.SEVERE, e.getMessage() + " - No default skin found");
			}

			serviceIss.sendStatus(idSignature, Status.ERROR_SKIN);
			LOGGER.log(Level.SEVERE, e.getMessage() + " - File not found");
		}

		return image;
	}

}
