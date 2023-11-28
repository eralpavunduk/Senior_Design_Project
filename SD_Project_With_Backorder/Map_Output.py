import csv
import plotly.graph_objects as go
import sys, getopt



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

     message = "%s<br>%s : Demand: %s , Amount of item recieved : %s , Truck number : %s Inventory : %s , Backorder Amount: %s" % (
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