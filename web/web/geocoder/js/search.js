/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var map; //contenedor de capas y componentes
var points, heatmap;
var pointsOverlay, heatmapOverlay; //Grupos de capas para puntos / heatmap

$(function () {

    var bmapsRoads = new ol.layer.Tile({
        title: 'BingMaps Roads',
        source: new ol.source.BingMaps({
            key: 'AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn',
            imagerySet: 'Road'
        }),
        visible: true
    });
    /*Creacion capa base Bing Maps Aerial*/
    var bmapsAerial = new ol.layer.Tile({
        title: 'BingMaps Aerial',
        source: new ol.source.BingMaps({
            key: 'AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn',
            imagerySet: 'AerialWithLabels'}),
        visible: false
    });
    /*Creacion capa base de OSM*/
    var osmLayer = new ol.layer.Tile({
        title: 'OpenStreetMaps',
        source: new ol.source.OSM(),
        visible: false
    });

    /**
     * GRUPOS PARA LAYERSWITCHER
     * @type ol.layer.Group
     */
    var baseMaps = new ol.layer.Group({
        title: 'Mapas Base',
        layers: [osmLayer, bmapsRoads, bmapsAerial]
    });

    /**
     * MAPA
     * @type ol.Map
     */
    map = new ol.Map({
        target: 'map', // The DOM element that will contains the map

        controls: ol.control.defaults({
            attributionOptions: ({
                collapsible: false
            })
        }).extend([
            new ol.control.ZoomToExtent({
                extent: [
                    -8605605, 137132,
                    -8599605, 132374
                ]
            }),
            new ol.control.ScaleLine(),
            //new ol.control.FullScreen(),
            new ol.control.MousePosition({
                coordinateFormat: ol.coordinate.createStringXY(4),
                projection: 'EPSG:3857'
            })
        ]),
        renderer: 'canvas', // Force the renderer to be used
        layers: [baseMaps],
        // Create a view centered on the specified location and zoom level
        view: new ol.View({
            center: [-8602509.5692, 134983.3435],
            zoom: 14,
            maxZoom: 18,
            minZoom: 14
        })
    });

    /**
     * ADICION DEL CONTROL LAYERSWITCHER
     * @type ol.control.LayerSwitcher
     */
    var layerSwitcher = new ol.control.LayerSwitcher();
    map.addControl(layerSwitcher);

    setInterval(function () {
        map.updateSize();
    }, 2);

});
