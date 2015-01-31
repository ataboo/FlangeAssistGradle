package com.atasoft.helpers;


//----Tax Manager holds tax and wage values by province and year---------
public class TaxManager {
	//Tax Years
	public static final int TY_2013 = 0;
	public static final int TY_2014 = 1;
    public static final int TY_2015 = 2;
	
	//Provinces
	public static final int PROV_BC = 0;
	public static final int PROV_AB = 1;
	public static final int PROV_SK = 2;
	public static final int PROV_MB = 3;
	public static final int PROV_ON = 4;
	public static final int PROV_QC = 5;
	public static final int PROV_NB = 6;
	public static final int PROV_NS = 7;
	public static final int PROV_PE = 8;
	public static final int PROV_NL = 9;
	
	public TaxManager() {
		//Log.d("tax manager","started");
		setupTaxStats();
	}
	
	//Used as a container for tables for each type of tax
	public class TaxStats{  
		public double[][] rates;
		public double[][] brackets;
		public double[][] constK;
		public double[] taxCred;
		public double[][] taxReduction;
		public double[][] healthPrem;
		public double[][] surtax;
		public double[] wageRates;
		public String[] wageNames;
		public double defaultWageIndex;  //tacked on end of wageRates inplace of custom value
		public double vacRate;
	}
	
	private TaxManager.TaxStats fedStats;
	private TaxManager.TaxStats bcStats;
	private TaxManager.TaxStats abStats;
	private TaxManager.TaxStats onStats;
	private double[][] cppEi;
	private void setupTaxStats(){
		
		//2013, 2014, 2015
		this.cppEi = new double[][]{
			{0.0495, 3500, 0.0188},
			{0.0495, 3500, 0.0188},
			{0.0495, 3500, 0.0188}
		};
		
		this.fedStats = new TaxManager.TaxStats();
		fedStats.brackets = new double[][]{
			{0,43561,87123,135054},
			{0,43953,87907,136370},
			{0,44701,89401,138586}
		};
		fedStats.rates = new double[][]{
			{0.15, 0.22, 0.26, 0.29},
			{0.15, 0.22, 0.26, 0.29},
			{0.15, 0.22, 0.26, 0.29}
		};
		fedStats.constK = new double[][]{
			{0,3049,6534,10586},
			{0,3077,6593,10681},
			{0,3129,6705,10863}};
		
		//(cpp max + ei max) * .15 [K2] + tax cred * .15 [K4]
		fedStats.taxCred = new double[]{2310.35, 2340.63, 2382.53};
		
		this.bcStats = new TaxManager.TaxStats();
		bcStats.brackets = new double[][]{
			{0, 37568, 75138, 86268, 104754.01, 150000},
			{0, 37606, 75213, 86354, 104858, 150000},
			{0, 37869, 75740, 86958, 105592, 151050}};
		bcStats.rates = new double[][]{
			{0.0506, 0.0770, 0.1050, 0.1229, 0.1470, 0.1680},
			{0.0506, 0.0770, 0.1050, 0.1229, 0.1470, 0.1680},
			{0.0506, 0.0770, 0.1050, 0.1229, 0.1470, 0.1680}};
		bcStats.constK = new double[][]{
			{0, 992, 3096, 4640, 7164, 10322},
			{0, 993, 3099, 4644, 7172, 10322},
			{0, 1000, 3120, 4677, 7222, 10394}};
		
		//(cpp max + ei max + BC1 amount) * .0506
		bcStats.taxCred = new double[]{684.28, 668.33, 675.44};
		bcStats.taxReduction = new double[][]{
			{18181, 409, 0.032},  //under 18181 gets 409 over gets 409 - difference * %3.2
			{18200, 409, 0.032},
			{18327, 412, 0.032}
		};
		
		//Simplicity is key
		this.abStats = new TaxManager.TaxStats();
		abStats.rates = new double[][]{{0.10},{0.10}, {0.10}};
		
		//(cpp max + ei max + AB1) * 0.1
		abStats.taxCred = new double[]{2084.03, 2112.62, 2162.46};
		
		this.onStats = new TaxManager.TaxStats();
		//Ontario adding a bracket is lame
		onStats.brackets = new double[][]{
			{0, 39723, 79448, 509000},
			{0, 40120, 80242, 514090},
			{0, 40922, 81847, 150000, 220000}};
		onStats.rates = new double[][]{
			{0.0505, 0.0915, 0.1116, 0.1316},
			{0.0505, 0.0915, 0.1116, 0.1316},
			{0.0505, 0.0915, 0.1116, 0.1216, 0.1316}};
		onStats.constK = new double[][]{
			{0, 1629, 3226, 13406},
			{0, 1645, 3258, 13540},
			{0, 1678, 3323, 4823, 7023}};
		onStats.taxCred = new double[]{647.48, 656.96, 670.31};
		onStats.taxReduction = new double[][]{
			{221},  //basic personal amount
			{223},
			{228}
		};
		onStats.healthPrem = new double[][]{  //doesn't support years yet
			{20000, 36000, 48000, 72000, 200000},
			{0.06, 0.06, 0.25, 0.25, 0.25},
			{300, 450, 600, 750, 900}
		};
		onStats.surtax = new double[][]{
			{4289, 5489, 0.2, 0.36},
			{4331, 5543, 0.2, 0.36},
			{4418, 5654, 0.2, 0.36}
		};
		//I threw these in here so I only have to update one spot
		//-------------------------Wages------------------
		//May 4, 2014
		bcStats.wageRates = new double[]{
			21.75, 24.91, 26.88, 28.86, 30.84, 32.81, 35.58, 39.53, 44.67, 46.64
		};
		bcStats.wageNames = new String[] {
			"Pre-App", "First Term", "Second Term", "Third Term", "Fourth Term", "Fifth Term", "Sixth Term", "Journeyman", "Foreman", "GF"
		};
		bcStats.defaultWageIndex = 7; //Journeyman
		bcStats.vacRate = 0.12d;

        //Updated November 2014
        abStats.wageRates = new double[]{
                32.24, 25.25, 32.24, 39.24, 43.15, 43.90, 47.05, 49.40, 51.40
        };
		abStats.wageNames = new String[]{
			"Helper", "1st Year", "2nd Year", "3rd Year", "Journeyman (S)", "Journeyman (N)", "Lead Hand", "Foreman", "GF"
		};
		abStats.defaultWageIndex = 5; //Journeyman (N)
		abStats.vacRate = 0.10d;
		
		onStats.wageRates = new double[]{
			26.05, 21.92, 26.05, 30.19, 34.32, 38.46, 40.46, 43.46, 45.46
		};
		onStats.wageNames = new String[]{
			"Helper", "1st Year", "2nd Year", "3rd Year", "4th Year", "Journeyman", "Ass't Foreman", "Foreman", "GF"
		};
		onStats.defaultWageIndex = 5;
		onStats.vacRate = 0.12d;
	}
	
	public String[] getWageNames(int province) {
		String[] wageNames;
		String surName;
		switch(province){
			case PROV_BC:
				wageNames = bcStats.wageNames;
				surName = "BC - ";
				break;
			case PROV_ON:
				wageNames = onStats.wageNames;
				surName = "ON - ";
				break;
			default:
				wageNames = abStats.wageNames;
				surName = "AB - ";
				break;
		}
		String[] retString = new String[wageNames.length + 1];
		for(int i=0; i < wageNames.length; i++){
			retString[i] = surName + wageNames[i];
		}
		retString[retString.length-1] = "Custom";
		return retString;
	}
	
	public double[] getWageRates(int province){
		double[] wageRates;
		double custVal;  //index for default of wage spinner tacked on under custom
		switch(province){
			case PROV_BC:
				wageRates = bcStats.wageRates;
				custVal = bcStats.defaultWageIndex;
				break;
			case PROV_ON:
				wageRates = onStats.wageRates;
				custVal = onStats.defaultWageIndex;
				break;
			default:
				wageRates = abStats.wageRates;
				custVal = abStats.defaultWageIndex;
				break;
		}	
		double[] retDoub = new double[wageRates.length + 1];
		for(int i=0; i < wageRates.length; i++){
			retDoub[i] = wageRates[i];
		}
		retDoub[retDoub.length-1] = custVal;
		return retDoub;
		
	}
	
	public double getVacationRate(int province){
		double vacRate;
		switch(province){
			case PROV_BC:
				vacRate = bcStats.vacRate;
				break;
			case PROV_ON:
				vacRate = onStats.vacRate;
				break;
			default:
				vacRate = abStats.vacRate;
				break;
		}
		return vacRate;
	}
	
	//Returns [fed, prov, cpp, ei]
	public double[] getTaxes(double gross, int year, int province) {
		double provTax = 0;
		double anGross = gross * 52;
		double fedTax = getFedTax(anGross, year);
		switch(province){
			case PROV_BC:
				provTax = getBCTax(anGross, year);
				break;
			case PROV_AB:
				provTax = getABTax(anGross, year);
				break;
			case PROV_ON:
				provTax = getONTax(anGross, year);
				break;
		}
		
		double[] cppEi = getCppEi(anGross, year);
		provTax = (provTax > 0) ? provTax:0;
		fedTax = (fedTax > 0) ? fedTax:0;
		//Log.d("taxmanager", String.format("returned: prov: %.2f, fed: %.2f", provTax/52, fedTax/52));
		//Log.d("taxmanager", String.format("returned: prov: %.2f, year: %.2f", (float) province, (float) year));
		return new double[]{fedTax/52, provTax/52, cppEi[0]/52, cppEi[1]/52};
	}
	
	private double[] getCppEi(double anGross, int year){
		//[cpp rate, exemption, ei rate]
		double cppRate = cppEi[year][0];
		double cppExempt = cppEi[year][1];
		double eiRate = cppEi[year][2];
		
		double cppRet = (anGross - cppExempt) * cppRate;
		double eiRet = anGross * eiRate;
		cppRet = (cppRet > 0) ? cppRet:0;
		return new double[]{cppRet, eiRet};
	}
	
	private double getFedTax(double anGross, int year){
		double[] bracket = fedStats.brackets[year];
		int taxIndex = (anGross<bracket[1]) ? 0:
			(anGross<bracket[2] ? 1 :
			(anGross<bracket[3] ? 2 : 3));
		double rate = fedStats.rates[year][taxIndex];
		double constK = fedStats.constK[year][taxIndex];
		return anGross * rate - constK - fedStats.taxCred[year];
	}
	
	private double getBCTax(double anGross, int year){
		double[] bracket = bcStats.brackets[year];
		int taxIndex = (anGross<bracket[1]) ? 0:
			(anGross<bracket[2] ? 1 :
			(anGross<bracket[3] ? 2 : 
			(anGross<bracket[4] ? 3 : 
			(anGross<bracket[5] ? 4 : 5))));
		double rate = bcStats.rates[year][taxIndex];  //Rate and constant will share same index
		double constK = bcStats.constK[year][taxIndex];	
		return rate * anGross - constK - bcStats.taxCred[year] - bcTaxReduction(anGross, year);
	}
	
	private double getABTax(double anGross, int year){
		double rate = abStats.rates[year][0];
		double taxCred = abStats.taxCred[year];
		return rate * anGross - taxCred;
	}
	
	private double getONTax(double anGross, int year){
		double[] bracket = onStats.brackets[year];
        int taxIndex;
        if(year == TY_2013 || year == TY_2014) {
            taxIndex = (anGross < bracket[1]) ? 0 :
                    (anGross < bracket[2] ? 1 :
                            (anGross < bracket[3] ? 2 : 3));
        } else { //5th tax bracket in 2015
            taxIndex = (anGross < bracket[1]) ? 0 :
                    (anGross < bracket[2] ? 1 :
                            (anGross < bracket[3] ? 2 :
                                    (anGross < bracket[4]) ? 3 : 4));
        }
		double rate = onStats.rates[year][taxIndex];  //Rate and constant will share same index
		double constK = onStats.constK[year][taxIndex];	
		double taxTotal = rate * anGross - constK - onStats.taxCred[year];
		taxTotal = ontarioSpecific(anGross, year, taxTotal);	//includes health premium, surcharge
		return taxTotal;
	}
	
	private double bcTaxReduction(double anGross, int year){  
		//switch case for provinces later?
		double[] redTable = bcStats.taxReduction[year];	//[bracket, credit, drop rate]
		double diff = anGross - redTable[0];
		double taxRed = (diff < 0) ? redTable[1] : redTable[1] - redTable[2] * diff;
		if(taxRed < 0) taxRed = 0;
		return taxRed;
	}
	
	private double ontarioSpecific(double anGross, int year, double taxPayable){
		//apply surtax
		double[] surBracket = {onStats.surtax[year][0], onStats.surtax[year][1]};
		double[] surRate = {onStats.surtax[year][2], onStats.surtax[year][3]};
		double surTax = (taxPayable < surBracket[0]) ? 0 : 
			(taxPayable < surBracket[1] ? surRate[0] * (taxPayable - surBracket[0]) : 
			surRate[0] * (taxPayable - surBracket[0]) + (taxPayable - surBracket[1]) * surRate[1]);
		taxPayable += surTax;
		//calc health premium
		double[] healthBracket = onStats.healthPrem[0];
		double[] healthRate = onStats.healthPrem[1];
		double[] healthConst = onStats.healthPrem[2];
		double rateAmount = 0d;
		if(anGross > healthBracket[0]) {
			int healthIndex = anGross < healthBracket[1] ? 0:
				(anGross < healthBracket[2] ? 1:
				(anGross < healthBracket[3] ? 2: 
				(anGross < healthBracket[4] ? 3: 4)));
			rateAmount = (anGross - healthBracket[healthIndex]) * healthRate[healthIndex];
			rateAmount = healthIndex > 0 ? rateAmount + healthConst[healthIndex - 1] : rateAmount;
			rateAmount = healthConst[healthIndex] < rateAmount ? healthConst[healthIndex] : rateAmount;
		}
		//tax reduction
		double persAmount = onStats.taxReduction[year][0] * 2 - taxPayable;
		taxPayable -= persAmount < 0 ? 0 : persAmount;
		
		//tax reduction relied on tax payable before health premium
		taxPayable += rateAmount;
		return taxPayable;
	}
}
