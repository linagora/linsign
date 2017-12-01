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

import org.linagora.linsign.application.model.dao.StatusCert;

public enum Text {

	/* Application */
	DOCUMENT_TO_SIGN("Documents to sign", "Documents à signer"),
	TITLE("Title", "Titre"),
	TRANSMITTER("Transmitter", "Emetteur"),
	CREATED_AT("Create at", "Création"),
	
	/* Certificate */
	TITLE_APPLET("Electronic signature","Signature électronique"),
	SIGNING_CERTIFICATE("Signing certificate", "Certificat de signature"),
	SELECTED("Selected", "Selection"),
	NAME_CN("Name (CN)", "Nom Commun (CN)"),
	CERTIFICATE_ID("Certificate Id (DN)", "Identifiant du certificat (DN)"),
	DATE_START_VALIDITY("Date start", "Date de début de validité"),
	DATE_END_VALIDITY("Date end", "Date de fin de validité"),
	SERIAL_NUMBER("Serial Number", "Numéro de série"),
	PREVIEW("Download", "Télécharger"),
	ACTION("Action", "Action"),
	SELECT_CERTIFICAT_VALIDITY_TITLE("Certificat", "Certificat"),
	SELECT_CERTIFICAT("Select this certificat", "Selection du certificat"),
	ID_CERTIFICAT("Id certificat", "Nom Commun (CN)"),
	VALIDITY_CERTIFICAT("validity", "Période de validité"),
	SEPERATOR_DATE("to", "au"),
	
	/* Error Validation Certificat */
	TRUSTED_CERT("TRUSTED_CERT", "Certificat Valide"),
	ISSUED_BY("from : CN=","émis par : CN="),
	ERROR_EXPIRED_CERT("Expired certificat", "Certificat expiré"),
	ERROR_NOT_TRUSTED_CERT("Certificat isn't trusted", "Certificat hors du périmetre de confiance"),
	ERROR_REVOKED_CERT("Revoked certificat", "Certificat révoqué"),
	ERROR_UNKNOWN_REVOCATION_CERT("Certificat outside the trusted perimeter", "Vérification du certificat impossible (CRL en défaut)"),
	ERROR_NOT_YET_VERIFIED_CERT("Error to validate the certificat", "Certificat non valide"),
	ERROR_VALIDATE_CERTIFICAT("Error to validate the certificat", "Certificat non valide"),
	ERROR_VALIDATE_DEFAULT_CERTIFICAT("Error to validate the only certificat", "Le seul certificat n'est pas valide"),
	
	/* Action waiting from user */
	SELECT_VALID_CERTIFICAT("Please select a valid certificat", "Merci de sélectionner un certificat valide"),
	SELECT_ONLY_CERTIFICAT("Select the only certificat find", "Selection du seul certificat identifié"),
	NO_CERTIFICAT_FOUND("No user certificat found", "Pas de certificat utilisateur trouvé"),
	
	/* Signing action */
	SIGNING("Signing", "Signature"),
	CERTIFY_READ_DOCUMENT("I certify having read all documents to sign"
			, "Je certifie avoir pris connaissance de l'ensemble des documents à signer"),
	SIGN_BUTTON("Sign", "Signer"),
	QUIT_BUTTON("Quit", "Quitter"),
	
	/* End process */
	POP_UP_TITLE_SIGN("Sign info", "Signature information"),
	SUCCES_MESSAGE("Sign document has been a succes. Exit", "La signature a été un succès. Quitter"),
	FAIL_MESSAGE("Error during the document sign. Exit", "Erreur durant la signature. Quitter"),
	ERROR_WITH_SOAP_SERVICE("Error during the service call", "Erreur du service : closeTransaction"),
	
	/* Error message */
	PREVIEW_NOT_SUPPORTED("Preview is suported with java 8 or more", "L'apercu n'est supporté qu'avec la version de java 8 ou plus"),
	ERROR_PREVIEW("Error with the preview", "Impossible d'avoir l'aperçu du fichier"),
	ERROR_SPACE_PREVIEW("Not enough space for the preview", "Espace mémoire insufissante pour l'aperçu du fichier"),
	ERROR_SPACE("Not enough space for the preview", "Espace mémoire insufissante pour la prévualisation"),
	
	NO_CERTIFICAT_SELECTED("Please select a certificate","Veuillez sélectionner un certificat"),
	NO_MATCHING_NUMBER("Number of pdfproperties don't match with the document list to sign", "Le nombre de pdfproperties ne correspond pas au nombre de document"),
	NO_MATCHING_CONFIG("The document is not find in the signature properties", "Le document n'a pas été trouvé dans la configuration de signature"),
	
	/* Error provider */
	ERROR_PKCS11("Unable to load the certificat, reason : ", "Impossible de charger le certificat, la raison : "),
	ERROR_TIME_OUT("Looks like no password has been given", "Il semblerait qu'aucun mot de passe a été indiqué"), 
	ERROR_LOGIN_ERROR("Wrong password", "Erreur de mot de passe"), 
	ERROR_LOCK_CARD("The certificat is locked", "Le certificat est verrouillé"),
	ERROR_LOAD_PKCS11("Error to load the main KeyStore", "Erreur de chargement du keystore principal"),
	ERROR_LOAD_PKCS11_PROFIL_CHARACTER("Unable to load profil, special character in profile","Un caractère spécial a été trouvé dans le chemin du profil Firefox"),
	
	PASSWORD_USER("Enter the Firefox principal password", "Entrer le mot de passe principal de firefox"),
	CANCEL_MAIN_PASSWORD("Cancel, X or escape key selected", "Annuler, X ou Echap bouton selectionné"),
	
	BEFORE("Before", "Suivant"),
	NEXT("Next", "After")
	;

	private final String english;
	private final String french;

	private Text(final String english, final String french) {
		this.english = english;
		this.french = french;
	}

	public String getEnglish() {
		return english;
	}

	public String getFrench() {
		return french;
	}

	public static Text selectTextWithCertificatError(String errorStr){
		if(errorStr.equals(StatusCert.EXPIRED_CERT.getStatus()))
			return ERROR_EXPIRED_CERT;
		else if(errorStr.equals(StatusCert.NOT_TRUSTED_CERT.getStatus()))
			return ERROR_NOT_TRUSTED_CERT;
		else if(errorStr.equals(StatusCert.REVOKED_CERT.getStatus()))
			return Text.ERROR_REVOKED_CERT;
		else if(errorStr.equals(StatusCert.UNKNOWN_REVOCATION_CERT.getStatus()))
			return Text.ERROR_UNKNOWN_REVOCATION_CERT;
		else if(errorStr.equals(StatusCert.NOT_YET_VERIFIED_CERT.getStatus()))
			return ERROR_NOT_YET_VERIFIED_CERT;
		return ERROR_VALIDATE_CERTIFICAT;
	}
}
