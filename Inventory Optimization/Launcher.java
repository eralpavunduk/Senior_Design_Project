package sd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import gurobi.GRBException;

public class Launcher {

	public static void main(String[] args) throws GRBException {
		Data data = ReadData();
		Optimizer optimizer = new Optimizer(data);

		optimizer.addConstraint1();
		optimizer.addConstraint2();
		optimizer.addConstraint3(data.capacityOfTransportation);
		optimizer.addConstraint4();
		optimizer.addConstraint5();
		optimizer.addConstraint6();
		//optimizer.addConstraint7();
		optimizer.addObjective();
		optimizer.Result();

	}

	private static Data ReadData() {
		Data data = new Data();
		
		Item[] item;
		Store[] store;
		int[] day;
		HashMap<Item, HashMap<Store, HashMap<Integer, Double>>> demand;
		double[] demandArray;
		double capacityOfTransportation;
		double itemInterestRate;

		try {

			File folder = new File("Demands_And_Parameters");
			File[] listOfFiles = folder.listFiles();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(listOfFiles[0]));

			String line = bufferedReader.readLine();
			String[] itemId = line.split(",");
			data.numberOfItems = itemId.length - 1;

			item = new Item[data.numberOfItems];

			for (int i = 0; i < data.numberOfItems; i++) {
				item[i] = new Item(itemId[i + 1]);
			}

			line = bufferedReader.readLine();
			String[] volumeCoef = line.split(",");
			data.numberOfItems = volumeCoef.length - 1;

			for (int i = 0; i < data.numberOfItems; i++) {
				item[i].setVolumeCoef(Double.parseDouble(volumeCoef[i + 1]));
			}

			line = bufferedReader.readLine();
			String[] productionCost = line.split(",");
			for (int i = 0; i < data.numberOfItems; i++) {
				item[i].setProductionCost(Double.parseDouble(productionCost[i + 1]));
			}

			line = bufferedReader.readLine();
			String[] storeId = line.split(",");
			data.numberOfStores = storeId.length - 1;

			store = new Store[data.numberOfStores];
			for (int i = 0; i < data.numberOfStores; i++) {
				store[i] = new Store(storeId[i + 1]);
			}

			line = bufferedReader.readLine();
			String[] capacityOfStore = line.split(",");
			data.numberOfStores = capacityOfStore.length - 1;

			for (int i = 0; i < data.numberOfStores; i++) {
				store[i].setCapacityOfStore(Double.parseDouble(capacityOfStore[i + 1]));
			}

			line = bufferedReader.readLine();
			String[] transportationCost = line.split(",");
			data.numberOfStores = transportationCost.length - 1;

			for (int i = 0; i < data.numberOfStores; i++) {
				store[i].setTransportationCost(Double.parseDouble(transportationCost[i + 1]));
			}

			line = bufferedReader.readLine();
			String[] capacityofTransportation = line.split(",");
			capacityOfTransportation = Double.parseDouble(capacityofTransportation[1]);

			line = bufferedReader.readLine();
			String[] dayId = line.split(",");
			data.numberOfDays = dayId.length - 1;

			day = new int[data.numberOfDays];
			for (int i = 0; i < data.numberOfDays; i++) {
				day[i] = i + 1;
			}

			line = bufferedReader.readLine();
			String[] interestRate = line.split(",");
			itemInterestRate = Double.parseDouble(interestRate[1]);

			demand = new HashMap<Item, HashMap<Store, HashMap<Integer, Double>>>();

			for (int f = 1; f < listOfFiles.length;) {

				for (Item it : item) {

					HashMap<Store, HashMap<Integer, Double>> storeMap = new HashMap<Store, HashMap<Integer, Double>>();

					for (Store s : store) {

						HashMap<Integer, Double> dayMap = new HashMap<Integer, Double>();

						BufferedReader bufferedReader2 = new BufferedReader(new FileReader(listOfFiles[f]));
						String lineitem = bufferedReader2.readLine();
						String[] demandList = lineitem.split(",");
						int demandLength = demandList.length - 1;
						demandArray = new double[demandLength];
						for (int i = 0; i < demandLength; i++) {
							demandArray[i] = Double.parseDouble(demandList[i + 1]);
						}

						for (int d : day) {
							double demandAmount = demandArray[d - 1];
							dayMap.put(d, demandAmount);
						}

						storeMap.put(s, dayMap);
						bufferedReader2.close();
						f++;

					}

					demand.put(it, storeMap);

				}

			}

			data.Item = item;
			data.Store = store;
			data.day = day;
			data.demandArray = demand;
			data.capacityOfTransportation = capacityOfTransportation;
			data.interestRate = itemInterestRate;

		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

		return data;

	}

}
