package es.redmoon.pl.mail.tests;

import es.redmoon.pl.mail.Mail;

public class TestLoadMails {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		long accountID = 1;
		String folder = "INBOX";
		
		System.out.println("Loading mails ...");
		long nMails = Mail.loadNewMails(accountID, folder);
		System.out.printf("Loaded %d mails.\n", nMails);
	}
}
