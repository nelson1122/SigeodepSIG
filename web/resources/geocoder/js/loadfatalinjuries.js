/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
window.onload = loadmap;

function loadmap() {

    var map = new OpenLayers.Map('map');
    var categories = ["general", "homicidios", "suicidios", "accidentales", "transito"];

    var gmaps = new OpenLayers.Layer.Google(
            "Google Hybrid",
            {
                type: google.maps.MapTypeId.HYBRID,
                numZoomLevels: 20
            }
    );
    var osm = new OpenLayers.Layer.OSM();
    
    var layers = [osm, gmaps];

    for (var i = 0; i < categories.length; i++) {
        layers.push(
                new OpenLayers.Layer.Vector(categories[i].toString(), {
                    projection: new OpenLayers.Projection("EPSG:3857"),
                    strategies: [new OpenLayers.Strategy.Fixed()],
                    protocol: new OpenLayers.Protocol.HTTP({
                        url: "web/data.jsp?category="+categories[i],
                        format: new OpenLayers.Format.GeoJSON()
                    }),
                    eventListeners: {
                        "featuresadded": function () {
                            map.zoomToExtent(this.getDataExtent());
                        }
                    }
                })
            );
    }
    
    map.addLayers(layers);
    map.addControl(new OpenLayers.Control.LayerSwitcher());

//    map.setCenter(new OpenLayers.LonLat(-77.280, 1.21489).transform(
//            new OpenLayers.Projection("EPSG:4326"),
//            map.getProjectionObject()
//            ), 14);

};



