package es.redmoon.pl.network.tests;

import es.redmoon.pl.network.Whois;


public class TestWhois {
	public static void main(String[] args)
	{
		String ip = "173.194.34.240";
		
		System.out.println("IP country: " + Whois.getCountry(ip));
		System.out.println("IP ISP: " + Whois.getNameISP(ip));
	}
}
