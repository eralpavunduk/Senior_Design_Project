package sd;

import java.util.HashMap;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class Optimizer {

	GRBEnv env;
	GRBModel model;

	HashMap<Item, HashMap<Store, HashMap<Integer, GRBVar>>> X = new HashMap<Item, HashMap<Store, HashMap<Integer, GRBVar>>>();
	HashMap<Store, HashMap<Integer, GRBVar>> Y = new HashMap<Store, HashMap<Integer, GRBVar>>();
	HashMap<Item, HashMap<Store, HashMap<Integer, GRBVar>>> I = new HashMap<Item, HashMap<Store, HashMap<Integer, GRBVar>>>();
	HashMap<Item, HashMap<Store, HashMap<Integer, GRBVar>>> B = new HashMap<Item, HashMap<Store, HashMap<Integer, GRBVar>>>();

	public Data data;

	public Optimizer(Data data) throws GRBException {

		this.data = data;

		env = new GRBEnv();
		model = new GRBModel(env);

		for (Item item : this.data.Item) {
			HashMap<Store, HashMap<Integer, GRBVar>> storeMap = new HashMap<Store, HashMap<Integer, GRBVar>>();
			for (Store store : this.data.Store) {
				HashMap<Integer, GRBVar> dayMap = new HashMap<Integer, GRBVar>();
				for (int day : data.day) {
					GRBVar Xijk = this.model.addVar(0, 6480, 0, GRB.INTEGER,
							"X_" + item.itemId + "_" + store.storeId + "_" + day);
					dayMap.put(day, Xijk);
				}

				storeMap.put(store, dayMap);

			}

			X.put(item, storeMap);

		}

		for (Store store : this.data.Store) {
			HashMap<Integer, GRBVar> dayMap = new HashMap<Integer, GRBVar>();
			for (int day : data.day) {
				GRBVar Yjk = this.model.addVar(0, 3, 0, GRB.INTEGER, "Y_" + store.storeId + "_" + day);
				dayMap.put(day, Yjk);
			}

			Y.put(store, dayMap);

		}

		for (Item item : this.data.Item) {
			HashMap<Store, HashMap<Integer, GRBVar>> storeMap = new HashMap<Store, HashMap<Integer, GRBVar>>();
			for (Store store : this.data.Store) {
				HashMap<Integer, GRBVar> dayMap = new HashMap<Integer, GRBVar>();
				for (int day = 0; day < data.day.length + 1; day++) {
					GRBVar Iijk = this.model.addVar(0, 6480, 0, GRB.INTEGER,
							"I_" + item.itemId + "_" + store.storeId + "_" + day);
					dayMap.put(day, Iijk);
				}

				storeMap.put(store, dayMap);

			}

			I.put(item, storeMap);

		}
		for (Item item : this.data.Item) {
			HashMap<Store, HashMap<Integer, GRBVar>> storeMap = new HashMap<Store, HashMap<Integer, GRBVar>>();
			for (Store store : this.data.Store) {
				HashMap<Integer, GRBVar> dayMap = new HashMap<Integer, GRBVar>();
				for (int day = 0; day < data.day.length + 1; day++) {
					GRBVar Bijk = this.model.addVar(0, 6480, 0, GRB.INTEGER,
							"B_" + item.itemId + "_" + store.storeId + "_" + day);
					dayMap.put(day, Bijk);
				}

				storeMap.put(store, dayMap);

			}

			B.put(item, storeMap);

		}

	}

	public void addConstraint1() throws GRBException {

		for (Item item : data.Item) {

			for (Store store : data.Store) {

				this.model.addConstr(I.get(item).get(store).get(0), GRB.EQUAL, 0, "Inventory of Day 0 Constraint");

			}
		}
	}

	public void addConstraint2() throws GRBException {

		GRBLinExpr InventoryConstraint_RHS = new GRBLinExpr();
		GRBLinExpr InventoryConstraint_LHS = new GRBLinExpr();

		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 0; day < data.day.length; day++) {

					InventoryConstraint_LHS.addTerm(1, I.get(item).get(store).get(day + 1));
					InventoryConstraint_RHS.addTerm(1, X.get(item).get(store).get(day + 1));
					InventoryConstraint_RHS.addConstant(-data.demandArray.get(item).get(store).get(day + 1));
					InventoryConstraint_RHS.addTerm(1, I.get(item).get(store).get(day));
					InventoryConstraint_RHS.addTerm(1, B.get(item).get(store).get(day + 1));
					InventoryConstraint_RHS.addTerm(-1, B.get(item).get(store).get(day));

					this.model.addConstr(InventoryConstraint_LHS, GRB.EQUAL, InventoryConstraint_RHS,
							"Inventory Balance Constraint");

				}
			}
		}
	}

	public void addConstraint3(double capacityofTransportation) throws GRBException {

		for (Store store : data.Store) {

			for (int day = 1; day < data.day.length + 1; day++) {

				GRBLinExpr TransportationCapacityConstraint_RHS = new GRBLinExpr();
				GRBLinExpr TransportationCapacityConstraint_LHS = new GRBLinExpr();

				for (Item item : data.Item) {

					TransportationCapacityConstraint_LHS.addTerm(item.getVolumeCoef(), X.get(item).get(store).get(day));

				}

				TransportationCapacityConstraint_RHS.addTerm(capacityofTransportation, Y.get(store).get(day));

				this.model.addConstr(TransportationCapacityConstraint_LHS, GRB.LESS_EQUAL,
						TransportationCapacityConstraint_RHS, "Transportation Capacity Constraint");

			}
		}
	}

	public void addConstraint4() throws GRBException {

		for (Store store : data.Store) {

			for (int day = 0; day < data.day.length; day++) {

				GRBLinExpr StoreCapacityConstraint_LHS = new GRBLinExpr();

				for (Item item : data.Item) {

					StoreCapacityConstraint_LHS.addTerm(item.getVolumeCoef(), X.get(item).get(store).get(day + 1));
					StoreCapacityConstraint_LHS.addTerm(item.getVolumeCoef(), I.get(item).get(store).get(day));

				}

				this.model.addConstr(StoreCapacityConstraint_LHS, GRB.LESS_EQUAL, store.getCapacityOfStore(),
						"Store Capacity Constraint");
			}
		}
	}

	public void addConstraint5() throws GRBException {
		for (Item item : data.Item) {

			for (Store store : data.Store) {

				this.model.addConstr(B.get(item).get(store).get(0), GRB.EQUAL, 0, "Backorder of Day 0 Constraint");
			}
		}

	}

	public void addConstraint6() throws GRBException {
		for (Item item : data.Item) {

			for (Store store : data.Store) {

				this.model.addConstr(B.get(item).get(store).get(30), GRB.EQUAL, 0, "Backorder of Day 30 Constraint");
			}
		}

	}
	/*public void addConstraint7() throws GRBException {
	for (Store store : data.Store) {
		for (int i = 1; i < data.day.length+1; i++) {
			if (i  == 1 || i==8 || i==15 || i==22 || i==29) {
			
			GRBLinExpr TransportationCapacityConstraint_LHS = new GRBLinExpr();
			
			TransportationCapacityConstraint_LHS.addTerm(1, Y.get(store).get(i));
			
			this.model.addConstr(TransportationCapacityConstraint_LHS, GRB.EQUAL,
					1, "Transportation Capacity Constraint");
			}
			else {
				
				GRBLinExpr TransportationCapacityConstraint_LHS = new GRBLinExpr();
				
				TransportationCapacityConstraint_LHS.addTerm(1, Y.get(store).get(i));
				
				this.model.addConstr(TransportationCapacityConstraint_LHS, GRB.EQUAL,
						0, "Transportation Capacity Constraint");
			}
		}
	}
	
}*/

	public void addObjective() throws GRBException {

		GRBLinExpr Obj = new GRBLinExpr();

		for (Store store : data.Store) {

			for (int day = 1; day < data.day.length + 1; day++) {

				Obj.addTerm(store.getTransportationCost(), Y.get(store).get(day));

			}
		}

		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 1; day < data.day.length + 1; day++) {

					Obj.addTerm(item.getProductionCost() * (data.interestRate), I.get(item).get(store).get(day));

				}
			}
		}

		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 1; day < data.day.length + 1; day++) {

					Obj.addTerm(2, B.get(item).get(store).get(day));

				}
			}
		}

		model.setObjective(Obj, GRB.MINIMIZE);

		model.optimize();

	}

	public void Result() throws GRBException {


		System.out.println("item_id:" + "   \t" + "store_id:" + "   \t" + "day:" + "   \t" + "demand:" + "   \t"
				+ "X_ijk" + "   \t" + "Y_jk" + "   \t" + "I_ijk" + "   \t" + "B_ijk");

		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 1; day < data.day.length + 1; day++) {

					System.out.println(item.getId() + "\t       " + store.getId() + "     \t" + day + "    \t"
							+ data.demandArray.get(item).get(store).get(day) + "    \t"
							+ X.get(item).get(store).get(day).get(GRB.DoubleAttr.X) + "   \t"
							+ Y.get(store).get(day).get(GRB.DoubleAttr.X) + "   \t"
							+ I.get(item).get(store).get(day).get(GRB.DoubleAttr.X) + "   \t"
							+ B.get(item).get(store).get(day).get(GRB.DoubleAttr.X));

				}
			}
		}
		double transportation_cost = 0;
		for (Store store : data.Store) {

			for (int day = 1; day < data.day.length + 1; day++) {
				double count = Y.get(store).get(day).get(GRB.DoubleAttr.X) * store.getTransportationCost();
				transportation_cost = transportation_cost + count;

			}
		}
		System.out.println("Transportation cost = " + transportation_cost);

		double holding_cost = 0;
		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 1; day < data.day.length + 1; day++) {
					double count = I.get(item).get(store).get(day).get(GRB.DoubleAttr.X) * item.getProductionCost()
							* 0.14;
					holding_cost = holding_cost + count;
				}
			}
		}
		System.out.println("Holding cost = " + holding_cost);

		double backorder_cost = 0;
		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 1; day < data.day.length + 1; day++) {
					double count = B.get(item).get(store).get(day).get(GRB.DoubleAttr.X) * 2;
					backorder_cost = backorder_cost + count;
				}
			}
		}
		System.out.println("Backorder cost = " + backorder_cost);

		double count_of_vehicle = 0;
		for (Store store : data.Store) {

			for (int day = 1; day < data.day.length + 1; day++) {
				double count = Y.get(store).get(day).get(GRB.DoubleAttr.X);
				count_of_vehicle = count_of_vehicle + count;

			}
		}
		System.out.println("Count of vehicle = " + count_of_vehicle);

		double count_of_holding_item = 0;
		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 1; day < data.day.length + 1; day++) {
					double count = I.get(item).get(store).get(day).get(GRB.DoubleAttr.X);
					count_of_holding_item = count_of_holding_item + count;
				}
			}
		}
		System.out.println("Number of held item = " + count_of_holding_item);

		double count_of_backorder_item = 0;
		for (Item item : data.Item) {

			for (Store store : data.Store) {

				for (int day = 1; day < data.day.length + 1; day++) {
					double count = B.get(item).get(store).get(day).get(GRB.DoubleAttr.X);
					count_of_backorder_item = count_of_backorder_item + count;
				}
			}
		}
		System.out.println("Number of backordered item = " + count_of_backorder_item);


		model.dispose();
		env.dispose();

	}

}
