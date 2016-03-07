package com.jpm.stock.simple.maintest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpm.stock.simple.dao.*;
import com.jpm.stock.simple.exception.StockException;
import com.jpm.stock.simple.model.*;
import com.jpm.stock.simple.service.Stock;
import com.jpm.stock.simple.service.Trade;
/**
 * <h1>DeployTest</h1>
 * 
 * This is the main method for the application.
 * 
 * 
 * @author Jose Calderon joseenrique86@gmail.com
 * @version 1.0
 * @since 2016-02-13
 *
 */
public class DeployTest {

	private static List<StockBean> stockList;
	private static List<TradeBean> totalTradeList;
	private static Logger log = Logger.getLogger(DeployTest.class);
	/*numTrades is the number of random trades for each Stock */
	private static final int numTrades = 15;

	public static void main(String args[]) throws StockException {
		totalTradeList = new ArrayList<TradeBean>();
		Stock stockDat = new Stock();
		Trade tradeDat = new Trade();
        /*Loading the Stocks from StockDAO class */
		log.info("***Loading Stocks...");
		stockList = StockDAO.loadStocks();
		for (StockBean stock : stockList) {
			log.info(stock.toString() + " has been loaded");
		}
		log.info("***Stocks are loaded.");
		
		log.info("***Calculating the Dividend Yield for all the Stocks in the System");
		/* Dividend Yield Calculation */
		for (StockBean stock : stockList) {
			BigDecimal dividendYield = stockDat.getDividendYield(stock);
			log.info("The Dividend Yield for: " + stock.getSymbol() + " is "
					+ dividendYield.toPlainString());
		}

		log.info("***Calculating the PE Ratio for all the Stocks in the System ");
		/* P/E Ratio Calculation */
		for (StockBean stock : stockList) {
			BigDecimal peRatio = stockDat.getPERatio(stock);
			log.info("The PE Ratio for: " + stock.getSymbol() + " is "
					+ peRatio.toPlainString());
		}

		log.info("***Creating Random Trades...");
		/* Generates random Trades for the Stocks */
		for (StockBean stock : stockList) {
			log.info("Starting Trades for: " + stock.getSymbol());
			for (int i = 0; i < numTrades; i++) {
				TradeBean trade = TradeDAO.createRandomTrade(stock);
				log.info("Trade:"+trade.toString());
				totalTradeList.add(trade);
				stock.setTickerPrice(stockDat.updateStockPrice(stock,
						totalTradeList));
				log.info("The Ticker Price for: " + stock.getSymbol()
						+ " has been updated to: "
						+ stock.getTickerPrice().toPlainString());
			}

		}

		log.info("***Stock price based on the last 15 minutes trades");
		/* Calculating the Stock Price based in the Trades made in the 
		 * last 15 minutes */
		Calendar time = Calendar.getInstance();
		time.add(Calendar.MINUTE, -15);
		for (StockBean stock : stockList) {
			BigDecimal stockPrice = tradeDat.getStockPriceInRange(time,
					totalTradeList, stock);
			log.info("The Ticker Price for " + stock.getSymbol() + " is: "
					+ stockPrice);
		}

		log.info("***All Share Index");
		/* Calculating the GBCE All Share Index using the geometric mean 
		 * of prices for all stocks */
		BigDecimal shareIndex=tradeDat.getAllShareIndex(stockList);
		log.info("The All Share Index Value is: "+shareIndex.toPlainString());

	}

}
