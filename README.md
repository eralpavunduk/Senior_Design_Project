# IE - 402 SENIOR DESIGN PROJECT (2022)
With the demand forecasting methods currently used by Sportive Company, they cannot reach the results that work and are satisfied in their stores. The demand forecasting problem also negatively affects the inventory management of the company's stores.
Based on these problems, we developed a solution for demand forecasting and inventory management.

For the forecast, we decided to use the method which helped our project the most in terms of margin of error and convenience: STL Decomposition

The programming languages we used to do inventory management(java) ,forecast(python v:3.7): Eclipse , Python

The solver we use to solve our mathematical model optimally: Gurobi

To store and read our data: Excel

In order to be able to run our working model in Eclipse on any computer without an IDE, we can run the "jar" format, which we have solved with the data inside of the jar file, with the command line.
[" CMD => java -jar .jar "]

JAVA

Launcher: Main method is in this class.

Optimizer: Computation method is in this class.

Item: Item class contains item id's and coefficients.

Store: Store class includes transportation costs, capacity of stores and store id's.

Data: This class combines all the problem datas.

PYTHON

main.py: Imports, data reading (from Excel), RobustSTL Decomposition, SARIMAX, running and executing jar file and mapping are all in this file.

Map_Output.py: If Demands_And_Paramters folder and result.csv are created by main.py, you can execute this file without forcasting, running and executing jar file to see results on map.

Item IDs are:...

If it is desired, multiple items can be observed at the same time by entering "itemID,itemID,...,itemID" to the input.

Credits

Onuralp Aslaner,
Ahmet Eralp Avunduk,
Oytun Sabri Avunduk,
Derviş Murat Bahçenli,
İrem Kuş,
Alp Tolga Yakıncı
:
