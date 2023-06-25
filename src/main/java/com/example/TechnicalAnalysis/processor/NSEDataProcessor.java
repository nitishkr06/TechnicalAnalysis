package com.example.TechnicalAnalysis.processor;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.TechnicalAnalysis.entity.Data;
import com.example.TechnicalAnalysis.entity.FilteredResponse;
import com.example.TechnicalAnalysis.entity.NSE;

@Component
public class NSEDataProcessor {

	long niftyCount=0;	
	List<Data> niftyPastRecords= new ArrayList<Data>();
	
	long bankNiftyCount=0;	
	double crore = 10000000;
	List<Data> bankNiftyPastRecords= new ArrayList<Data>();
	public final String BANK_NIFTY_IDENTIFIER = "BANK_NIFTY";
	public final String NIFTY_IDENTIFIER = "NIFTY";
	
	public FilteredResponse processNiftyData(NSE nse, double limit, double round) {
		
		HashMap<Double, Double> putSortedLevels = new HashMap<>();
		HashMap<Double, Double> callSortedLevels = new HashMap<>();
		
		HashMap<Double, Double> putLevels = new HashMap<>();
		HashMap<Double, Double> callLevels = new HashMap<>();
			
			List<Data> records = new ArrayList<Data>();
			
			double pastUnderlyingPrice =0;
	
			double currentStrike =Math.round((nse.getRecords().getUnderlyingValue()/round))*round;
			
			records = Arrays.asList(nse.getFiltered().getData()).stream()
					.filter(data -> (data.getStrikePrice()>=(currentStrike-limit) && data.getStrikePrice()<=(currentStrike+limit)))
					.collect(Collectors.toList());
			
			
			/*
			 * for (Data data : records) { putLevels.put(data.getStrikePrice(),
			 * (data.getPe().getChangeinOpenInterest()*
			 * data.getPe().getTotalTradedVolume())/crore);
			 * callLevels.put(data.getStrikePrice(),
			 * (data.getCe().getChangeinOpenInterest()*
			 * data.getCe().getTotalTradedVolume())/crore); }
			 * 
			 * putLevels.entrySet().stream()
			 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) .limit(5)
			 * .forEachOrdered(x -> putSortedLevels.put(x.getKey(), x.getValue()));
			 * 
			 * callLevels.entrySet().stream()
			 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) .limit(5)
			 * .forEachOrdered(x -> callSortedLevels.put(x.getKey(), x.getValue()));
			 */
			if(niftyCount==0) {
				niftyPastRecords = records;
			}
			pcrOperation(records, NIFTY_IDENTIFIER);
			
			FilteredResponse response = new FilteredResponse();
			response.setUnderlyingPrice(nse.getRecords().getUnderlyingValue());
			response.setCurrentStrike(currentStrike);
			response.setPastRecords(niftyPastRecords);
			response.setPastUnderlyingPrice(pastUnderlyingPrice);
			response.setRecords(records);
			response.setCallLevels(callSortedLevels);
			response.setPutLevels(putSortedLevels);	
			niftyPastRecords = records;
			pastUnderlyingPrice=nse.getRecords().getUnderlyingValue();
			response.setPutTotalOI(nse.getFiltered().getPe().getTotOI());
			response.setCallTotalOI(nse.getFiltered().getCe().getTotOI());
			response.setPutTotalVolume(nse.getFiltered().getPe().getTotVol());
			response.setCallTotalVolume(nse.getFiltered().getCe().getTotVol());
			response.setCallTotalChangeInOI(1000);
			response.setPutTotalChangeInOI(2000);
			response.setPcr(pcrNiftyList);
			niftyCount++;
			
			return response;
		}
	
	public FilteredResponse processBankNiftyData(NSE nse, double limit, double round) {
		
		HashMap<Double, Double> putSortedLevels = new HashMap<>();
		HashMap<Double, Double> callSortedLevels = new HashMap<>();
		
		HashMap<Double, Double> putLevels = new HashMap<>();
		HashMap<Double, Double> callLevels = new HashMap<>();
		
		List<Data> records = new ArrayList<Data>();
		
		double pastUnderlyingPrice =0;

		double currentStrike =Math.round((nse.getRecords().getUnderlyingValue()/round))*round;
		
		records = Arrays.asList(nse.getFiltered().getData()).stream()
				.filter(data -> (data.getStrikePrice()>=(currentStrike-limit) && data.getStrikePrice()<=(currentStrike+limit)))
				.collect(Collectors.toList());
		
		/*
		 * for (Data data : records) { putLevels.put(data.getStrikePrice(),
		 * (data.getPe().getChangeinOpenInterest()*
		 * data.getPe().getTotalTradedVolume())/crore);
		 * callLevels.put(data.getStrikePrice(),
		 * (data.getCe().getChangeinOpenInterest()*
		 * data.getCe().getTotalTradedVolume())/crore); }
		 * 
		 * putLevels.entrySet().stream()
		 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) .limit(5)
		 * .forEachOrdered(x -> putSortedLevels.put(x.getKey(), x.getValue()));
		 * 
		 * callLevels.entrySet().stream()
		 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) .limit(5)
		 * .forEachOrdered(x -> callSortedLevels.put(x.getKey(), x.getValue()));
		 */
		
		
		if(bankNiftyCount==0) {
			bankNiftyPastRecords = records;
		}
		pcrOperation(records, BANK_NIFTY_IDENTIFIER);
		
		FilteredResponse response = new FilteredResponse();
		response.setUnderlyingPrice(nse.getRecords().getUnderlyingValue());
		response.setCurrentStrike(currentStrike);
		response.setPastRecords(bankNiftyPastRecords);
		response.setPastUnderlyingPrice(pastUnderlyingPrice);
		response.setRecords(records);
		response.setCallLevels(callSortedLevels);
		response.setPutLevels(putSortedLevels);	
		response.setPutTotalOI(nse.getFiltered().getPe().getTotOI());
		response.setCallTotalOI(nse.getFiltered().getCe().getTotOI());
		response.setPutTotalVolume(nse.getFiltered().getPe().getTotVol());
		response.setCallTotalVolume(nse.getFiltered().getCe().getTotVol());
		response.setCallTotalChangeInOI(3000);
		response.setPutTotalChangeInOI(4000);
		response.setPcr(pcrBankNiftyList);
		bankNiftyPastRecords = records;
		pastUnderlyingPrice=nse.getRecords().getUnderlyingValue();
		bankNiftyCount++;
		
		return response;
	}
	
	public final DecimalFormat decfor = new DecimalFormat("0.00");
	public List<String> pcrNiftyList = new ArrayList();
	public List<String> pcrBankNiftyList = new ArrayList();
	
	public void pcrOperation(List<Data> records, String recordIdentifier)
	{
		double sumChangeInOICE = records.stream()
				.filter(record -> record.getCe().getChangeinOpenInterest() > 0)
				.mapToDouble(record -> record.getCe().getChangeinOpenInterest())
				.sum();
		
		double sumChangeInOIPE = records.stream()
				.filter(record -> record.getPe().getChangeinOpenInterest() > 0)
				.mapToDouble(record -> record.getPe().getChangeinOpenInterest())
				.sum();
		
		String pcr = decfor.format(sumChangeInOIPE / sumChangeInOICE);
		LocalTime time = LocalTime.now();
		String time_HH_mm_ss = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		
		String time_pcr = time_HH_mm_ss +">" + pcr;
		
		if (NIFTY_IDENTIFIER.equals(recordIdentifier))
		{
			String last = pcrNiftyList.isEmpty() ? null : pcrNiftyList.get(pcrNiftyList.size() - 1);
			if (!(last != null && last.split(">").length == 2 && last.split(">")[1].equals(pcr)))
			{
				pcrNiftyList.add(time_pcr);
			}
			System.out.println("NIFTY PCR:      " + time_pcr);
		}
		else if (BANK_NIFTY_IDENTIFIER.equals(recordIdentifier)) 
		{
			String last = pcrBankNiftyList.isEmpty() ? null : pcrBankNiftyList.get(pcrBankNiftyList.size() - 1);
			if (!(last != null && last.split(">").length == 2 && last.split(">")[1].equals(pcr)))
			{
				pcrBankNiftyList.add(time_pcr);
			}
			System.out.println("BANK NIFTY PCR: " + time_pcr);
		}
	}
}
