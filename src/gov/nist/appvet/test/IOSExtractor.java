package gov.nist.appvet.test;

import gov.nist.appvet.shared.backend.FileUtil;

import java.io.File;

public class IOSExtractor {

	
	
	
	public static void main(String[] args) {
		
		final String ipaFilePath = "/home/carwash/test-apps/e-FIOA.ipa";
		File ipaFile = new File(ipaFilePath);
		
		String zipFilePath = "/home/carwash/test-apps/e-FIOA.zip";
		File destFile = new File(zipFilePath);
		
		FileUtil.copyFile(ipaFile, destFile);
	}
	
}
