/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var map; //contenedor de capas y componentes
var points, heatmap;
var pointsOverlay, heatmapOverlay; //Grupos de capas para puntos / heatmap
var vectorLayer, iconFeature, popup;
var container, content, closer; // contenedores (divs) para el popup (mapa de puntos)

$(function () {
    
    //Divisiones para popup (configuradas con estilos)
    container = document.getElementById('popup');
    content = document.getElementById('popup-content');
    closer = document.getElementById('popup-closer');

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
    
    /*
     * Adicion de dialogo (popup)
     */
    closer.onclick = function () {
        container.style.display = 'none';
        closer.blur();
        return false;
    };
    popup = new ol.Overlay({
        element: container
    });

    map.addOverlay(popup);


});

function locateAddress() {

    var resultAddress = $('#formInjuries\\:txtResultAddress').val();
    var resultNeighborhood = $('#formInjuries\\:txtResultNeighborhood').val();
    var resultCommune = $('#formInjuries\\:txtResultCommune').val();
    var resultLongitude = parseFloat($('#formInjuries\\:txtResultLongitude').val());
    var resultLatitude = parseFloat($('#formInjuries\\:txtResultLatitude').val());

    map.removeLayer(vectorLayer);

    if (resultLongitude !== 0 && resultLatitude !== 0) {

        iconFeature = new ol.Feature({
            geometry: new ol.geom.Point([resultLongitude, resultLatitude]),
        });

        vectorLayer = new ol.layer.Vector({
            source: new ol.source.Vector({
                features: [iconFeature]
            }),
            style: new ol.style.Style({
                image: new ol.style.Icon({
                    anchor: [190, 370],
                    anchorXUnits: 'pixels',
                    anchorYUnits: 'pixels',
                    src: './img/location_icon.png',
                    scale: 0.1
                })
            })
        });
        var info = ''
                    +'<b>Direccion:</b>  ' + resultAddress + '<br>'
                    +'<b>Barrio:</b>  '+ resultNeighborhood + '<br>'
                    +'<b>Comuna:</b>  '+ resultCommune + '<br>'
                    +'<b>Longitud:</b>  '+ resultLongitude + '<br>'
                    +'<b>Latitud:</b>  '+ resultLatitude + '<br>';
        popup.setPosition([resultLongitude, resultLatitude]);
        content.innerHTML = info;
        container.style.display = 'block';
        container.style.bottom= '24px';
        
        map.addLayer(vectorLayer);
        map.getView().setCenter([resultLongitude, resultLatitude]);
        map.getView().setZoom(17);
        
    }else{
        container.style.display = 'none';
        closer.blur();
    }
}
