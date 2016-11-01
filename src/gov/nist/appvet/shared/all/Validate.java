/* This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 United States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 * 
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS".  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof including, but
 * not limited to, the correctness, accuracy, reliability or usefulness of
 * the software.
 * 
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement.
 */
package gov.nist.appvet.shared.all;



/**
 * @author steveq@nist.gov
 */
public class Validate {

	public static final int USERNAME_MIN_LENGTH = 3;
	public static final int USERNAME_MAX_LENGTH = 254;
	public static final int PASSWORD_MIN_LENGTH = 4;
	public static final int PASSWORD_MAX_LENGTH = 254;

	public static boolean isPrintable(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!isAsciiPrintable(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isAsciiPrintable(char ch) {
		return ch >= 32 && ch < 127;
	}

	public static boolean isAlpha(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		return s.matches("[a-zA-Z]+");
	}

	public static boolean isNumeric(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		return s.matches("[0-9]+");
	}

	public static boolean isAlphaNumeric(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		return s.matches("^[a-zA-Z0-9]*$");
	}

	public static boolean hasWhiteSpace(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		return s.matches(".*\\s+.*");
	}

	public static boolean isDate(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		// Match format YYYY-MM-DD
		return s.matches("\\d{4}-\\d{2}-\\d{2}");
	}

	public static boolean isTime(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		// Match format HH:MM:SSZ
		return s.matches("\\d{2}:\\d{2}:\\d{2}Z");
	}

	public static boolean isValidUserName(String userName) {
		if (userName == null || userName.isEmpty()) {
			return false;
		}
		return userName != null && !userName.isEmpty()
				//&& isAlphaNumeric(userName)
				&& userName.length() <= USERNAME_MAX_LENGTH
				&& userName.length() >= USERNAME_MIN_LENGTH;
	}

	public static boolean isValidPassword(String password) {
		if (password == null || password.isEmpty()) {
			return false;
		}
		return password != null && !password.isEmpty()
				&& !hasWhiteSpace(password) && isPrintable(password)
				&& (password.length() <= PASSWORD_MAX_LENGTH)
				&& (password.length() >= PASSWORD_MIN_LENGTH);
	}

	public static boolean isValidEmail(String email) {
		if (email == null || email.isEmpty()) {
			return false;
		}
		return email
				.matches("[A-Za-z0-9._%+-][A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{3}");
	}
	
	public static boolean isValidRole(String roleStr) {
		return isPrintable(roleStr);
	}

	public static boolean isUrl(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		return s.matches("([a-zA-Z0-9\\-_\\.!\\~\\*'\\(\\);/\\?:\\@\\&=\\+$,]|(%[a-fA-F0-9]{2}))*");
	}

	public static boolean isLegalFileName(String fileName) {
		return isPrintable(fileName) && !hasWhiteSpace(fileName);
	}

	public static DeviceOS hasValidAppFileExtension(String fileName) {
		final String fileNameLowerCase = fileName.toLowerCase();
		if (fileNameLowerCase.endsWith(".apk")) {
			return DeviceOS.ANDROID;
		} else if (fileNameLowerCase.endsWith(".ipa")) {
			return DeviceOS.IOS;
		} else {
			return null;
		}
	}

	public static ReportFileType hasValidReportFileExtension(String fileName) {
		final String fileNameLowerCase = fileName.toLowerCase();
		if (fileNameLowerCase.endsWith(".pdf")) {
			return ReportFileType.PDF;
		} else if (fileNameLowerCase.endsWith(".json")) {
			return ReportFileType.JSON;
		} else if (fileNameLowerCase.endsWith(".html")) {
			return ReportFileType.HTML;
		} else if (fileNameLowerCase.endsWith(".txt")) {
			return ReportFileType.TXT;
		} else if (fileNameLowerCase.endsWith(".docx")) {
			return ReportFileType.DOCX;
		} else if (fileNameLowerCase.endsWith(".rtf")) {
			return ReportFileType.RTF;
		} else if (fileNameLowerCase.endsWith(".xml")) {
			return ReportFileType.XML;
		} else {
			return null;
		}
	}

	public static boolean isLegalSearchString(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		return isPrintable(str) && !hasWhiteSpace(str);
	}
	
	public static boolean hasValidOs(String appOS) {
		if (appOS == null || appOS.isEmpty()) {
			return false;
		}
		String os = appOS.toUpperCase();
		if (os.equals(DeviceOS.ANDROID.name()) || 
				os.equals(DeviceOS.IOS.name())) {
			return true;
		} else {
			return false;
		}
	}

	public Validate() {
	}
}
