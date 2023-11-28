import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from pandas.plotting import register_matplotlib_converters
from statsmodels.tsa.seasonal import STL
import xlrd
import statsmodels.api as sm
import math
import csv
import os
import shutil
import subprocess
import plotly.graph_objects as go
import sys, getopt
import warnings


def fxn():
    warnings.warn("deprecated", DeprecationWarning)

with warnings.catch_warnings():
    warnings.simplefilter("ignore")
    fxn()

warnings.filterwarnings("ignore")


register_matplotlib_converters()
sns.set_style("darkgrid")
plt.rc("figure", figsize=(16, 12))
plt.rc("font", size=13)

os.mkdir("Demands_And_Parameters")

loc = ("SportiveData5.xls")
wb = xlrd.open_workbook(loc)

arraylist = [[0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0]]
# Number of stores
for x in range(15):
    sheet = wb.sheet_by_index(x + 2)
    # Number of items in store x
    for y in range(8):
        arraylist[x][y] = sheet.col_values(y + 2, start_rowx=4, end_rowx=197)

data1 = []
data2 = []
data3 = []
data4 = []
data5 = []
data6 = []
data7 = []
data8 = []
data9 = []

sheet = wb.sheet_by_index(2)
Coefficients_And_Parameters_Name = str(sheet.cell(0, 0))
Coefficients_And_Parameters_Name_str = Coefficients_And_Parameters_Name.replace('text:', '')
with open(f'{Coefficients_And_Parameters_Name_str}.csv', 'w', encoding='UTF8', newline='') as f:
    writer = csv.writer(f)

    writer.writerow(data1)
    writer.writerow(data2)
    writer.writerow(data3)
    writer.writerow(data4)
    writer.writerow(data5)
    writer.writerow(data6)
    writer.writerow(data7)
    writer.writerow(data8)
    writer.writerow(data9)

source = f'{Coefficients_And_Parameters_Name_str}.csv'
destination = 'Demands_And_Parameters'
shutil.move(source, destination)

exoglist = [[0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0]]


for x in range(15):
    for y in range(8):
        data = arraylist[x][y]
        data = pd.Series(
            data, index=pd.date_range("1-1-2018", periods=len(data), freq="W"), name="DATA"
        )
        data.describe()
        stl = STL(data, seasonal=51)
        res = stl.fit()



        def add_stl_plot(fig, res, legend):
            """Add 3 plots from a second STL fit"""
            axs = fig.get_axes()
            comps = ["trend", "seasonal", "resid"]
            for ax, comp in zip(axs[1:], comps):
                series = getattr(res, comp)
                if comp == "resid":
                    ax.plot(series, marker="o", linestyle="none")
                else:
                    ax.plot(series)
                    if comp == "trend":
                        ax.legend(legend, frameon=False)


        stl = STL(data, period=51, robust=True)
        res_robust = stl.fit()

        res_non_robust = STL(data, period=51, robust=False).fit()

        exoglist[x][y] = res_robust.resid.values

        exogdata = exoglist[x][y]
        exogdata = pd.Series(
            exogdata, index=pd.date_range("1-1-2018", periods=len(exogdata), freq="W"), name="EXOGDATA"
        )

        model = sm.tsa.statespace.SARIMAX(data, exogdata=193, order=(1, 1, 1), seasonal_order=(2, 1, 1, 52))
        results = model.fit(disp=0)
        forecast = results.predict(start=193, end=198, dynamic=True)



        weeklydemands = [0, 0, 0, 0, 0]
        for z in range(5):
            weeklydemands[z] = math.ceil(forecast.get(z))


        dailydemands = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        count = 0
        for m in range(5):
            for n in range(5):
                if n + count < 30:
                    a = math.ceil(weeklydemands[m] * 0.12)
                    dailydemands[n + count] = a
            count = count + 5
            for k in range(2):
                if n + count < 32:
                    b = math.ceil(weeklydemands[m] * 0.2)
                    dailydemands[k + count] = b
            count = count + 2

        sheet = wb.sheet_by_index(x + 2)
        item_name = str(sheet.cell(0, 1))
        store_name = str(sheet.cell(3, y + 2))
        item_name_str = item_name.replace('text:', '')
        store_name_str = store_name.replace('text:', '')

        dailydata = [f'{item_name_str}', dailydemands[0], dailydemands[1], dailydemands[2], dailydemands[3],
                     dailydemands[4], dailydemands[5], dailydemands[6], dailydemands[7], dailydemands[8],
                     dailydemands[9], dailydemands[10], dailydemands[11], dailydemands[12], dailydemands[13],
                     dailydemands[14], dailydemands[15], dailydemands[16], dailydemands[17], dailydemands[18],
                     dailydemands[19], dailydemands[20], dailydemands[21], dailydemands[22], dailydemands[23],
                     dailydemands[24], dailydemands[25], dailydemands[26], dailydemands[27], dailydemands[28],
                     dailydemands[29]]

        with open(f'{item_name_str}_{store_name_str}.csv', 'w', encoding='UTF8') as f:
            writer = csv.writer(f)
            # write the data
            writer.writerow(dailydata)

        source = f'{item_name_str}_{store_name_str}.csv'
        destination = 'Demands_And_Parameters'
        shutil.move(source, destination)

res = subprocess.Popen('java -jar InventoryManagement_With_Backorder_2_INTEGER_4.jar & java -jar InventoryManagement_With_Backorder_2_INTEGER_4.jar > result.csv', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

for line in res.stdout.readlines():
    line = str(line)
res.stdout.close()



while True:
 stores = {
     "AQUA_FLORYA_MAGAZA":
         {"name": "AQUA_FLORYA_MAGAZA", "lat": 40.96645310500456, "lon": 28.79741698421447,
          "message": ""},

     "B.DUZU_SPORTIVE_OUTLET_MAGAZA":
         {"name": "B.DUZU_SPORTIVE_OUTLET_MAGAZA", "lat": 41.00662832817044, "lon": 28.680365838832873,
          "message": ""},

     "BAYRAMPASA_FORUM_MAGAZA": {"name": "BAYRAMPASA_FORUM_MAGAZA", "lat": 41.046333291669576, "lon": 28.89793532839253,
                                 "message": ""},

     "BEYLIKDUZU_REGULE_MAGAZA": {"name": "BEYLIKDUZU_REGULE_MAGAZA", "lat": 41.00657975010739,
                                  "lon": 28.681299247422963, "message": ""},

     "HILLTOWN_MAGAZA": {"name": "HILLTOWN_MAGAZA", "lat": 40.95319562587188, "lon": 29.121460028389528,
                         "message": ""},

     "ISFANBUL_MAGAZA": {"name": "ISFANBUL_MAGAZA", "lat": 41.07583622397867, "lon": 28.924607797712643,
                         "message": ""},

     "MALL_OF_MAGAZA": {"name": "MALL_OF_MAGAZA", "lat": 41.062826458161645, "lon": 28.806302540042058,
                        "message": ""},

     "OZDILEK_MAGAZA": {"name": "OZDILEK_MAGAZA", "lat": 41.07741465820449, "lon": 29.010882899558407,
                        "message": ""},
  }

 mapbox_access_token = "pk.eyJ1IjoiZW1yYWhzaW1zZWsiLCJhIjoiY2wxNmlwNGFuMDJhZjNjcDkydGQ1MWV2cCJ9.q_HtwToHpSvk1BXTHKP7aw"
 itemCode = ''
 lats = []
 lons = []
 messages = []


 def main(argv):
     global itemCode

     try:
         opts, args = getopt.getopt(argv, "", ["item="])
     except getopt.GetoptError:
         sys.exit(2)
     for opt, arg in opts:
         if opt == '--item':
             if arg == '':
                 itemCode = arg

     # eğer parametre girilmemişs
     if itemCode == '':
         itemCode = input("Enter Item Id : ")

     readCSVFile()

     for key in stores:
         store = stores[key]
         lats.append(store.get('lat'))
         lons.append(store.get('lon'))
         messages.append(store.get('message'))

     fig = go.Figure(go.Scattermapbox(
         lat=lats,
         lon=lons,
         mode='markers',
         marker=go.scattermapbox.Marker(
             size=20,
         ),
         text=messages,
     ))

     fig.update_layout(
         autosize=True,
         hovermode='closest',
         mapbox=dict(
             accesstoken=mapbox_access_token,
             bearing=0,
             center=dict(
                 lat=41.00527,
                 lon=28.98696
             ),
             pitch=0,
             zoom=10
         ),
     )

     fig.show()


 def parseLine(str):
     arr = str.split('\t')
     return {
         'itemid': arr[0].strip(),
         'storeid': arr[1].strip(),
         'day': arr[2].strip(),
         'demand': arr[3].strip(),
         'x': arr[4].strip(),
         'y': arr[5].strip(),
         'l': arr[6].strip(),
         'b': arr[7].strip(),
     }


 def updateStoreMessage(values):
     store = stores[values.get('storeid')]
     message = store.get('message')
     if message == '':
         message = "%s - %s" % (store.get('name'), values.get('itemid'))

     message = "%s<br>%s : Demand: %s , Recieved : %s , Truck : %s Inventory : %s , Backorder : %s" % (
         message, values.get('day'), values.get('demand'), values.get('x'), values.get('y'), values.get('l'), values.get('b'))
     store.update({"message": message})


 def readCSVFile():
     started = False

     with open('result.csv', mode='r') as csv_file:
         csv_reader = csv.reader(csv_file)
         line_count = 0
         for row in csv_reader:
             if (row):
                 if started != True and row[0].find('item') == 0:
                     started = True
                     line_count += 1
                 else:
                     if (started == True):
                         values = parseLine(row[0])
                         print(values)

                         if values.get('itemid') == itemCode:
                             updateStoreMessage(values)
                 line_count = line_count + 1


 if __name__ == "__main__":
     main(sys.argv[1:])