package sd;

import java.util.HashMap;

public class Data {

	int numberOfStores;
	int numberOfItems;
	int numberOfDays;
	double capacityOfTransportation;
	double interestRate;

	Store[] Store;
	Item[] Item;
	int[] day;
	HashMap<Item, HashMap<Store, HashMap<Integer, Double>>> demandArray;

}