package sd;

public class Item {

	public double volumeCoef;
	public String itemId;
	public double productionCost;

	Item(String itemId) {
		this.itemId = itemId;
	}

	public void setVolumeCoef(double volumeCoef) {
		this.volumeCoef = volumeCoef;
	}

	public double getVolumeCoef() {
		return this.volumeCoef;
	}

	public void setId(String itemId) {
		this.itemId = itemId;
	}

	public String getId() {
		return this.itemId;

	}

	public void setProductionCost(double productionCost) {
		this.productionCost = productionCost;
	}

	public double getProductionCost() {
		return this.productionCost;
	}
}