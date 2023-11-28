package sd;

public class Store {

	public double transportationCost;
	public double capacityOfStore;
	public String storeId;

	Store(String storeId) {
		this.storeId = storeId;
	}

	public String getId() {
		return this.storeId;
	}

	public void setTransportationCost(double transportationCost) {
		this.transportationCost = transportationCost;
	}

	public double getTransportationCost() {
		return this.transportationCost;
	}

	public void setCapacityOfStore(double storeCapacity) {
		this.capacityOfStore = storeCapacity;
	}

	public double getCapacityOfStore() {
		return this.capacityOfStore;
	}

}